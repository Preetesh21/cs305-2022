def face_info(db,imageID):
    query="SELECT * from vectors WHERE id = {}".format(
        imageID,
    )
    db.execute(query)
    mobile_records = db.fetchall()
    if(len(mobile_records)>0):
        #print(len(mobile_records),mobile_records[0])
        return tuple(mobile_records)
    else:
        return -1



# imageID = 100
# host,database,user,password=json_helper(['host','database','user','password'])
# conn = psycopg2.connect(
#         host=host,
#         database=database,
#         user=user,
#         password=password)
# # Open a cursor to perform database operations
# db = conn.cursor()

# matched_records=face_info(db,imageID)
