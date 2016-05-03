#!/bin/bash

. common.sh

java -Xmx4096m -cp $CLASSPATH:target/JapaneseDictionary-1.0-SNAPSHOT.jar pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGenerator generate-missing-word-list $@
