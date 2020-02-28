#!/bin/bash

mvn exec:java -Dexec.mainClass=Indexer

mvn exec:java -Dexec.mainClass=Searcher

cd trec_eval

./trec_eval ../data/QRelsCorrectedforTRECeval ../results/BM25/results.txt > ../BM25_results.txt

./trec_eval ../data/QRelsCorrectedforTRECeval ../results/Vector_Space/results.txt > ../VectorSpace_results.txt

cd .. 

echo Process Completed