import edu.stanford.nlp.ling.*;

import java.util.*;
import java.lang.Math;

import static java.lang.Math.log;

public class TFIDF {

    private ArrayList<String> words;
    private ArrayList<MapEntry<String, Integer>> totalWords;
    private WordMap wordMap;
    private int totalW;
    public TFIDF(ArrayList<String> words, WordMap wordMap, ArrayList<MapEntry<String, Integer>> totalWords){
        this.words = words;
        this.wordMap = wordMap;
        this.totalWords = totalWords;
    }

    public ArrayList<Double> TF(String word){
        ArrayList<Double> tf = new ArrayList<>();
            FileMap fileMap = wordMap.get(word);
            for (Map.Entry<String, ArrayList<Integer>> e : fileMap.entrySet2()){
                String documentName = e.getKey();
                System.out.println(documentName);

                for (MapEntry<String, Integer> e2 : totalWords){
                    System.out.println(e2.getKey());
                    if (e2.getKey().equals(documentName)){
                        totalW = e2.getValue();
                    }
                }
                int countW = e.getValue().size();
                System.out.println(countW);
                System.out.println(totalW);
                double tf1 = (double) countW / totalW;
                tf.add(tf1);
            }
        return tf;
    }

    public double IDF(String word) {
        int totalD = totalWords.size();
        int countDW = TF(word).size();

        return 1 + log( (double) (1 + totalD) / (1+countDW));

    }
}

