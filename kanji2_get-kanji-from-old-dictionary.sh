#!/bin/bash

. common.sh

java -Xmx4096m -cp $CLASSPATH:target/JapaneseDictionary-1.0-SNAPSHOT.jar:target/test-classes pl.idedyk.japanese.kanji2.app.GetKanjiFromOldDictionary $@
