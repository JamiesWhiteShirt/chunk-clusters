### chunk-clusters ###

chunk-clusters is a tool that analyzes chunk clusters in Minecraft region folders. A chunk cluster is a connected body of chunks. A Minecraft world can consist of several chunk clusters originating from different actions.

chunk-clusters' purpose is to detect chunk clusters that are accidentally created by mods.

NOTE: This tool is currently incomplete. In the future chunk-clusters will be able to determine the origin of a chunk cluster via a companion mod.

## Usage ##

```
$ chunk-clusters -h
usage: chunk-clusters [-h] region

Tool to analyze Minecraft world chunks

positional arguments:
  region                 Path to region folder

optional arguments:
  -h, --help             show this help message and exit
```
