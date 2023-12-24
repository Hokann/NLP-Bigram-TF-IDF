import java.util.*;

import static java.lang.Math.log;

/**
 * NLP operation that for a given query of words, finds the most relevant document
 * Relevancy is calculated by the TF-IDF, which the 3 methods below do.
 *
 * @author Hokan Gillot (20242295)
 * */
public class Search {

    private ArrayList<String> words;
    private ArrayList<MapEntry<String, Integer>> totalWords;
    private WordMap wordMap;
    private int totalW;
    public Search(ArrayList<String> words, WordMap wordMap, ArrayList<MapEntry<String, Integer>> totalWords){
        this.words = words;
        this.wordMap = wordMap;
        this.totalWords = totalWords;
    }

    public ArrayList<Double> TFIDF(){
        ArrayList<Double> result = new ArrayList<>();
        // for each word in the query, we find its TF-IDF for all documents
        for (String word : words){
            ArrayList<Double> tfValues = TF(word);
            double idfValue = IDF(word);

            ArrayList<Double> tfidf = new ArrayList<>();
            for (Double tfValue : tfValues){
                tfidf.add(tfValue * idfValue); // TF-IDF = TF * IDF
            }

            // add TF-IDF values if words share a document
            for (int i = 0; i < tfidf.size(); i++) {
                if (result.size() <= i) {
                    // if the result ArrayList is not long enough, add a new element
                    result.add(tfidf.get(i));
                } else {
                    // if the result ArrayList is long enough, accumulate the values
                    double sum = tfidf.get(i) + result.get(i);
                    result.set(i, sum);
                }
            }

        }
        return result;
    }

    // TF is the ration between count(w) : the number of times the word w appears in a document
    // and totalW : the total number of words in the document
    public ArrayList<Double> TF(String word){
        ArrayList<Double> tf = new ArrayList<>();
            FileMap fileMap = wordMap.get(word);
            for (MapEntry<String, Integer> e2 : totalWords){
                boolean entryFound = false;
                for (Map.Entry<String, ArrayList<Integer>> e : fileMap.entrySet2()){
                    String documentName = e.getKey();
                    if (e2.getKey().equals(documentName)){
                        entryFound = true;

                        // get the ratio count(w)/totalW
                        totalW = e2.getValue();
                        int countW = e.getValue().size();
                        double tf1 = (double) countW / totalW;
                        tf.add(tf1); break;
                }
            }
                if (!entryFound) { tf.add(0.0); }
        }
        return tf;
    }

    public double IDF(String word) {
        int totalD = totalWords.size(); // number of documents
        ArrayList<Double> copy = new ArrayList<>(); // we use a copy to not alter the real list of TF
        // number of documents with the word in it
        for (Double tf : TF(word)) {
            if (tf != 0.0) { copy.add(tf); }
        }
        int countDW = copy.size();
        return 1 + log( (double) (1 + totalD) / (1+countDW)); // java.lang.Math's log is in base e already

    }
}

