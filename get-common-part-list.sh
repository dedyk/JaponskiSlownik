#!/bin/bash

. common.sh

#cat input/common_word.csv | head -10000 | egrep -E -e "^[0-9]*,," | cut -d, -f1 | shuf | head -25 | sort -n
#cat input/common_word.csv | head -10000 | egrep -E -e "^[0-9]*,," | cut -d, -f1 | wc -l

java -Xmx4096m -cp $CLASSPATH:target/JapaneseDictionary-1.0-SNAPSHOT.jar pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGenerator get-common-part-list $1
