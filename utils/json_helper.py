import json

def json_helper(name_list):
    f=open("config.json")
    data=json.load(f)
    item_a=data[name_list[0]]
    item_b=data[name_list[1]]
    item_c=data[name_list[2]]
    item_d=data[name_list[3]]
    f.close()
    return item_a,item_b,item_c,item_d