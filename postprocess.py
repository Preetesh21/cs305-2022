import re
import numpy as np


class PostProcessor:
    
    def __init__(self, CFG):
        self.CHARS = CFG.chars
    # remove emplty lines from the txt recognized by Tesseract 
    def removeEmptyLines(self, txt):
        lines = txt.split('\n')
        non_empty_lines = [line for line in lines if line.strip() != ""]
        string_without_empty_lines = ""
        for line in non_empty_lines:
            string_without_empty_lines += line.replace('\n', '').replace('\r', '').replace('\n+e', '') + "\n"
        return string_without_empty_lines
    # Remove Undefined Characters, replacing @ by a and
    # replacing some characters with .
    def cleanText(self, txt):
        txt = re.sub(self.CHARS, ' ', txt)
        txt2 = ''
        for idx, i in enumerate(txt):
            if i == '@':
                txt2+= 'a'
                continue
            if idx < 4:
                txt2 += i
                continue
            if txt2[idx-4:idx] == '....' and txt[idx] != '.' and txt[idx] != ' ':
                txt2 += '.'
            else:
                txt2 += i
        return txt2


    def postprocess(self, txt):
        txt = self.removeEmptyLines(txt)
        txt = self.cleanText(txt)
        return txt
    
        