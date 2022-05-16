from pickle import TRUE
import click
import cv2
import os
from config import CFG
from postprocess import PostProcessor
from preprocess import PreProcessor
from ocr import ocr
from writer import writer


# @click.command()
# @click.option('--flag', prompt = 'Flag',default = TRUE, help = 'Flag information')
# @click.option('--input', prompt = 'Input_directory',default = './book_cover.jpg', help = 'Path to the input file')



def run(flag,input):
    if(flag!="false"):
        imgs = cv2.imread(input)
        pre = PreProcessor()
        tesser = ocr(lang = 'eng')
        post = PostProcessor(CFG)
        writ = writer()
        i = imgs
        image, _ = pre.preprocess(i)
        txt = tesser.get_text_from_image(image)
        txt = post.postprocess(txt)
        dict_={}
        line = txt.split("  ")
        lines = []
        for l in line :
            lines.append(l.replace("\n",""))
        topics = ['Book Name','Author']
        #print(lines)
        lines = lines[-2:]
        for i,l in enumerate(lines):
            dict_[topics[i]] = l
        #print(dict_)
        writ.write(dict_)
        
    else:
        texts =[]
        images = []
        for filename in os.listdir(input):
            img = cv2.imread(os.path.join(input,filename))
            if img is not None:
                images.append(img)
        for imgs in images:
            pre = PreProcessor()
            tesser = ocr(lang = 'eng')
            post = PostProcessor(CFG)
            writ = writer()
            i = imgs
            image, _ = pre.preprocess(i)
            txt = tesser.get_text_from_image(image)
            txt = post.postprocess(txt)
            txt = txt.split("  ")
            texts.append(txt)
        topics = ['Book Name','Author','ISBN']
        line = [item for sublist in texts for item in sublist]
        line=[i for i in line if i]
        #print(line)
        dict_={}
        for i,l in enumerate(line):
            dict_[topics[i]] = l
        #print(dict_)
        writ.write(dict_)


# if __name__ == '__main__':
#     run()