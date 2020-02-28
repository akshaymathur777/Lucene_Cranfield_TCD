import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class File_Processes {
	
	// Function to Parse the file
	public List<Map<String, String>> parser (String Directory) {

        List<Map<String, String>> List = new ArrayList<Map<String, String>>();
        
        try {

            if (!(new File(Directory).exists() && new File(Directory).isDirectory())) 
            	Directory = "data/";
            File file = new File(Directory + "/cran.all.1400");
            FileReader file_reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(file_reader);

            Map<String, String> Read = new HashMap<String, String>();

            String line;
            String next_string = ".I";
            int lineNumber = 0;

            String id = "";
            String title = "";
            String authors = "";
            String locations = "";
            String doc_abstract = "";

            // Splitting the documents according to the different sections
            while ((line = bufferedReader.readLine()) != null) {

                lineNumber++;

                String[] words_in_line = line.split("\\s+");
                switch (words_in_line[0]) {

                    case ".I":
                        if (next_string != ".I")
                            System.out.println("Error in Parsing " + Integer.toString(lineNumber));
                        assert (Integer.parseInt(words_in_line[1]) - 1) == List.size();
                        if (lineNumber > 1) {

                            Read.put("ID", id);
                            Read.put("Abstract", doc_abstract);
                            List.add(Read);
                            Read = new HashMap<String, String>();
                        }
                        id = words_in_line[1];
                        doc_abstract = "";
                        next_string = ".T";
                        break;

                    case ".T":
                        if (next_string != ".T")
                            System.out.println("Error in Parsing " + Integer.toString(lineNumber));
                        next_string = ".A";
                        break;

                    case ".A":
                        if (next_string != ".A") {

                            if (next_string == ".I") break;
                            System.out.println("Error in Parsing " + Integer.toString(lineNumber));
                        }
                        Read.put("Title", title);
                        title = "";
                        next_string = ".B";
                        break;

                    case ".B":
                        if (next_string != ".B") {

                            if (next_string == ".I") break;
                            System.out.println("Error in Parsing " + Integer.toString(lineNumber));
                        }
                        Read.put("Authors", authors);
                        authors = "";
                        next_string = ".W";
                        break;

                    case ".W":
                        if (next_string != ".W") {

                            if (next_string == ".I") break;
                            System.out.println("Error in Parsing " + Integer.toString(lineNumber));
                        }
                        Read.put("Locations", locations);
                        locations = "";
                        next_string = ".I";
                        break;

                    default:
                        switch (next_string) {
                            case ".A":
                                title += line + " ";
                                break;

                            case ".B":
                                authors += line + " ";
                                break;

                            case ".W":
                                locations += line + " ";
                                break;

                            case ".I":
                                doc_abstract += line + " ";
                                break;

                            default:
                                System.out.println("Error in Parsing " + Integer.toString(lineNumber));
                        }
                }
            }

            Read.put("ID", id);
            Read.put("Abstract", doc_abstract);
            List.add(Read);

            file_reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return List;
    }

	// Parsing the queries
    public List<Map<String, String>> parse_queries(String Directory) {

        List<Map<String, String>> query_list = new ArrayList<Map<String, String>>();
        try {

            if (!(new File(Directory).exists() && new File(Directory).isDirectory())) 
            	Directory = "data/";

            File file = new File(Directory + "/cran.qry");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);


            Map<String, String> query_dict = new HashMap<String, String>();

            String line;
            String next_string = ".I";
            int line_number = 0;

            String id = "";
            int query_number = 0;
            String query = "";

            while ((line = bufferedReader.readLine()) != null) {

                line_number++;

                line = line.replace("?", "");
                String[] words_in_line = line.split("\\s+");
                switch (words_in_line[0]) {


                    case ".I":
                        if (next_string != ".I")
                            System.out.println("Error in Parsing " + Integer.toString(line_number));
                        if (line_number > 1) {

                        	query_dict.put("ID", id);
                        	query_dict.put("QueryNo", Integer.toString(query_number));
                        	query_dict.put("Query", query);
                            query_list.add(query_dict);
                            query_dict = new HashMap<String, String>();
                        }
                        id = words_in_line[1];
                        query_number++;
                        query = "";
                        next_string = ".W";
                        break;

                    case ".W":
                        if (next_string != ".W")
                            System.out.println("Error in Parsing " + Integer.toString(line_number));
                        next_string = ".I";
                        break;

                    default:
                        switch (next_string) {

                            case ".I":
                                query += line + " ";
                                break;

                            default:
                                System.out.println("Error in Parsing " + Integer.toString(line_number));
                        }
                }
            }

            query_dict.put("ID", id);
            query_dict.put("QueryNo", Integer.toString(query_number));
            query_dict.put("Query", query);
            query_list.add(query_dict);

            fileReader.close();
        } catch (IOException e) {

            e.printStackTrace();
            System.exit(1);
        }
        return query_list;
    }

    // Function to delete the specified directory and its contents
    public void delete_dir(File file) {

        File[] contents = file.listFiles();
        if (contents != null) {

            for (File f : contents) {

                delete_dir(f);
            }
        }
        file.delete();
    }
}