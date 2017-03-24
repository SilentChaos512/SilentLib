import os
import sys

def lowercase_file_contents(root, name):
    path = os.path.join(root, name)
    
    # Read lines from file and lowercase them
    lines = []
    with open(path, 'r') as input:
        for line in input:
            lines.append(line.lower())

    # Write lowercased lines back to file
    with open(path, 'w') as output:
        for line in lines:
            output.write(line)



top_dir = ""

for arg in sys.argv:
    top_dir = arg

for root, dirs, files, in os.walk(top_dir):
    for name in files:
        lowercase_file_contents(root, name)
