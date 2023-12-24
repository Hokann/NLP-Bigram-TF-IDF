import java.io.*;
import java.util.*;

/**
 * Bigram is an NLP operation which takes a word, and finds the most probable following word. (used in autocomplete for example)
 * We define 3 methods to allow us to determine the bigram of a word (w1)
 *
 * @author Hokan Gillot (20242295)
 * */
public class Bigram {
    private WordMap wordMap;
    public Bigram(WordMap wordMap) {
        this.wordMap = wordMap;
    }

    // Main method which will compute the probability of observing w1 for all possible bigrams, then choosing the bigram
    // with the highest probability
    public String bigramOf(String w1){
        ArrayList<MapEntry<String, Double>> possibleBigrams = coOccurrences(w1); // list of possible bigrams
        Double countW1 = possibleBigrams.removeLast().getValue(); // total count of w1 in the dataset

        for (MapEntry<String, Double> bigram : possibleBigrams){
            bigram.setValue(bigram.getValue() / countW1); // calculate probability of observing w1 for each bigram
        }

        // Find the bigram with the highest probability, if we find 2 bigrams with the same probabilities, we take
        // the smallest in lexicographic order
        double bestProba = 0.0; String bestBigram = ""; // init
        for (MapEntry<String, Double> bigram : possibleBigrams){
            if (bigram.getValue() > bestProba){
                bestProba = bigram.getValue();
                bestBigram = bigram.getKey();
            }else if (bigram.getValue() == bestProba){
                if (bigram.getKey().compareTo(bestBigram) < 0 ){ // compare lexicographic order if same value
                    bestBigram = bigram.getKey();
                }
            }
        }
        return bestBigram;
    }

    public ArrayList<MapEntry<String, Double>> coOccurrences(String w1) {
        ArrayList<MapEntry<String, ArrayList<Integer>>> w1Positions = new ArrayList<>();
        // list to keep track of bigrams and the number co-occurrences (occurences of w1 followed by the bigram)
        ArrayList<MapEntry<String, Double>> coOccurrenceList = new ArrayList<>();
        // C(w1), all the counts of w1 within all documents
        double CW1 = 0.0;
        FileMap w1FileMap = wordMap.get(w1);
        for (Map.Entry<String, ArrayList<Integer>> entry : w1FileMap.entrySet2()) {
            String documentName = entry.getKey();
            ArrayList<Integer> documentPositions = entry.getValue();
            w1Positions.add(new MapEntry<>(documentName, documentPositions));

            for (Integer position : documentPositions) { // iterate through each position of w1
                Integer bigramPos = position + 1; // the bigrams position is just +1

                String bigramWord = getWord(documentName, bigramPos); // find the corresponding word
                if (bigramWord == null) {  break; }
                // Check if the bigram is already in coOccurrenceList
                boolean found = false;
                for (MapEntry<String, Double> coOccurrenceEntry : coOccurrenceList) {
                    if (coOccurrenceEntry.getKey().equals(bigramWord) && bigramWord != null) {
                        // If so, increment the count
                        coOccurrenceEntry.setValue(coOccurrenceEntry.getValue() + 1);
                        found = true;
                        break;
                    }
                }
                // If not, new entry, and we mark its 1st occurence
                if (!found) {
                    coOccurrenceList.add(new MapEntry<>(bigramWord, 1.0)); // Using double makes it easier to calculate probailities later on
                }
            }
            CW1 += entry.getValue().size();
        }
        // we include the total count of w1 at the end, just so we have access to it later on
        coOccurrenceList.add(new MapEntry<>("W1 OCCURENCES", CW1));
        return coOccurrenceList;
    }

    // Find a word if given only its position within a document
    public String getWord(String documentName, Integer bigramPos) {
        for (Map.Entry<String, FileMap> entry : wordMap.entrySet2()) { // iterate through all words
            FileMap entryFileMap = entry.getValue();
            if (entryFileMap.containsKey(documentName)) { // we find the word which is in the document
                ArrayList<Integer> entryIntList = entryFileMap.get(documentName);

                    // Within that document, iterate through the position to try to find the bigramPos
                    for (int i = 0; i < entryIntList.size(); i++) {
                        if (entryIntList.get(i).equals(bigramPos)) {
                            return entry.getKey();
                        } else if (entryIntList.get(i) > bigramPos) {
                            // if current position greater than bigramPos, break
                            break;
                        }
                    }
            }
        }
        return null; // Return null if the word is not found
    }

}