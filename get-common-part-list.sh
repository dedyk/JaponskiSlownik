#!/bin/bash

. common.sh

#cat input/common_word.csv | head -10000 | egrep -E -e "^[0-9]*,," | cut -d, -f1 | shuf | head -25 | sort -n
#cat input/common_word.csv | head -10000 | egrep -E -e "^[0-9]*,," | cut -d, -f1 | wc -l
#cat input/word-new.csv | egrep -E -e "^[0-9]*," | shuf | head -1
# cat input/kanji.csv | grep "Kanji: " | wc -l
# kanji: =DŁ(H1)
# cat input/common_word.csv | head -99999999 | egrep -E -e "^[0-9]*,," | cut -d, -f4 | shuf | head -10
# cat input/word-new-test.csv | egrep -E -e "^[0-9]*,BEGIN," | shuf | head -1
# export LC_CTYPE=ja_JP.UTF-8

java -Xmx4096m -cp $CLASSPATH:target/JapaneseDictionary-1.0-SNAPSHOT.jar pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGenerator get-common-part-list $@
