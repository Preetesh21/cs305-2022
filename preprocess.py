import cv2
import numpy as np


class PreProcessor:

    # removing any noise if present in the image
    def denoiseimage(self,img):
        dst = cv2.fastNlMeansDenoisingColored(img,None,10,10,7,21)
        return dst

    # Adjust image in case it was rotated a little 
    def fixRotation(self,img):
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        (h, w) = img.shape[:2]
        gray = cv2.bitwise_not(gray)
        thresh = cv2.threshold(gray, 0, 255,
        cv2.THRESH_BINARY | cv2.THRESH_OTSU)[1]
        coords = np.column_stack(np.where(thresh > 0))
        angle = cv2.minAreaRect(coords)[-1]
        if angle < -45:
            angle = -(90 + angle)
        else:
            angle = -angle
        center = (w // 2, h // 2)
        M = cv2.getRotationMatrix2D(center, angle, 1.0)
        rotated = cv2.warpAffine(img, M, (w, h),
        flags=cv2.INTER_CUBIC, borderMode=cv2.BORDER_REPLICATE)
        return rotated
    
    # In case there were lines in the text image, we need to remove them
    def removeLines(self, image):
        result = image.copy()
        gray = cv2.cvtColor(image,cv2.COLOR_BGR2GRAY)
        thresh = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY_INV + cv2.THRESH_OTSU)[1]
        # Remove horizontal lines
        horizontal_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (20,1))
        remove_horizontal = cv2.morphologyEx(thresh, cv2.MORPH_OPEN, horizontal_kernel, iterations=2)
        cnts = cv2.findContours(remove_horizontal, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        cnts = cnts[0] if len(cnts) == 2 else cnts[1]
        for c in cnts:
            cv2.drawContours(result, [c], -1, (255,255,255), 1)
        vertical_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (1,20))
        remove_vertical = cv2.morphologyEx(thresh, cv2.MORPH_OPEN, vertical_kernel, iterations=2)
        cnts = cv2.findContours(remove_vertical, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        cnts = cnts[0] if len(cnts) == 2 else cnts[1]
        for c in cnts:
            cv2.drawContours(result, [c], -1, (255,255,255), 1)
        return result
    
    def preprocess(self, img):
        original_img = img.copy()
        #img = self.denoiseimage(img)
        img = self.removeLines(img)
        img = self.fixRotation(img)       
        return img, original_img

    
