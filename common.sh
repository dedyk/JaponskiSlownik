#!/bin/bash

export JAPANESE_DICTIONARY_API_VERSION=1.0-SNAPSHOT
export JAPANESE_DICTIONARY_LUCENE_COMMON_VERSION=1.0-SNAPSHOT
export LUCENE_VERSION=4.7.2

export CLASSPATH=$HOME/.m2/repository/pl/idedyk/japanese/JapaneseDictionaryAPI/$JAPANESE_DICTIONARY_API_VERSION/JapaneseDictionaryAPI-$JAPANESE_DICTIONARY_API_VERSION.jar:$CLASSPATH
export CLASSPATH=$HOME/.m2/repository/pl/idedyk/japanese/JapaneseDictionaryLuceneCommon/$JAPANESE_DICTIONARY_LUCENE_COMMON_VERSION/JapaneseDictionaryLuceneCommon-$JAPANESE_DICTIONARY_LUCENE_COMMON_VERSION.jar:$CLASSPATH

export CLASSPATH=$HOME/.m2/repository/net/sourceforge/javacsv/javacsv/2.1/javacsv-2.1.jar:$CLASSPATH
export CLASSPATH=$HOME/.m2/repository/org/dom4j/dom4j/2.1.1/dom4j-2.1.1.jar:$CLASSPATH
export CLASSPATH=$HOME/.m2/repository/jaxen/jaxen/1.1.4/jaxen-1.1.4.jar:$CLASSPATH

export CLASSPATH=$HOME/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar:$CLASSPATH
export CLASSPATH=$HOME/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar:$CLASSPATH
export CLASSPATH=$HOME/.m2/repository/commons-cli/commons-cli/1.3.1/commons-cli-1.3.1.jar:$CLASSPATH

export CLASSPATH=$HOME/.m2/repository/org/json/json/20150729/json-20150729.jar:$CLASSPATH

export CLASSPATH=$HOME/.m2/repository/org/apache/lucene/lucene-core/$LUCENE_VERSION/lucene-core-$LUCENE_VERSION.jar:$CLASSPATH
export CLASSPATH=$HOME/.m2/repository/org/apache/lucene/lucene-analyzers-common/$LUCENE_VERSION/lucene-analyzers-common-$LUCENE_VERSION.jar:$CLASSPATH
export CLASSPATH=$HOME/.m2/repository/org/apache/lucene/lucene-queries/$LUCENE_VERSION/lucene-queries-$LUCENE_VERSION.jar:$CLASSPATH
export CLASSPATH=$HOME/.m2/repository/org/apache/lucene/lucene-queryparser/$LUCENE_VERSION/lucene-queryparser-$LUCENE_VERSION.jar:$CLASSPATH
export CLASSPATH=$HOME/.m2/repository/org/apache/lucene/lucene-sandbox/$LUCENE_VERSION/lucene-sandbox-$LUCENE_VERSION.jar:$CLASSPATH

export CLASSPATH=$HOME/.m2/repository/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar:$CLASSPATH
export CLASSPATH=$HOME/.m2/repository/commons-codec/commons-codec/1.15/commons-codec-1.15.jar:$CLASSPATH
