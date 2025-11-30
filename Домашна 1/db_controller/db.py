import psycopg2
from psycopg2.extras import RealDictCursor
from dotenv import load_dotenv
import os

load_dotenv()

class Database:
    def __init__(self, host=os.getenv("DB_HOST"), database=os.getenv("DB_NAME"), user=os.getenv("DB_USER"), password=os.getenv("DB_PASSWORD")):
        self._conn = psycopg2.connect(
            host=host,
            database=database,
            user=user,
            password=password
        )
        # Create a new cursor for this specific instance
        self._cur = self._conn.cursor(cursor_factory=RealDictCursor)

    def is_empty(self):
        self._cur.execute("SELECT 1 FROM market_data LIMIT 1;")
        return self._cur.fetchone() is None

    def fetchall(self, query, params=None):
        self._cur.execute(query, params or ())
        return self._cur.fetchall()

    def fetchone(self, query, params=None):
        self._cur.execute(query, params or ())
        return self._cur.fetchone()

    def execute(self, query, params=None):
        self._cur.execute(query, params or ())

    def copy_expert(self, query, file):
        self._cur.copy_expert(query, file)

    def commit(self):
        self._conn.commit()

    def roll_back(self):
        self._conn.rollback()

    def close(self):
        if self._cur:
            self._cur.close()
        if self._conn:
            self._conn.close()