#!/bin/bash

. common.sh

# cat input/common_word.csv | head -10000 | egrep -E -e "^[0-9]*,," | cut -d, -f1 | shuf | head -25 | sort -n
# cat input/common_word.csv | head -10000 | egrep -E -e "^[0-9]*,," | cut -d, -f1 | wc -l
# cat input/word-new.csv | egrep -E -e "^[0-9]*," | shuf | head -1
# cat input/kanji.csv | grep "Kanji: " | wc -l
# kanji: =DŁ(H1)
# cat input/common_word.csv | head -99999999 | egrep -E -e "^[0-9]*,," | cut -d, -f4 | shuf | head -10
# cat input/word-new-test.csv | egrep -E -e "^[0-9]*,BEGIN," | shuf | head -1
# export LC_CTYPE=ja_JP.UTF-8
# paste -d'\n' plik1 plik2 | egrep -v '^$'
# cat input/all_missing_word_from_group_id_in_dictionary2_format.csv | cut -c1-8 | sort | uniq
# cat input/all_missing_word_from_group_id_full.csv | cut -c1-7 | sort | uniq
# cat input/all_missing_word_from_group_id_in_dictionary2_format.csv | cut -c1-7 | sort | uniq | egrep "^5.*"
# awk '!x[$0]++'
# cat input/all_missing_word_from_group_id_words_no_exist_in_jmedict.csv | sed -rn 's/(.*)(GroupId: [0-9]*).*/\2/p' | cut -d' ' -f2 | awk '!x[$0]++'

java -Xmx4096m -cp $CLASSPATH:target/JapaneseDictionary-1.0-SNAPSHOT.jar pl.idedyk.japanese.dictionary.tools.wordgenerator.WordGenerator get-common-part-list $@
