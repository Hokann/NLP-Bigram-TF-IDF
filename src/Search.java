import java.util.*;

import static java.lang.Math.log;

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
        for (String word : words){
            //System.out.println(word);
            ArrayList<Double> tfValues = TF(word);
            double idfValue = IDF(word);

            ArrayList<Double> tfidf = new ArrayList<>();
            for (Double tfValue : tfValues){
                tfidf.add(tfValue * idfValue);
            }
            //System.out.println(tfidf.toString());
            //System.out.println(tfidf.size());

            for (int i = 0; i < tfidf.size(); i++) {
                if (result.size() <= i) {
                    // If the result ArrayList is not long enough, add a new element
                    result.add(tfidf.get(i));
                } else {
                    // If the result ArrayList is long enough, accumulate the values
                    double sum = tfidf.get(i) + result.get(i);
                    result.set(i, sum);
                }
            }

        }
        //System.out.println(result.toString());
        return result;
    }

    public ArrayList<Double> TF(String word){
        ArrayList<Double> tf = new ArrayList<>();
            FileMap fileMap = wordMap.get(word);
            for (MapEntry<String, Integer> e2 : totalWords){
                //System.out.println(e2.getKey());
                boolean entryFound = false;
                for (Map.Entry<String, ArrayList<Integer>> e : fileMap.entrySet2()){
                    String documentName = e.getKey();
                    //System.out.println(documentName);
                    if (e2.getKey().equals(documentName)){
                        entryFound = true;
                        totalW = e2.getValue();
                        int countW = e.getValue().size();
                        //System.out.println(countW);
                        //System.out.println(totalW);
                        double tf1 = (double) countW / totalW;
                        tf.add(tf1); break;
                }
            }
                if (!entryFound) { tf.add(0.0); }
        }
        return tf;
    }

    public double IDF(String word) {
        int totalD = totalWords.size();
        ArrayList<Double> copy = new ArrayList<>();
        for (Double tf : TF(word)) {
            if (tf != 0.0) { copy.add(tf); }
        }
        int countDW = copy.size();
        return 1 + log( (double) (1 + totalD) / (1+countDW));

    }
}

