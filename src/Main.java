import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import java.util.Properties;


public class Main {
        public static String text = "Joe Smith wasn't born in California. " + "In 2017, he went to his car, sister's car Paris, France in the summer. " + "His flight left at 3:00pm on July 10th, 2017. " + "After eating some escargot for the first time, Joe said, \"That was delicious!\" " + "He sent a postcard to his sister Jane Smith. " + "After hearing about Joe's trip, Jane decided she might go to France one day.";
        public static void main(String[] args) {
// set up pipeline properties
            Properties props = new Properties();
// set the list of annotators to run
            props.setProperty("annotators", "tokenize,pos,lemma");
// set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
            props.setProperty("coref.algorithm", "neural");
// build pipeline
            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
// create a document object
            CoreDocument document = new CoreDocument(text);

            pipeline.annotate(document);
//System.out.println(document.tokens());
            for (CoreLabel tok : document.tokens()) {
                System.out.println(String.format("%s\t%s",
                        tok.word(), tok.lemma()));
            }

        }
}