import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;

import java.io.*;
import java.util.*;

import static edu.stanford.nlp.util.StringUtils.editDistance;

/**
 * This program implements 2 NLP operations :
 * 1) Finding the most probable Bigram of a word
 * 2) Finding the most relevant document in relation to a given word
 *
 * We first clean our dataset using the StandfordCoreNLP package
 * Then, we build 2 implementations of Map, allowing us to efficiently manipulate our dataset
 * Finally, using these implementations we can process queries requesting the bigram of a word (1), or the most relevant document of given word(s).
 *
 * @author Hokan Gillot (20242295)
 * */
public class Main {
        public static void main(String[] args) throws IOException {

            // Starting INPUTS
            String dataset = "src/dataset";
            String query = "src/query.txt";

            WordMap wordMap = new WordMap(); // implementation of Map, specifically for words

            ArrayList<MapEntry<String, Integer>> totalWords = new ArrayList<>(); // to keep track of the number of words in a document

            File folder = new File(dataset);
            File[] listOfFiles = folder.listFiles();
            // sort files in alphabetical order to ensure later on that totalWords matches with the correct file
            // e.g. totalWords.get(0) == firstfileWords.length (first => alphabetically comes first)
            Arrays.sort(listOfFiles);
            for (File file : listOfFiles)
            {
                if(file.isFile())
                {
                    BufferedReader br=new BufferedReader(new FileReader(new File(dataset+"/"+file.getName())));
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
                    String[] words = str.split("\\s+");

                    totalWords.add(new MapEntry<>(file.getName(), words.length));//number of words per document, in alphabetical order

                    // Building the wordMap
                    for ( int index = 0; index < words.length; index++){
                        ArrayList<Integer> i = new ArrayList<>(1); i.add(index);
                        if (wordMap.get(words[index]) == null){ // if word doesn't exist in our wordMap
                            FileMap fileMap = new FileMap();
                            fileMap.put(file.getName(), i);
                            wordMap.put(words[index], fileMap); // we add it and create it's corresponding FileMap
                        }
                        else{ wordMap.get(words[index]).put(file.getName(), i); } // otherwise, we update the word's fileMap
                    }
                }
            }

            // Reading queries, and writing the solutions
            try {
                File queryFile = new File(query);
                Scanner reader = new Scanner(queryFile);

                File outputFile = new File("src/solutions.txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

                while (reader.hasNextLine()) { // each line of query.txt is a specific request, we deal with them one at a time
                    String queryLine = reader.nextLine();
                    String[] arr = queryLine.split("\\s+");
                    List<String> list = Arrays.asList(arr);
                    LinkedList<String> queryWordsList = new LinkedList<>(list); // transform the String into a LinkedList of the words

                    // TYPE 2 QUERY : Retrieving most relevant document
                    if (arr[0].equals("search")){
                        queryWordsList.removeFirst(); // words to consider
                        ArrayList<String> searchQuery = correctQuery(queryWordsList, wordMap); // correct spelling errors of words if nececssary

                        Search TFIDFQuery = new Search(searchQuery, wordMap, totalWords);
                        ArrayList<Double> searchResults = TFIDFQuery.TFIDF(); // List of the docuements' TF-IDF values
                        Double highestTFIDF = 0.0;
                        for (Double value : searchResults){
                            if (value > highestTFIDF){
                                highestTFIDF = value; // max value is the most relevant
                            }
                        }
                        // get the document associated the max TF-IDF
                        MapEntry<String, Integer> documentEntry = totalWords.get(searchResults.indexOf(highestTFIDF));

                        String mostRelevantDocument = documentEntry.getKey();
                        writer.write(mostRelevantDocument+"\n"); // write the document in the solution


                        // TYPE 1 QUERY : Finding the most probable Bigram of word
                    }else{
                        String bigramoOf = queryWordsList.getLast(); // word to find the most probable bigram of
                        queryWordsList.clear(); queryWordsList.add(bigramoOf);
                        ArrayList<String> bigramQuery = correctQuery(queryWordsList, wordMap); // correct word if necessary
                        String word = bigramQuery.getLast();

                        // Using Bigram.java, we find our solution
                        Bigram bigram = new Bigram(wordMap);
                        String mostProbableBigram = bigram.bigramOf(word);
                        writer.write(word + " " + mostProbableBigram+"\n"); // write word and its most probable bigram

                    }
                }

                reader.close();
                writer.close();
            } catch (FileNotFoundException e) {
                System.out.println("Error : file not found");
                e.printStackTrace();
            }
    }

    /**
     * This method takes a list of possible typos, words that may need to be corrected, and our wordmap
     * Returns the corrected list of words
     *
     * Using the editDistance method provided in CoreNLP, we calculate the LevenshteinDistance between our word and all the
     * other words in the wordMap.
     * The word with the minimal distance can be considered the "closest" one, and we correct to it
     * (if there are no typos, the closest word is the word itself)
     *
     * */
    public static ArrayList<String> correctQuery(List<String> typos, WordMap map){
            ArrayList<String> correctedQuery = new ArrayList<>();
        for (String typo : typos){ // iterate through all possible typos
            String correctedWord = typo;
            int minDistance = typo.length();
            for (Map.Entry<String, FileMap> e : map.entrySet2()){ // for each typo, find its closest word
                String word = e.getKey();
                int LevenshteinDistance = editDistance(typo, word);
                if (LevenshteinDistance < minDistance){
                    minDistance = LevenshteinDistance;
                    correctedWord = word; // correct that word
                }
            }
            correctedQuery.add(correctedWord);
        }
        return correctedQuery;
    }
}