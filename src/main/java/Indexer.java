import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.MultiSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Indexer {
	
	  public void index_create(List<Map<String, String>> List) {

	        Vector_Space(List);
	        BM25(List);
	  }
	  
	  // Define the BM25 Indexer
	  void BM25(List<Map<String, String>> List) {

	        try {
	            Analyzer analyzer = null;
	            analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());
	            Directory directory = FSDirectory.open(Paths.get("indexed_documents/BM25"));
	            IndexWriterConfig config = new IndexWriterConfig(analyzer);
	            // Using Multi Similarity
	            Similarity similarity[] = {
	            		new BM25Similarity(),
	            		new ClassicSimilarity()
	            };
	            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
	            config.setSimilarity(new MultiSimilarity(similarity));
	            IndexWriter iwriter = new IndexWriter(directory, config);

	            for (int i = 0; i < List.size(); i++)
	                add_document(iwriter, List.get(i));

	            iwriter.close();
	            directory.close();
	        } 
	        
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	  
	  // Define the Vector Space Indexer
	  void Vector_Space(List<Map<String, String>> List) {

	        try {
	            Analyzer analyzer = null;
	            analyzer = new WhitespaceAnalyzer();
	            		//StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());
	            Directory directory = FSDirectory.open(Paths.get("indexed_documents/Vector_Space"));
	            IndexWriterConfig config = new IndexWriterConfig(analyzer);
	            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
	            config.setSimilarity(new ClassicSimilarity());
	            IndexWriter iwriter = new IndexWriter(directory, config);
	            for (int i = 0; i < List.size(); i++)
	                add_document(iwriter, List.get(i));

	            iwriter.close();
	            directory.close();
	        } 
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	  
	  private void add_document(IndexWriter iwriter, Map<String, String> Dict) throws IOException {

	        Document document = new Document();
	        document.add(new StringField("ID", Dict.get("ID"), Field.Store.YES));
	        document.add(new TextField("Title", Dict.get("Title"), Field.Store.YES));
	        document.add(new TextField("Locations", Dict.get("Locations"), Field.Store.YES));
	        document.add(new TextField("Authors", Dict.get("Authors"), Field.Store.YES));
	        document.add(new TextField("Abstract", Dict.get("Abstract"), Field.Store.YES));
	        iwriter.addDocument(document);
	    }
	    
	  // The Main Function
	  public static void main(String[] args) {

	        System.out.println("Process Started");
	        File_Processes fileIO = new File_Processes();
	        List<Map<String, String>> List = fileIO.parser("data/");
	        Indexer indexer = new Indexer();
	        indexer.index_create(List);
	        System.out.println("Process Completed");
	    }
}