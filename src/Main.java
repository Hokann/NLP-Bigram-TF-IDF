import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;

import java.io.*;
import java.util.*;

public class Main {
        public static void main(String[] args) throws IOException {

            WordMap wordMap = new WordMap();

            File folder = new File("src/dataset");
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles)
            {
                if(file.isFile())
                {
                    BufferedReader br=new BufferedReader(new FileReader(new
                            File("src/dataset"+"/"+file.getName())));
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
                    System.out.println(str);

                    String[] words = str.split("\\s+");
                    System.out.println(Arrays.toString(words));

                    for ( int index = 0; index < words.length; index++){
                        System.out.println(index);
                        ArrayList i = new ArrayList(1); i.add(index);

                        if (wordMap.get(words[index]) == null){
                            System.out.println("new word");
                            FileMap fileMap = new FileMap();
                            fileMap.put(file.getName(), i);
                            System.out.println("filemap for word success");
                            wordMap.put(words[index], fileMap);
                            System.out.println("wordmap entry for word success");
                        }
                        else{
                        wordMap.get(words[index]).put(file.getName(), i);
                    }
                    }
                }
            }

            //START
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


    }
}