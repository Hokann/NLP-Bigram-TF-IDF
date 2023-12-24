import java.io.*;
import java.util.*;

public class Bigram {
    private WordMap wordMap;
    private String w1;

    public Bigram(String w1, WordMap wordMap) {
        this.wordMap = wordMap;
        this.w1 = w1;
    }

    public String bigramOf(String w1){
        ArrayList<MapEntry<String, Double>> possibleBigrams = coOccurrences(w1); // list of possible bigrams
        Double countW1 = possibleBigrams.removeLast().getValue(); // C(W1)

        for (MapEntry<String, Double> bigram : possibleBigrams){
            bigram.setValue(bigram.getValue() / countW1); // calculate probability of observing w1
        }

        double bestProba = 0.0; String bestBigram = "";
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
        ArrayList<MapEntry<String, Double>> coOccurrenceList = new ArrayList<>();
        // C(w1), all the counts of w1 within all documents
        double CW1 = 0.0;
        FileMap w1FileMap = wordMap.get(w1);
        for (Map.Entry<String, ArrayList<Integer>> entry : w1FileMap.entrySet2()) {
            String documentName = entry.getKey();
            ArrayList<Integer> documentPositions = entry.getValue();
            w1Positions.add(new MapEntry<>(documentName, documentPositions));

            for (Integer position : documentPositions) { // position of each w1
                Integer bigramPos = position + 1;
                //System.out.println("bigram pos = " + bigramPos + " in file = " + documentName);
                String bigramWord = getWord1(documentName, bigramPos);
                if (bigramWord == null) {  break; }
                //MapEntry<String, Integer> mapEntry = new MapEntry<>(bigramWord, 1);
                // Check if the bigram is already in coOccurrenceList
                boolean found = false;
                for (MapEntry<String, Double> coOccurrenceEntry : coOccurrenceList) {
                    if (coOccurrenceEntry.getKey().equals(bigramWord) && bigramWord != null) {
                        // If found, increment the count
                        coOccurrenceEntry.setValue(coOccurrenceEntry.getValue() + 1);
                        found = true;
                        break;
                    }
                }
                // If not found, add a new entry
                if (!found) {
                    coOccurrenceList.add(new MapEntry<>(bigramWord, 1.0));
                }
            }
            CW1 += entry.getValue().size();
        }
        coOccurrenceList.add(new MapEntry<>("W1 OCCURENCES", CW1));
        return coOccurrenceList;
    }
    public String getWord1(String documentName, Integer bigramPos) {
        for (Map.Entry<String, FileMap> entry : wordMap.entrySet2()) {
            FileMap entryFileMap = entry.getValue();
            if (entryFileMap.containsKey(documentName)) {
                ArrayList<Integer> entryIntList = entryFileMap.get(documentName);

                // Check if bigramPos is within the range of positions
               // if (!entryIntList.isEmpty() && bigramPos >= entryIntList.get(0) && bigramPos <= entryIntList.get(entryIntList.size() - 1)) {
                    // Iterate through positions to find the word at the specified position
                    for (int i = 0; i < entryIntList.size(); i++) {
                        if (entryIntList.get(i).equals(bigramPos)) {
                            //System.out.println("TRUE : " + entry.getKey());
                            return entry.getKey();
                        } else if (entryIntList.get(i) > bigramPos) {
                            // If the current position is greater than bigramPos, break the loop
                            break;
                        }
                    }
                //}
            }
        }
        return null; // Return null if the word is not found
    }

}