#!/bin/bash

llc miniTypes.myun.ll
gcc -c miniTypes.myun.s -o miniTypes.o
gcc miniTypes.o -o miniTypes
