# import xlsxwriter module
import xlsxwriter

class writer:

    def write(self,content):
        #print(content)
        # Workbook() takes one, non-optional, argument
        # which is the filename that we want to create.
        workbook = xlsxwriter.Workbook('output.xlsx')
        
        # The workbook object is then used to add new
        # worksheet via the add_worksheet() method.
        worksheet = workbook.add_worksheet()
        if(len(content) == 2):
            key=list(content.keys())
            value=list(content.values())
            # Use the worksheet object to write
            # data via the write() method.
            worksheet.write('A1', key[0])
            worksheet.write('B1', key[1])
            worksheet.write('A2', value[0])
            worksheet.write('B2', value[1])
        
        else:
            key=list(content.keys())
            value=list(content.values())
            # Use the worksheet object to write
            # data via the write() method.
            worksheet.write('A1', key[0])
            worksheet.write('B1', key[1])
            worksheet.write('A2', value[0])
            worksheet.write('B2', value[1])
            worksheet.write('C1',key[2])
            worksheet.write('C2',value[2])
        
        # Finally, close the Excel file
        # via the close() method.
        workbook.close()