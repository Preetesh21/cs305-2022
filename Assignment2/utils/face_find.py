import cv2
import face_recognition
# import psycopg2
# import numpy as np
# from json_helper import json_helper

def find_match(db,image,threshold,k):
    #print(threshold,k)
    final_ans=[]
    encodings = face_recognition.face_encodings(image)
    try:
        if len(encodings) > 0:
            for i in range(len(encodings)):
                query = "SELECT * FROM vectors WHERE sqrt(power(CUBE(array[{}]) <-> vec_low, 2) + power(CUBE(array[{}]) <-> vec_high, 2)) <= {} ".format(
                    ','.join(str(s) for s in encodings[i][0:64]),
                    ','.join(str(s) for s in encodings[i][64:128]),
                    threshold,
                ) + \
                        "ORDER BY sqrt(power(CUBE(array[{}]) <-> vec_low, 2) + power(CUBE(array[{}]) <-> vec_high, 2)) ASC LIMIT {}".format(
                            ','.join(str(s) for s in encodings[i][0:64]),
                            ','.join(str(s) for s in encodings[i][64:128]),
                            k,
                        )
                
                db.execute(query)
                mobile_records = db.fetchall()
                if(len(mobile_records)>0):
                    #print(len(mobile_records))
                    final_ans.append(mobile_records)
    except:
        return -1

    return final_ans

# file_name="face_find_images/images.jpeg"
# # Load the image
# image = cv2.imread(file_name)


# host,database,user,password=json_helper(['host','database','user','password'])
# conn = psycopg2.connect(
#         host=host,
#         database=database,
#         user=user,
#         password=password)
# # Open a cursor to perform database operations
# db = conn.cursor()

# matched_records=find_match(db,image,threshold = 0.6,k=3)
# print(matched_records)
# print(np.shape(matched_records))
