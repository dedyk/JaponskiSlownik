#!/bin/bash

. common.sh

java -Xmx6096m -cp $CLASSPATH:target/JapaneseDictionary-1.0-SNAPSHOT.jar pl.idedyk.japanese.dictionary.android.AndroidDictionaryGenerator $1
