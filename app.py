import zipfile
from flask import Flask,g, jsonify, make_response,request
import json
import os
import cv2
import numpy as np
from werkzeug.utils import secure_filename
from utils.face_info import face_info
from utils.face_find import find_match
from utils.face_upload import face_upload
from utils.bulk_upload import get_image_meta_data

UPLOAD_FOLDER = os.path.dirname(os.path.realpath(__file__))

def c_app(app,db,conn):
    # Flask constructor takes the name of 
    # current module (__name__) as argument.
    # app = Flask(__name__)

    @app.route('/',methods=["GET"])
    async def base_function():
        return "Hello World"
    
    @app.route('/get_face_info',methods=["POST"])
    async def get_face_info():
        data_string = request.get_data()
        data = json.loads(data_string)
        id=int(data["id"])
        records=face_info(db,id)
        if(records !=-1):
            return jsonify(records)
        else:
            data = {'message': 'Wrong ID', 'code': 'Failure'}
            return make_response(jsonify(data),400)

    @app.route('/search_faces',methods=["POST"])
    async def search_faces():
        threshold=float(request.form["threshold"])
        k=int(request.form["k"])
        if 'file' not in request.files:
            data={'message':'wrong ID','code':'Failure'}
            return make_response(jsonify(data),400)
        image = request.files['file']
        imageFileName = secure_filename(image.filename)
        image.save(os.path.join(UPLOAD_FOLDER, imageFileName))
        #print(os.path.join(UPLOAD_FOLDER +"/"+ imageFileName))
        image = cv2.imread(os.path.join(UPLOAD_FOLDER +"/"+ imageFileName))
        records=find_match(db,image,threshold,k)
        return jsonify(records)

    @app.route('/add_face',methods=["POST"])
    async def upload_single_face():
        if 'file' not in request.files:
            data={'message':'wrong ID','code':'Failure'}
            return make_response(jsonify(data),400)
        image = request.files['file']
        imageFileName = secure_filename(image.filename)
        image.save(os.path.join(UPLOAD_FOLDER, imageFileName))
        #print(os.path.join(UPLOAD_FOLDER +"/"+ imageFileName))
        image = cv2.imread(os.path.join(UPLOAD_FOLDER +"/"+ imageFileName))
        # PersonName_PersonID_PersonImageID
        first_part=imageFileName.split(".")[0]
        name=first_part.split("_")[0]
        personID=first_part.split("_")[1]
        personImageID=first_part.split("_")[2]
        meta_data=[name,int(personID),int(personImageID),os.path.join(UPLOAD_FOLDER +"/"+ imageFileName)]
        response=face_upload(db,conn,image,meta_data)
        if response==1:
            data = {'message': 'Created', 'code': 'SUCCESS'}
            return make_response(jsonify(data), 201)
        else:
            data = {'message': 'Failed', 'code': 'Failure'}
            return make_response(jsonify(data),400)

    @app.route('/add_faces_in_bulk',methods=["POST"])
    async def upload_in_bulk():
        if 'file' not in request.files:
            data={'message':'wrong ID','code':'Failure'}
            return make_response(jsonify(data),400)
        file = request.files['file']
        #print(os.path.join(UPLOAD_FOLDER +"/"+ imageFileName))
        filename = secure_filename(file.filename)
        diretory_name=filename.split(".")[0]
        file.save(os.path.join(UPLOAD_FOLDER, filename))
        zip_ref = zipfile.ZipFile(os.path.join(UPLOAD_FOLDER, filename), 'r')
        zip_ref.extractall(UPLOAD_FOLDER)
        zip_ref.close()
        print(diretory_name)
        response=get_image_meta_data(diretory_name,db,conn)
        print(response)
        #return ("ehehe",201)
        if response==1:
            data = {'message': 'Created', 'code': 'SUCCESS'}
            return make_response(jsonify(data), 201)
        else:
            data = {'message': 'Failed', 'code': 'Failure'}
            return make_response(jsonify(data),400)
