import json
import sys

raw_json = sys.stdin.read()
json_obj = json.loads(raw_json)

for i in json_obj['jobs']:
    print i['name']+':'+i['url']