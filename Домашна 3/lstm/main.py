import os
import json
import joblib
import numpy as np
import pandas as pd
import torch
import torch.nn as nn
import torch.optim as optim
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from sqlalchemy import create_engine
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import mean_squared_error, mean_absolute_percentage_error, r2_score
from fastapi import Query
from dotenv import load_dotenv
import uvicorn
import traceback

load_dotenv("../../.env")

app = FastAPI()

db = os.getenv("DB_NAME")
user = os.getenv("DB_USER")
password = os.getenv("DB_PASSWORD")
host = os.getenv("DB_HOST")
port = os.getenv("DB_PORT")

DB_CONNECTION_STRING = f"postgresql://{user}:{password}@{host}:{port}/{db}"
engine = create_engine(DB_CONNECTION_STRING)

# Params
LOOKBACK_WINDOW = 60
TRAIN_SPLIT = 0.70
HIDDEN_SIZE = 128
INPUT_FEATURES = 5
# Folder to save models
MODEL_DIR = "models"

if not os.path.exists(MODEL_DIR):
    os.makedirs(MODEL_DIR)


class CryptoLSTM(nn.Module):
    def __init__(self, input_size=5, hidden_size=64, output_size=1):
        super(CryptoLSTM, self).__init__()
        self.hidden_size = hidden_size

        self.lstm = nn.LSTM(input_size, hidden_size, batch_first=True)

        self.fc_layers = nn.Sequential(
            nn.Linear(hidden_size, 64),
            nn.ReLU(),
            nn.Linear(64, output_size)
        )

    def forward(self, x):
        out, _ = self.lstm(x)
        out = out[:, -1, :]
        out = self.fc_layers(out)
        return out


class PredictionResponse(BaseModel):
    symbol: str
    predicted_next_close_price: float
    metrics: dict
    training_info: str


def get_data_from_db(symbol: str):
    query = f"""
        SELECT open, high, low, close, volume, timestamp 
        FROM market_data 
        WHERE symbol = '{symbol}' 
        ORDER BY timestamp ASC
    """
    df = pd.read_sql(query, engine)

    return df


def create_dataset(X_data, y_data, look_back):
    dataX, dataY = [], []
    for i in range(len(X_data) - look_back):
        a = X_data[i:(i + look_back), :]
        dataX.append(a)
        dataY.append(y_data[i + look_back])
    return np.array(dataX), np.array(dataY)

def save_state(symbol, model, scaler_features, scaler_target, last_date):
    torch.save(model.state_dict(), os.path.join(MODEL_DIR, f"{symbol}.pth"))
    joblib.dump(scaler_features, os.path.join(MODEL_DIR, f"{symbol}_scaler_X.save"))
    joblib.dump(scaler_target, os.path.join(MODEL_DIR, f"{symbol}_scaler_Y.save"))

    meta = {"last_trained_date": str(last_date)}
    with open(os.path.join(MODEL_DIR, f"{symbol}_meta.json"), "w") as f:
        json.dump(meta, f)


def load_metadata(symbol):
    path = os.path.join(MODEL_DIR, f"{symbol}_meta.json")
    if os.path.exists(path):
        with open(path, "r") as f:
            return json.load(f).get("last_trained_date")
    return None


@app.get("/api/predict", response_model=PredictionResponse)
def predict_price(symbol: str = Query()):
    symbol = symbol.upper()

    model_path = os.path.join(MODEL_DIR, f"{symbol}.pth")
    scaler_X_path = os.path.join(MODEL_DIR, f"{symbol}_scaler_X.save")

    try:
        df = get_data_from_db(symbol)
        latest_db_date = str(df['timestamp'].max())

        last_trained_date = load_metadata(symbol)

        model = CryptoLSTM(input_size=INPUT_FEATURES, hidden_size=HIDDEN_SIZE)
        scaler_features = None
        scaler_target = None
        info_msg = ""
        is_fine_tuning = False

        if os.path.exists(model_path) and last_trained_date == latest_db_date:
            model.load_state_dict(torch.load(model_path))
            scaler_features = joblib.load(scaler_X_path)
            scaler_target = joblib.load(os.path.join(MODEL_DIR, f"{symbol}_scaler_Y.save"))
            info_msg = "Cached"
            model.eval()

        elif os.path.exists(model_path) and last_trained_date != latest_db_date:
            model.load_state_dict(torch.load(model_path))
            scaler_features = joblib.load(scaler_X_path)
            scaler_target = joblib.load(os.path.join(MODEL_DIR, f"{symbol}_scaler_Y.save"))

            is_fine_tuning = True
            info_msg = "Cached + Trained"

        else:
            scaler_features = MinMaxScaler(feature_range=(0, 1))
            scaler_target = MinMaxScaler(feature_range=(0, 1))
            info_msg = "Trained"

        raw_X = df[['open', 'high', 'low', 'close', 'volume']].values.astype('float32')
        raw_Y = df[['close']].values.astype('float32')

        split_idx = int(len(raw_X) * TRAIN_SPLIT)

        # 2. Scaler Logic
        if info_msg == "Trained":
            scaler_features = MinMaxScaler(feature_range=(0, 1))
            scaler_target = MinMaxScaler(feature_range=(0, 1))

            scaler_features.fit(raw_X[:split_idx])
            scaler_target.fit(raw_Y[:split_idx])

            X_scaled = scaler_features.transform(raw_X)
            y_scaled = scaler_target.transform(raw_Y)

        elif info_msg == "Cached + Trained":
            X_scaled = scaler_features.transform(raw_X)
            y_scaled = scaler_target.transform(raw_Y)
        else:
            X_scaled = scaler_features.transform(raw_X)
            y_scaled = scaler_target.transform(raw_Y)

        X, y = create_dataset(X_scaled, y_scaled, LOOKBACK_WINDOW)


        train_size = int(len(X) * TRAIN_SPLIT)

        X_train = X[:train_size]
        X_test = X[train_size:]
        y_train = y[:train_size]
        y_test = y[train_size:]

        if info_msg != "Cached":
            X_train_t = torch.from_numpy(X_train).float()
            y_train_t = torch.from_numpy(y_train).float()

            criterion = nn.MSELoss()

            if is_fine_tuning:
                optimizer = optim.Adam(model.parameters(), lr=0.0001)
                epochs = 5
            else:
                model = CryptoLSTM(input_size=INPUT_FEATURES, hidden_size=HIDDEN_SIZE)
                optimizer = optim.Adam(model.parameters(), lr=0.001)
                epochs = 25

            model.train()
            for epoch in range(epochs):
                optimizer.zero_grad()
                output = model(X_train_t)
                loss = criterion(output, y_train_t)
                loss.backward()
                optimizer.step()
                print(loss.item())

            save_state(symbol, model, scaler_features, scaler_target, latest_db_date)
            model.eval()

        X_test_t = torch.from_numpy(X_test).float()

        with torch.no_grad():
            test_preds_scaled = model(X_test_t).numpy()

            test_preds = scaler_target.inverse_transform(test_preds_scaled)
            y_test_real = scaler_target.inverse_transform(y_test)

            rmse = np.sqrt(mean_squared_error(y_test_real, test_preds))
            mape = mean_absolute_percentage_error(y_test_real, test_preds)
            r2 = r2_score(y_test_real, test_preds)

            last_window_scaled = X_scaled[-LOOKBACK_WINDOW:]
            last_window_t = torch.from_numpy(last_window_scaled).float().unsqueeze(0)

            future_pred_scaled = model(last_window_t).numpy()
            future_price = scaler_target.inverse_transform(future_pred_scaled)[0][0]

        return {
            "symbol": symbol,
            "predicted_next_close_price": float(future_price),
            "metrics": {
                "RMSE": float(rmse),
                "MAPE": float(mape),
                "R2": float(r2)
            },
            "training_info": info_msg
        }

    except Exception as e:
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=str(e))


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
