#!/bin/bash

for jarfile in */*/*.java
do
echo $jarfile
java insertText $jarfile
done

for jarfile in */*/*/*.java
do
echo $jarfile
java insertText $jarfile
done



