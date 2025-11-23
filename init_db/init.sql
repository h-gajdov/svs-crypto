CREATE TABLE IF NOT EXISTS market_data (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    timestamp BIGINT NOT NULL,
    open DOUBLE PRECISION,
    high DOUBLE PRECISION,
    low DOUBLE PRECISION,
    close DOUBLE PRECISION,
    volume DOUBLE PRECISION,
    UNIQUE(symbol, timestamp)
);

CREATE TABLE IF NOT EXISTS daily_data (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    timestamp BIGINT NOT NULL,
    last_price DOUBLE PRECISION,
    volume_24h DOUBLE PRECISION,
    high_24h DOUBLE PRECISION,
    low_24h DOUBLE PRECISION,
    UNIQUE(symbol, timestamp),
    CONSTRAINT fk_daily_market_symbol FOREIGN KEY (symbol)
        REFERENCES market_data(symbol)
        ON DELETE CASCADE
);

-- This table is a temporary table for updating the main table
CREATE TABLE IF NOT EXISTS market_data_staging (
    symbol VARCHAR(20),
    timestamp BIGINT,
    open DOUBLE PRECISION,
    high DOUBLE PRECISION,
    low DOUBLE PRECISION,
    close DOUBLE PRECISION,
    volume DOUBLE PRECISION
);

CREATE INDEX IF NOT EXISTS idx_market_data_symbol ON market_data(symbol);
CREATE INDEX IF NOT EXISTS idx_market_data_timestamp ON market_data(timestamp);

CREATE INDEX IF NOT EXISTS idx_daily_data_symbol ON daily_data(symbol);
CREATE INDEX IF NOT EXISTS idx_daily_data_timestamp ON daily_data(timestamp);