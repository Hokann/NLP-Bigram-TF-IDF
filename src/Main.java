import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;

import java.io.*;
import java.util.*;

import static edu.stanford.nlp.util.StringUtils.editDistance;

public class Main {
        public static void main(String[] args) throws IOException {

            WordMap wordMap = new WordMap();

            ArrayList<MapEntry<String, Integer>> totalWords = new ArrayList<>();

            File folder = new File("src/dataset");
            File[] listOfFiles = folder.listFiles();
            // sort files in alphabetical order to ensure later on that totalWords matches with the correct file
            // e.g. totalWords.get(0) == firstfileWords.length (first => alphabetically comes first)
            Arrays.sort(listOfFiles);
            for (File file : listOfFiles)
            {
                if(file.isFile())
                {
                    BufferedReader br=new BufferedReader(new FileReader(new File("src/dataset"+"/"+file.getName())));
                    StringBuffer word=new StringBuffer();
                    String line;
                    while((line=br.readLine())!=null)
                    {
                        String newline=line.replaceAll("[^’'a-zA-Z0-9]"," ");
                        String finalline=newline.replaceAll("\\s+"," ").trim();
                        // set up pipeline properties
                        Properties props=new Properties();
                        // set the list of annotators to run
                        props.setProperty("annotators","tokenize,pos,lemma");
                        // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
                        props.setProperty("coref.algorithm","neural");
                        // build pipeline
                        StanfordCoreNLP pipeline=new StanfordCoreNLP(props);
                        // create a document object
                        CoreDocument document=new CoreDocument(finalline);
                        // annnotate the document
                        pipeline.annotate(document);
                        //System.out.println(document.tokens());
                        for(CoreLabel tok:document.tokens()){
                            String str=String.valueOf(tok.lemma());
                            if(!(str.contains("'s")||str.contains("’s"))){
                                word.append(str).append(" ");
                            }
                        }
                    }
                    String str=String.valueOf(word);
                    str=str.replaceAll("[^a-zA-Z0-9]"," ").replaceAll("\\s+"," ").trim();
                    //System.out.println(str);
                    String[] words = str.split("\\s+");
                    //System.out.println(Arrays.toString(words));

                    totalWords.add(new MapEntry<>(file.getName(), words.length));//number of words per document, in alphabetical order
                    // Building the wordmap
                    for ( int index = 0; index < words.length; index++){
                        //System.out.println(index);
                        ArrayList i = new ArrayList(1); i.add(index);

                        if (wordMap.get(words[index]) == null){
                            FileMap fileMap = new FileMap();
                            fileMap.put(file.getName(), i);
                            wordMap.put(words[index], fileMap);
                        }
                        else{ wordMap.get(words[index]).put(file.getName(), i); }
                    }
                }
            }
            System.out.println(totalWords.toString());
            System.out.println("-----------------------------------");
            //Printing out all entries of the wordmap (and for each word its corresponding filemap)
            for (Map.Entry entry: wordMap.entrySet2()) {
                String word = (String) entry.getKey();
                System.out.println(word+" : ");
                FileMap wordFileMap = (FileMap) entry.getValue();
                for (Map.Entry entry2: wordFileMap.entrySet2()){
                    String filename = (String) entry2.getKey();
                    ArrayList<Integer> positions = (ArrayList<Integer>) entry2.getValue();
                    System.out.println(" - "+filename + " : "+positions.toString());
                }
            }
            System.out.println("-----------------------------------");

            try {
                File queryFile = new File("src/query.txt");
                Scanner reader = new Scanner(queryFile);
                while (reader.hasNextLine()) {
                    String query = reader.nextLine();

                    String[] arr = query.split("\\s+");
                    List<String> list = Arrays.asList(arr);
                    LinkedList<String> queryWordsList = new LinkedList<>(list);

                    // TYPE 2 QUERY : Retrieving most relevant document
                    if (arr[0].equals("search")){
                        queryWordsList.removeFirst();
                        System.out.println(queryWordsList+" TF-IDT");
                        ArrayList<String> searchQuery = correctQuery(queryWordsList, wordMap);
                        System.out.println("search" + searchQuery.toString());

                        Search search = new Search(searchQuery, wordMap, totalWords);
                        System.out.println(search.TFIDF());



                        // TYPE 1 QUERY : Finding most probable Bigram of word
                    }else{
                        String bigramoOf = queryWordsList.getLast();
                        queryWordsList.clear(); queryWordsList.add(bigramoOf);
                        System.out.println(queryWordsList+" Bigram");
                        ArrayList<String> bigramQuery = correctQuery(queryWordsList, wordMap);
                        System.out.println(bigramQuery.toString());
                    }
                }

                reader.close(); // fin lecture fichier
            } catch (FileNotFoundException e) {
                System.out.println("Erreur : fichier non trouvé");
                e.printStackTrace();
            }
    }
    public static ArrayList<String> correctQuery(List<String> typos, WordMap map){
            ArrayList<String> correctedQuery = new ArrayList<>();
        for (String typo : typos){
            String correctedWord = typo;
            int minDistance = typo.length();
            for (Map.Entry<String, FileMap> e : map.entrySet2()){
                String word = e.getKey();
                int LevenshteinDistance = editDistance(typo, word);
                if (LevenshteinDistance < minDistance){
                    minDistance = LevenshteinDistance;
                    correctedWord = word;
                }
            }
            correctedQuery.add(correctedWord);
        }
        return correctedQuery;
    }
}