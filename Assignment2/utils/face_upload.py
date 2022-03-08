import cv2
import face_recognition
import psycopg2
# from json_helper import json_helper


def face_upload(cur,conn,img,meta_data):
    encodings = face_recognition.face_encodings(img)
    try:
        if len(encodings) > 0:
            cur.execute("INSERT INTO vectors (name, personid,imagepersonid, location, vec_low, vec_high)" 
            "VALUES (%s, %s, %s, %s, %s, %s)",
            (meta_data[0],meta_data[1],meta_data[2],meta_data[3],
            ','.join(str(s) for s in encodings[0][0:64]),
            ','.join(str(s) for s in encodings[0][64:128]))
            )
        conn.commit()
        # cur.close()
        # conn.close()
        return 1
    except:
        return 0

# file_name="lfw_funneled/Yuri_Fedotov/Yuri_Fedotov_0001.jpg"
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
# meta_data=["Dummy",1212121,12121,"dummy"]
# face_upload(db,image,meta_data)



