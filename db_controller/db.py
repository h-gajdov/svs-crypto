import psycopg2
from psycopg2.extras import RealDictCursor
import threading
from dotenv import load_dotenv
import os

load_dotenv()

class Database:
    _instance = None
    _lock = threading.Lock()

    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            with cls._lock:
                if not cls._instance:
                    cls._instance = super().__new__(cls)
        return cls._instance

    def __init__(self, host=os.getenv("DB_HOST"), database=os.getenv("DB_NAME"), user=os.getenv("DB_USER"), password=os.getenv("DB_PASSWORD")):
        if not hasattr(self, "_conn"):
            self._conn = psycopg2.connect(
                host=host,
                database=database,
                user=user,
                password=password
            )
            self._cur = self._conn.cursor(cursor_factory=RealDictCursor)

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

    def executemany(self, query, params_list):
        self._cur.executemany(query, params_list)

    def close(self):
        self._cur.close()
        self._conn.close()