#!/usr/local/bin/python3.3

import os
import sys

pb_dir=os.path.join(os.path.dirname(os.path.abspath(__file__)), 'tools', 'pb')
print(pb_dir);
sys.path.append(pb_dir)
pbdir_count=pb_dir.count('..')
print(pbdir_count);

import refreshProtos

config = refreshProtos.Config()

config.protos_dir = 'protos'
config.java_out = './src/main/java'

refreshProtos.refresh(config,pbdir_count)

