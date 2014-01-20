# Extract NameHelper.getMapping("(net/minecraft/tileentity/TileEntityHopper)", "(worldObj)", "(Lnet/minecraft/world/World;)")
# if group 3 contains ( this is a method lookup

# look in fields/methods.csv to find srg name
# then compare in packaged.exc to make sure the desc and class match

import os
import fileinput
import re
import sys
from optparse import OptionParser

searchPattern = re.compile(r'NameHelper.getMapping\s*\(\s*"(.*?)"\s*,\s*"(.*?)"\s*,\s*"(.*?)"\s*\)')
methodPattern = re.compile(r'^MD: [\w\/$_]+ .*? ([\w\/$_]+)\/([\w\/$_\<\>]+) (.+?)$')
fieldPattern = re.compile(r'^FD: [\w\/$_]+ ([\w\/$_]+)\/([\w\/$_\<\>]+)$')
lookupPattern = re.compile(r'#\s*([\w\/$_]+)\s*>\s*([\w\/$_]+)')

confDir = ""


def main():
    parser = OptionParser(version='1.0')
    parser.add_option('--conf', dest='confDir', help='The conf folder')
    parser.add_option('--src', dest='sourceDir', help='The source folder')

    options, _ = parser.parse_args()
    
    confDir = options.confDir
    
    global fieldFile
    global methodFile
    global packagedFile
    fieldFile = open(os.path.join(confDir, "fields.csv"), 'r')
    methodFile = open(os.path.join(confDir, "methods.csv"), 'r')
    packagedFile = open(os.path.join(confDir, "packaged.srg"), 'r')

    print("Translating names for ASM")
    filterAll(options.sourceDir)

    fieldFile.close()
    methodFile.close()
    packagedFile.close()

def getSRGName(file, name):
    file.seek(0)
    matching = []
    for line in file:
        parts = line.split(',')
        if parts[1] == name:
            matching.append(parts[0])
    
    return matching

def refine(matches, owner, desc, isMethod):
    packagedFile.seek(0)
    for line in packagedFile:
        if isMethod:
            match = re.match(methodPattern, line)
        else:
            match = re.match(fieldPattern, line)
            
        if match and match.group(2) in matches:
            if match.group(1) == owner:
                if isMethod:
                    if match.group(3) == desc:
                        return match.group(2)
                else:
                    return match.group(2)
            elif lookupPaths.has_key(owner):
                if match.group(1) in lookupPaths[owner]:
                    if isMethod:
                        if match.group(3) == desc:
                            return match.group(2)
                    else:
                        return match.group(2)
    return None

def translate(match):
    name = match.group(2)
    
    if match.group(3).find("(") != -1: #Method descriptor
        lookupFile = methodFile
        isMethod = True
    else: # Field descriptor
        lookupFile = fieldFile
        isMethod = False
        
        
    matches = getSRGName(lookupFile, name)
    
    if len(matches) != 0:
        newName = refine(matches, match.group(1), match.group(3), isMethod)
        if newName:
            name = newName
        
    return 'NameHelper.getMapping("%s","%s","%s")' % (match.group(1), name, match.group(3))

def filterAll(searchDir):
    for root, dirs, filenames in os.walk(searchDir):
        for filename in filenames:
            if filename.endswith(".java"):
                global lookupPaths
                lookupPaths = {}
                for line in fileinput.input(os.path.join(root, filename), inplace=True):
                    
                    match = re.search(lookupPattern, line)
                    if match:
                        if not lookupPaths.has_key(match.group(1)):
                            lookupPaths[match.group(1)] = [match.group(2)]
                        else:
                            lookupPaths[match.group(1)].append(match.group(2))
                            
                    sys.stdout.write(re.sub(searchPattern, translate, line))


if __name__ == "__main__":
    main()