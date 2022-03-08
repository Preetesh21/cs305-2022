import cv2
import face_recognition
import os
import numpy as np

def get_image_meta_data(directory,cur,conn):
    try:
        #print("Directory is"+directory)
        for file in os.listdir(directory):
            # file name of the format PersonName_PersonID_PersonimageID.jpg
            file_l=directory+"/"+file
            first_part=file.split(".")[0]
            name=first_part.split("_")[0]
            personID=first_part.split("_")[1]
            personImageID=first_part.split("_")[2]
            img = cv2.imread(file_l)
            img = cv2.resize(img,(500, 500))
            encodings = face_recognition.face_encodings(img)    
            if len(encodings) > 0:
                cur.execute("INSERT INTO vectors (name, personid,imagepersonid, location, vec_low, vec_high)" 
                "VALUES (%s, %s, %s, %s, %s, %s)",
                (name,int(personID),int(personImageID),file_l,
                ','.join(str(s) for s in encodings[0][0:64]),
                ','.join(str(s) for s in encodings[0][64:128]))
                )
            
    except : 
        return 0
    conn.commit()
    return 1




