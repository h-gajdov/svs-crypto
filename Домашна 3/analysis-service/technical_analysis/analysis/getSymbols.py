from sqlalchemy import create_engine
import pandas as pd

import os
from dotenv import load_dotenv
from sqlalchemy import create_engine

load_dotenv()

DB_HOST = os.getenv("DB_HOST")
DB_PORT = os.getenv("DB_PORT", "5432") 
DB_NAME = os.getenv("DB_NAME")
DB_USER = os.getenv("DB_USER")
DB_PASSWORD = os.getenv("DB_PASSWORD")

engine = create_engine(
    f"postgresql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"
)

def get_all_symbols():
    query = "SELECT DISTINCT symbol FROM market_data ORDER BY symbol;"
    df = pd.read_sql(query, engine)
    return df["symbol"].tolist()

def load_data(symbol, limit=50000):
    query = f"""
        SELECT timestamp, open, high, low, close, volume
        FROM market_data
        WHERE symbol = '{symbol}'
        ORDER BY timestamp ASC;
    """
    df = pd.read_sql(query, engine)

    df['timestamp'] = pd.to_datetime(df['timestamp'], unit='s')
    df.set_index('timestamp', inplace=True)

    return df

