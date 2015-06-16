#!/usr/local/bin/python3.2

import re
import os
import fnmatch
import sys

def locate(pattern, root=os.curdir):
    '''Locate all files matching supplied filename pattern in and below
    supplied root directory.'''
    for path, dirs, files in os.walk(os.path.abspath(root)):
        for filename in fnmatch.filter(files, pattern):
            yield os.path.join(path, filename)

def findall(pattern, root=os.curdir):
    configs = []
    for file in locate(pattern, root):
        configs.append(file)
    return configs

def xmlfind(filepath, xpath, attrib):
    from xml.etree.ElementTree import ElementTree
    doc = ElementTree(file=filepath).getroot()
    items = []
    for mapper in doc.findall(xpath):
        item = mapper.attrib[attrib]
        items.append(item)
    return items

import imp
import hashlib
import os.path
import traceback

def md5_for_file(f, block_size=2**20):
    md5 = hashlib.md5()
    while True:
        data = f.read(block_size)
        if not data:
            break
        md5.update(data.encode())
    return md5

def load_module(code_path):
    try:
        try:
            code_dir = os.path.dirname(code_path)
            code_file = os.path.basename(code_path)

            fin = open(code_path, 'rb')

            import codecs
            return imp.load_source(md5_for_file(codecs.open(code_path, 'r', 'utf-8')).hexdigest(), code_path, fin)
        finally:
            try: fin.close()
            except: pass
    except ImportError as x:
        traceback.print_exc(file = sys.stderr)
        raise
    except:
        traceback.print_exc(file = sys.stderr)
        raise

def run_cmd(cmd):
    lines = [t.strip() for t in os.popen(cmd).readlines()]
    return lines

    
def table_exists(config, table):
    cmd = "mysql -h " + config['database.host'] + " -P " +config['database.port'] + " -u " + config['database.username'] + " -p" + config['database.password'] + " information_schema -Bse \"select TABLE_NAME from tables where table_name='%s' and table_schema='%s'\"" % (table, config['database.name'])
    lines = run_cmd(cmd)
    if len(lines) == 1:
        return True
    else:
        return False;

def db_version(config, version_table):
    cmd = "mysql -h " + config['database.host'] + " -P " +config['database.port'] + " -u " + config['database.username'] + " -p" + config['database.password'] + " " + config['database.name'] + " -Bse \"select version from %s.%s\"" % (config['database.name'], version_table)
    lines = run_cmd(cmd)
    if len(lines) == 1:
        version=lines[0].strip('\'')
        return int(version)
    else:
        return 0;

if __name__ == '__main__':
    load_module('dbutils.py')