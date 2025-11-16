import psycopg2

conn = psycopg2.connect(
    host="localhost",
    database="mydb",
    user="docker",
    password="docker",
)

cur = conn.cursor()
cur.execute("""
INSERT INTO market_data (symbol, timestamp, open, high, low, close, volume) VALUES
('BTC-USD', '2025-11-15 00:00:00', 57000.0, 57500.0, 56800.0, 57200.0, 1200.5),
('BTC-USD', '2025-11-15 01:00:00', 57200.0, 57450.0, 57050.0, 57300.0, 980.3),
('BTC-USD', '2025-11-15 02:00:00', 57300.0, 57600.0, 57250.0, 57550.0, 1050.0),
('ETH-USD', '2025-11-15 00:00:00', 4300.0, 4350.0, 4280.0, 4320.0, 300.0),
('ETH-USD', '2025-11-15 01:00:00', 4320.0, 4340.0, 4305.0, 4335.0, 280.0),
('ETH-USD', '2025-11-15 02:00:00', 4335.0, 4360.0, 4325.0, 4350.0, 310.0);
""")
cur.execute("SELECT * FROM market_data;")
print(cur.fetchone())
conn.commit()

cur.close()
conn.close()