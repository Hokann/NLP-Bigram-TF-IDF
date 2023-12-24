import java.io.*;
import java.util.*;

public class Bigram {
    private WordMap wordMap;
    private String w1;

    public Bigram(String w1, WordMap wordMap) {
        this.wordMap = wordMap;
        this.w1 = w1;
    }

    public ArrayList<MapEntry<String, Integer>> bigramOf(String w1) {
        ArrayList<MapEntry<String, ArrayList<Integer>>> w1Positions = new ArrayList<>();
        ArrayList<MapEntry<String, Integer>> coOccurenceList = new ArrayList<>();
        // C(w1) all the counts of w1 within all documents
        int CW1 = 0;
        FileMap w1FileMap = wordMap.get(w1);
        for (Map.Entry<String, ArrayList<Integer>> entry : w1FileMap.entrySet2()) {
            String documentName = entry.getKey();
            ArrayList<Integer> documentPositions = entry.getValue();
            w1Positions.add(new MapEntry<>(documentName, documentPositions));

            for (Integer position : documentPositions) { // position of each w1
                Integer bigramPos = position + 1;
                System.out.println("bigram pos = " + bigramPos + " in file = " + documentName);
                String bigramWord = getWord1(documentName, bigramPos);

                /*
                for (Map.Entry<String, FileMap> entry2 : wordMap.entrySet2()) { // go through each word
                    ArrayList<Integer> e3 = entry2.getValue().get(documentName);
                    if (e3 != null && e3.contains(bigramPos)){
                        for (Integer pos : e3){
                            if (pos == bigramPos){System.out.println(entry2.getKey());};
                        }
                    }

                    int CW = 0;
                    MapEntry<String, Integer> mapEntry = new MapEntry(entry.getKey(), CW);
                    for (Map.Entry<String, ArrayList<Integer>> e : entry2.getValue().entrySet2()) { // go through each fileMap
                        if (entry.getKey() == e.getKey()) { // same document
                            if (e.getValue().contains(bigramPos)) {
                                mapEntry.setValue(CW++);
                            }

                        }
                    }
                    coOccurenceList.add(mapEntry);
                }
            }
            CW1 += entry.getValue().size();
        }
        System.out.println(CW1);
    }*/
            }
        }
        return coOccurenceList;

    }
    public String getWord1(String documentName, Integer bigramPos) {
        for (Map.Entry<String, FileMap> entry : wordMap.entrySet2()) {
            FileMap entryFileMap = entry.getValue();
            if (entryFileMap.containsKey(documentName)) {
                ArrayList<Integer> entryIntList = entryFileMap.get(documentName);

                // Check if bigramPos is within the range of positions
                if (!entryIntList.isEmpty() && bigramPos >= entryIntList.get(0) && bigramPos <= entryIntList.get(entryIntList.size() - 1)) {
                    // Iterate through positions to find the word at the specified position
                    for (int i = 0; i < entryIntList.size(); i++) {
                        if (entryIntList.get(i) == bigramPos) {
                            System.out.println("TRUE : " + entry.getKey());
                            return entry.getKey();
                        } else if (entryIntList.get(i) > bigramPos) {
                            // If the current position is greater than bigramPos, break the loop
                            break;
                        }
                    }
                }
            }
        }
        return null; // Return null if the word is not found
    }

    public String getWord(String documentName, Integer bigramPos){
        for (Map.Entry<String, FileMap> entry : wordMap.entrySet2()) { // go through each word
            FileMap entryFileMap = entry.getValue();
            if (entryFileMap.containsKey(documentName)){
                ArrayList<Integer> entryIntList = entryFileMap.get(documentName);
                System.out.println(entryIntList.toString());
                if (entryIntList.contains(bigramPos)){
                    System.out.println("TRUE : "+entry.getKey());
                    return entry.getKey();
                }
            }
        }
        return "f";
    }
}