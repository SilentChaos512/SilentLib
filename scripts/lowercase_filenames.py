import os
import sys

def rename_to_lowercase(root, name):
    current_path = os.path.join(root, name)
    new_path = os.path.join(root, str.lower(name))
    os.rename(current_path, new_path)



top_dir = ""

for arg in sys.argv:
    top_dir = arg

for root, dirs, files in os.walk(top_dir):
    for name in files:
        print(root + ", " + name + ", " + os.path.join(root, name))
        rename_to_lowercase(root, name)
