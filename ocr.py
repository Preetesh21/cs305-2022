import cv2
import pytesseract
from PIL import Image
    
    
class ocr:

    def __init__(self, lang = None):
        self.lang = lang
        
    def get_text_from_image(self,img):
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        text = pytesseract.image_to_string(Image.fromarray(img), config='--psm 1', lang = self.lang)
        return text
