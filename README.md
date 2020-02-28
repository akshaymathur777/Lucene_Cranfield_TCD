## Lucene_Cranfield_TCD

### Steps for Running 
Just execute the run.sh file present in the Lucene_Cranfield_TCD folder

./run.sh

OR

## Change directory to Lucene_Cranfield_TCD

cd Lucene_Cranfield_TCD

## Indexer

mvn exec:java -Dexec.mainClass=Indexer

## Searching

mvn exec:java -Dexec.mainClass=Searcher

## Running Trec Eval

### Change directory to trec_eval

cd trec_eval

## Running trec_eval for BM25 Similarity

./trec_eval ../data/QRelsCorrectedforTRECeval ../results/BM25/results.txt > ../BM25_results.txt

## Running trec_eval for Vector Space Model

./trec_eval ../data/QRelsCorrectedforTRECeval ../results/Vector_Space/results.txt > ../VectorSpace_results.txt

## The results for the trec_eval are present in the folder Lucene_Cranfield_TCD as BM25_results.txt and VectorSpace_results.txt

