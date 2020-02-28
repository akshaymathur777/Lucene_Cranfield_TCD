import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

    private int HITS_PER_PAGE = 1000;

    public static void main(String[] args) throws ParseException {

    	File_Processes fileIO = new File_Processes();
        List<Map<String, String>> List = fileIO.parse_queries("data/");
        System.out.println("Documents Parsed");
        Searcher searcher = new Searcher();
        searcher.search_queries("indexFiles", List);
        fileIO.delete_dir(new File("indexed_documents"));
    }

    private void search_queries(String indexFile, List<Map<String, String>> List) {
        Vector_Space(List);
        BM25(List);
        System.out.println("Completed");

    }

    private void BM25(List<Map<String, String>> List) {

        try {
            @SuppressWarnings("unused")
			Map<String, List<String>> result_dict = new HashMap<String, List<String>>();
            Analyzer analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());
            Directory directory = FSDirectory.open(Paths.get("indexed_documents/BM25"));
            DirectoryReader index_reader = DirectoryReader.open(directory);
            IndexSearcher index_searcher = new IndexSearcher(index_reader);
            index_searcher.setSimilarity(new BM25Similarity());
            List<String> result = new ArrayList<String>();
            System.out.println("BM25 Similarity Running");
            // Create directory if it does not exist
            File outputDir = new File("results/BM25");
            if (!outputDir.exists()) 
            	outputDir.mkdirs();

            result_dict = parse_search(List, analyzer, index_searcher, result);

            Files.write(Paths.get("results/BM25/results.txt"), result, Charset.forName("UTF-8"));
        } catch (IOException e) {

            e.printStackTrace();
            System.exit(1);
        }
    }

    private void Vector_Space(List<Map<String, String>> List) {
        try {
            @SuppressWarnings("unused")
			Map<String, List<String>> result_dict = new HashMap<String, List<String>>();
            Analyzer analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());
            Directory directory = FSDirectory.open(Paths.get("indexed_documents/Vector_Space"));
            DirectoryReader dir_reader = DirectoryReader.open(directory);
            IndexSearcher index_searcher = new IndexSearcher(dir_reader);
            index_searcher.setSimilarity(new ClassicSimilarity());
            List<String> result = new ArrayList<String>();
            System.out.println("Vector Space Similarity Running");
            result_dict = parse_search(List, analyzer, index_searcher, result);

            // Create directory if it does not exist
            File outputDir = new File("results/Vector_Space");
            if (!outputDir.exists()) outputDir.mkdirs();

            Files.write(Paths.get("results/Vector_Space/results.txt"), result, Charset.forName("UTF-8"));
        } catch (IOException e) {

            e.printStackTrace();
            System.exit(1);
        }
    }

    private Map<String, List<String>> parse_search(List<Map<String, String>> List, Analyzer analyzer, IndexSearcher index_searcher, List<String> res_file) {
        Map<String, List<String>> resultDict = new HashMap<String, List<String>>();
        try {
            for (int i = 0; i < List.size(); i++) {

                Map<String, String> Query = List.get(i);
                MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                        new String[]{"Title", "Locations", "Authors", "Abstract"},
                        analyzer);
                Query query = queryParser.parse(Query.get("Query"));

                // Searching For Query
                TopDocs topDocs = index_searcher.search(query, HITS_PER_PAGE);
                ScoreDoc[] hits = topDocs.scoreDocs;

                // Results
                List<String> resultList = new ArrayList<String>();
//                System.out.println(hits.length + " hits Found.");
                for (int j = 0; j < hits.length; j++) {

                    int docId = hits[j].doc;
                    Document doc = index_searcher.doc(docId);
                    resultList.add(doc.get("ID"));
                    res_file.add(Query.get("QueryNo") + " 0 " + doc.get("ID") + " 0 " + hits[j].score + " STANDARD");
                }
                resultDict.put(Integer.toString(i + 1), resultList);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultDict;
    }

}