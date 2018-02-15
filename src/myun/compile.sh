#!/bin/bash

llc -O0 $1.ll && gcc -c $1.s -o $1.o && gcc $1.o -o $1.out
