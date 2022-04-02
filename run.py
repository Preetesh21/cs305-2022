from pickle import TRUE
import click
import cv2
from config import CFG
from postprocess import PostProcessor
from preprocess import PreProcessor
from ocr import ocr


@click.command()
@click.option('--flag', prompt = 'Flag',default = TRUE, help = 'Flag information')
@click.option('--input', prompt = 'Input_directory',default = './book_cover', help = 'Path to the input file')


def run(flag,input):
    if(flag):
        imgs = cv2.imread(input)
        texts =[]
        pre = PreProcessor()
        tesser = ocr(lang = 'eng')
        post = PostProcessor(CFG)
        i = imgs
        image, _ = pre.preprocess(i)
        txt = tesser.get_text_from_image(image)
        txt = post.postprocess(txt)
        texts.append(txt)
        print(txt)
    else:
        texts =[]
        for files in input:
            imgs = cv2.imread(files)
            pre = PreProcessor()
            tesser = ocr(lang = 'eng')
            post = PostProcessor(CFG)
            i = imgs
            image, _ = pre.preprocess(i)
            txt = tesser.get_text_from_image(image)
            txt = post.postprocess(txt)
            texts.append(txt)
        print(texts)


if __name__ == '__main__':
    run()