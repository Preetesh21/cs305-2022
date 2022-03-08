import cv2
import face_recognition
import os
import psycopg2
from utils.json_helper import json_helper


def get_image_meta_data(directory):
    host,database,user,password=json_helper(['host','database','user','password'])
    conn = psycopg2.connect(
            host=host,
            database=database,
            user=user,
            password=password)
    # Open a cursor to perform database operations
    cur = conn.cursor()
    # Execute a command: this creates a new table
    cur.execute('DROP TABLE IF EXISTS vectors;')
    cur.execute("create extension if not exists cube;")
    cur.execute("create table vectors (id serial PRIMARY KEY,"
                                        "Name VARCHAR NOT NULL,"
                                        "PersonID INTEGER NOT NULL,"
                                        'ImagePersonID INTEGER NOT NULL,'
                                        'Location VARCHAR NOT NULL,'
                                        " vec_low cube, vec_high cube);")
    cur.execute("create index vectors_vec_idx on vectors (vec_low, vec_high);")
    i=1

    count=0
    for file in os.listdir(directory):
        try:
            j=1
            for f in os.listdir(directory+"/"+file):
                file_l=directory+"/"+file+"/"+f
                
                img = cv2.imread(os.path.join(directory+"/"+file,f))
                encodings = face_recognition.face_encodings(img)
                #print(len(encodings))
                #print(file)
                if len(encodings) > 0:
                    cur.execute("INSERT INTO vectors (name, personid,imagepersonid, location, vec_low, vec_high)" 
                    "VALUES (%s, %s, %s, %s, %s, %s)",
                    (file,i,j,file_l,
                    ','.join(str(s) for s in encodings[0][0:64]),
                    ','.join(str(s) for s in encodings[0][64:128]))
                    )
                    j=j+1
                    count=count+1

        except Exception as e:
            print(e)
            print("Some error"+(directory+file))
        i=i+1
        
        #print(count)
        if(count%1000==0):
            print("Working on encodings",count)
    conn.commit()

    cur.close()
    conn.close()

path = 'lfw_funneled'
get_image_meta_data(path)



