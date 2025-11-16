import psycopg2

conn = psycopg2.connect(
    host="localhost",
    database="mydb",
    user="docker",
    password="docker",
)

cur = conn.cursor()
cur.execute("SELECT version();")
print(cur.fetchone())
conn.commit()

cur.close()
conn.close()