# importing libraries
import psycopg2
from utils.json_helper import json_helper
from flask import Flask
from app import c_app
# using only the INTERFACE provided by the library and following SOLID Principle
app = Flask(__name__)

host,database,user,password=json_helper(['host','database','user','password'])
conn = psycopg2.connect(
        host=host,
        database=database,
        user=user,
        password=password)
# Open a cursor to perform database operations
db = conn.cursor()
# passing the instance of the app object to the supporter module again displaying the usage of the SOLID Principle.
c_app(app,db,conn)

if __name__ == '__main__':
    
    # run() method of Flask class runs the application 
    # on the local development server.
    app.run(debug=True)