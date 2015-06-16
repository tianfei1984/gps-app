#!/usr/local/bin/python3.2

import re
import os
import sys
sys.path.append(os.path.join(os.path.dirname(os.path.abspath(__file__))))
import utils
class Config(object):

    def __init__(self):

        #relative protocol buffer directory
        self.protos_dir = None
        
        #relative java code output directory
        self.java_out = None
        
        #relative python code output directory
        self.python_out = None
        
        #protos which are defined in other project, delimited with ;, example: ../other-project/protos/external/test1.proto,protos/external/test1.proto;../other-project/protos/external/test2.proto,protos/external/test2.proto
        self.external_protos = None

        #relative python code output directory(test/rest)
        self.python_out_test=None

def refresh(config,dircount):

    print('refresh start...')
    if(config.protos_dir == None):
        config.protos_dir = 'protos'
    
    externalProtos = []
    if(config.java_out == None):
        config.java_out = 'src/main/java'
    
    if (config.python_out == None):
        config.python_out = 'src/main/resources'

    if (config.python_out_test==None):
        #config.python_out_test='../../../../../test/rest'
        config.python_out_test=('../'*(dircount) +'test/rest')
        
    if(config.external_protos is not None):
        externalProtos = config.external_protos.split(';')
    
    utils.run_cmd('IF NOT EXIST "%s" MKDIR "%s"' % (config.java_out, config.java_out))
    
    utils.run_cmd('IF NOT EXIST "%s" MKDIR "%s"' % (config.python_out, config.python_out))

    utils.run_cmd('IF NOT EXIST "%s" MKDIR "%s"' % (config.python_out_test,config.python_out_test))

    print('protos directory:%s' % config.protos_dir)
    print('java output directory:%s' % config.java_out)

    externalProtosImport = []
    externalProtosDir = []
    print('copy external protos')
    for proto in externalProtos:
        splitArray = proto.split(',')
        protoPath = splitArray[0]
        protoImport = splitArray[1]
        externalProtosImport.append(protoImport)
        protoDir = protoImport[0:protoImport.rindex('/')].replace('/', '\\')
        externalProtosDir.append(protoDir)
        utils.run_cmd('IF NOT EXIST "%s" MKDIR "%s"' % (protoDir, protoDir))
        utils.run_cmd('copy /Y %s %s' % (protoPath.replace('/', '\\'), protoImport.replace('/', '\\')))
    print('finish copying external protos')
    
    print('copy base.proto and protoc.exe...')
    script_dir = os.path.dirname(os.path.abspath(__file__))
    #baseProtoFilePath = os.path.join(script_dir, 'base.proto')
    #utils.run_cmd('copy /Y %s .' % baseProtoFilePath)
    toolFilePath2 = os.path.join(script_dir, 'protoc2.exe')
    utils.run_cmd('copy /Y %s .' % toolFilePath2)
    toolFilePath3 = os.path.join(script_dir, 'protoc3.exe')
    utils.run_cmd('copy /Y %s .' % toolFilePath3)
    print('finish copy')
    
    protoFiles = utils.findall('*.proto', config.protos_dir)
    for protoFile in protoFiles:
        if contain(externalProtosImport, protoFile) is False:
            proto_cmd = 'protoc2.exe --java_out=%s %s' % (config.java_out, '.%s' % protoFile.replace(os.path.abspath(os.curdir), '').replace('\\', '/'))
            python_proto_cmd = 'protoc3.exe --python_out=%s %s' % (config.python_out, '.%s' % protoFile.replace(os.path.abspath(os.curdir), '').replace('\\', '/'))
            python_test_cmd= 'protoc3.exe --python_out=%s %s' % (config.python_out_test, '.%s' % protoFile.replace(os.path.abspath(os.curdir), '').replace('\\', '/'))
            print('execute %s' % proto_cmd)
            utils.run_cmd(proto_cmd)
            print('execute %s' % python_proto_cmd)
            utils.run_cmd(python_proto_cmd)
            print('execute %s' % python_test_cmd)
            utils.run_cmd(python_test_cmd)

    
    print('finish pb code generation')
    print('clean base.proto and protoc.exe...')
    #utils.run_cmd('del /Q base.proto')
    utils.run_cmd('del /Q protoc2.exe')
    utils.run_cmd('del /Q protoc3.exe')

    for file in externalProtosImport:
        utils.run_cmd('del /Q %s' % file.replace('/', '\\'))
    
    for dir in externalProtosDir:
        if len(utils.findall('*.proto', dir)) == 0:
            utils.run_cmd('rd /Q %s' % dir.replace('/', '\\'))
    
    print('finish cleaning')
    
    print('refresh finish')

    
def contain(protoArray, protoPath):
    protoPath = protoPath.replace('\\', '/')
    for proto in protoArray:
        if proto[proto.rindex('/'):] == protoPath[protoPath.rindex('/'):]:
            return True
    
    return False
        
        
    

if __name__ == '__main__':
    config = Config()
    refresh(config,5)
