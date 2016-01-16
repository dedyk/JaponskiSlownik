#!/bin/bash

. common.sh

java -cp $CLASSPATH:target/JapaneseDictionary-1.0-SNAPSHOT.jar pl.idedyk.japanese.dictionary.tools.JMEDictSearcher $1
