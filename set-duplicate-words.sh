#!/bin/bash

export JAPANESE_DICTIONARY_API_VERSION=1.0-SNAPSHOT

export CLASSPATH=$HOME/.m2/repository/pl/idedyk/japanese/JapaneseDictionaryAPI/$JAPANESE_DICTIONARY_API_VERSION/JapaneseDictionaryAPI-$JAPANESE_DICTIONARY_API_VERSION.jar:$CLASSPATH
export CLASSPATH=$HOME/.m2/repository/net/sourceforge/javacsv/javacsv/2.1/javacsv-2.1.jar:$CLASSPATH
export CLASSPATH=$HOME/.m2/repository/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:$CLASSPATH
export CLASSPATH=$HOME/.m2/repository/jaxen/jaxen/1.1.4/jaxen-1.1.4.jar:$CLASSPATH

java -Xmx4096m -cp $CLASSPATH:target/JapaneseDictionary-1.0-SNAPSHOT.jar pl.idedyk.japanese.dictionary.misc.SetDuplicateWords
