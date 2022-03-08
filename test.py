# importing all the necessary modules
from pydoc import cli
from flask import Flask
import os, sys; sys.path.append(os.path.dirname(os.path.realpath(__file__)))
from app import c_app
from utils.json_helper import json_helper
import psycopg2
import cv2
import io
from PIL import Image
import io

host,database,user,password=json_helper(['host','database','user','password'])
conn = psycopg2.connect(
        host=host,
        database=database,
        user=user,
        password=password)
# Open a cursor to perform database operations
db = conn.cursor()


# here I am testing the basic app object from flask library and its get functionality using a client
def test_base_route():
    app = Flask(__name__)
    c_app(app,db,conn)
    client = app.test_client()
    url = '/'

    response = client.get(url)
    print(response.get_data)
    assert response.get_data() == b'Hello World'
    assert response.status_code == 200


# this is the test which successfully runs through most of the code. Here it creates a client which sends a request to the server which
# after its own configurations and processings passes it to the receiver and the receiver sends back the acknowledgment
def test_route_1():
    app = Flask(__name__)
    c_app(app,db,conn)
    client = app.test_client()
    url = '/get_face_info'

    data={"id":1}
    response = client.post(url, json=(data))
    assert response.get_json()[0][1] == "Jim_Abbott"
    assert response.get_json()[0][4] == 'lfw_funneled/Jim_Abbott/Jim_Abbott_0001.jpg'
    assert response.status_code == 200

def test_route_1_bad_request():
    app = Flask(__name__)
    c_app(app,db,conn)
    client = app.test_client()
    url = '/get_face_info'

    data={"id":20000}
    response = client.post(url, json=(data))
    assert response.status_code == 400

def test_route_2():
    app = Flask(__name__)
    c_app(app,db,conn)
    client = app.test_client()
    url = '/search_faces'
    # prepare headers for http request
    content_type = 'image/jpg'
    headers = {'content-type': content_type}
    im = Image.open('face_find_images/Yuri_Fedotov.jpg')
    im_resize = im.resize((500, 500))
    buf = io.BytesIO()
    im_resize.save(buf, format='JPEG')
    byte_im = buf.getvalue()
    threshold=0.5
    data={"threshold":1-threshold,"k":2}
    data = {key: str(value) for key, value in data.items()}
    data["file"]=(io.BytesIO(byte_im), 'face_find_images/Yuri_Fedotov.jpg')
    response = client.post(url, data=data, headers=headers, content_type='multipart/form-data')
    
    assert response.get_json()[0][0][1] == "Yuri_Fedotov"
    assert response.get_json()[0][1][1] == "Yuri_Fedotov"
    assert response.status_code == 200

    im = Image.open('face_find_images/Bush_George_W.jpg')
    im_resize = im.resize((500, 500))
    buf = io.BytesIO()
    im_resize.save(buf, format='JPEG')
    byte_im = buf.getvalue()
    threshold=0.5
    data={"threshold":1-threshold,"k":5}
    data = {key: str(value) for key, value in data.items()}
    data["file"]=(io.BytesIO(byte_im), 'face_find_images/Bush_George_W.jpg')
    response = client.post(url, data=data, headers=headers, content_type='multipart/form-data')
    #print(response.get_json()[0])
    assert response.get_json()[0][0][1] == "George_W_Bush"
    assert response.get_json()[0][1][1] == "George_W_Bush"
    assert response.get_json()[0][2][1] == "George_W_Bush"
    assert response.get_json()[0][3][1] == "George_W_Bush"
    assert response.get_json()[0][4][1] == "George_W_Bush"
    assert response.status_code == 200

    im = Image.open('face_find_images/George_Lucas.jpg')
    im_resize = im.resize((500, 500))
    buf = io.BytesIO()
    im_resize.save(buf, format='JPEG')
    byte_im = buf.getvalue()
    threshold=0.4
    data={"threshold":1-threshold,"k":2}
    data = {key: str(value) for key, value in data.items()}
    data["file"]=(io.BytesIO(byte_im), 'face_find_images/George_Lucas.jpg')
    response = client.post(url, data=data, headers=headers, content_type='multipart/form-data')
    #print(response.get_json())
    assert response.get_json()[0][0][1] == "George_Lucas"
    assert response.get_json()[0][1][1] != "George_Lucas"
    assert response.status_code == 200

    im = Image.open('face_find_images/Tiger_Woods.jpg')
    im_resize = im.resize((500, 500))
    buf = io.BytesIO()
    im_resize.save(buf, format='JPEG')
    byte_im = buf.getvalue()
    threshold=0.35
    data={"threshold":1-threshold,"k":5}
    data = {key: str(value) for key, value in data.items()}
    data["file"]=(io.BytesIO(byte_im), 'face_find_images/Tiger_Woods.jpg')
    response = client.post(url, data=data, headers=headers, content_type='multipart/form-data')
    #print(response.get_json()[0])
    assert response.get_json()[0][0][1] == "Tiger_Woods"
    assert response.get_json()[0][1][1] == "Tiger_Woods"
    assert response.get_json()[0][2][1] == "Tiger_Woods"
    assert response.get_json()[0][3][1] != "Tiger_Woods"
    assert response.get_json()[0][4][1] == "Tiger_Woods"
    assert response.status_code == 200

    im = Image.open('face_find_images/sachin.jpg')
    im_resize = im.resize((500, 500))
    buf = io.BytesIO()
    im_resize.save(buf, format='JPEG')
    byte_im = buf.getvalue()
    threshold=0.5
    data={"threshold":1-threshold,"k":1}
    data = {key: str(value) for key, value in data.items()}
    data["file"]=(io.BytesIO(byte_im), 'face_find_images/sachin.jpg')
    response = client.post(url, data=data, headers=headers, content_type='multipart/form-data')
    #print(response.get_json())
    assert response.get_json()[0][0][1] == "Sachin_Tendulkar"
    assert response.status_code == 200

    im = Image.open('face_find_images/Roger_Federer.jpg')
    im_resize = im.resize((500, 500))
    buf = io.BytesIO()
    im_resize.save(buf, format='JPEG')
    byte_im = buf.getvalue()
    threshold=0.45
    data={"threshold":1-threshold,"k":3}
    data = {key: str(value) for key, value in data.items()}
    data["file"]=(io.BytesIO(byte_im), 'face_find_images/Roger_Federer.jpg')
    response = client.post(url, data=data, headers=headers, content_type='multipart/form-data')
    #print(response.get_json())
    assert response.get_json()[0][0][1] == "Carla_Moreno"
    assert response.get_json()[0][1][1] == "Daniele_Hypolito"
    assert response.get_json()[1][0][1] == "Martin_Keown"
    assert response.get_json()[1][1][1] == "Matt_Doherty"
    assert response.get_json()[1][2][1] == "Franco_Cangele"
    assert response.status_code == 200

    im = Image.open('face_find_images/images.jpeg')
    im_resize = im.resize((500, 500))
    buf = io.BytesIO()
    im_resize.save(buf, format='JPEG')
    byte_im = buf.getvalue()
    threshold=0.45
    data={"threshold":0.45,"k":3}
    data = {key: str(value) for key, value in data.items()}
    data["file"]=(io.BytesIO(byte_im), 'face_find_images/images.jpeg')
    response = client.post(url, data=data, headers=headers, content_type='multipart/form-data')
    #print(response.get_json())
    assert response.get_json()[0][0][1] == "George_W_Bush"
    assert response.status_code == 200

def test_route_2_bad_request():
    app = Flask(__name__)
    c_app(app,db,conn)
    client = app.test_client()
    url = '/search_faces'
    # prepare headers for http request
    content_type = 'image/jpg'
    headers = {'content-type': content_type}
    data={"threshold":0.5,"k":2}
    data = {key: str(value) for key, value in data.items()}
    response = client.post(url, data=data, headers=headers, content_type='multipart/form-data')
    assert response.status_code == 400


def test_route_3():
    app = Flask(__name__)
    c_app(app,db,conn)
    client = app.test_client()
    url = '/add_face'
    # prepare headers for http request
    content_type = 'image/jpg'
    headers = {'content-type': content_type}
    im = Image.open('AbrahamLincoln_10000_1.jpg')
    im_resize = im.resize((500, 500))
    buf = io.BytesIO()
    im_resize.save(buf, format='JPEG')
    byte_im = buf.getvalue()
    data={}
    data["file"]=(io.BytesIO(byte_im), 'AbrahamLincoln_10000_1.jpg')
    response = client.post(url, data=data, headers=headers, content_type='multipart/form-data')
    assert response.status_code == 201

def test_route_3_bad_request():
    app = Flask(__name__)
    c_app(app,db,conn)
    client = app.test_client()
    url = '/add_face'
    # prepare headers for http request
    content_type = 'image/jpg'
    headers = {'content-type': content_type}
    data={}
    response = client.post(url, data=data, headers=headers, content_type='multipart/form-data')
    assert response.status_code == 400


def test_route_4():
    app = Flask(__name__)
    c_app(app,db,conn)
    client = app.test_client()
    url = '/add_faces_in_bulk'
    # prepare headers for http request
    files = {'file': open('images.zip', 'rb')}
    response = client.post(url, data=files, content_type='multipart/form-data')
    print(response)
    assert response.status_code == 201

def test_route_4_bad_request():
    app = Flask(__name__)
    c_app(app,db,conn)
    client = app.test_client()
    url = '/add_faces_in_bulk'
    # prepare headers for http request
    files = {}
    response = client.post(url, data=files, content_type='multipart/form-data')
    print(response)
    assert response.status_code == 400
