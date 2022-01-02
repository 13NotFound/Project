import sys
import json

if len(sys.argv) > 2:
    name = sys.argv[1]
    result = sys.argv[2]

file = open("testcase_results.json", "r")
result_ls = json.loads(file.read())
file.close()

result_ls.append({name: result})

file = open("testcase_results.json", "w")
file.write(json.dumps(result_ls, indent = 4))
file.close()