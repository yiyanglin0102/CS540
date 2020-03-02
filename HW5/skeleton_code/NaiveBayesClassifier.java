import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifier implements Classifier {
    private int vSize;
    private int numDoc;
    private Map<String, Integer> nCount;
    private Map<String, Integer> pCount;
    private Map<Label, Integer> wLabel;
    private Map<Label, Integer> docLabel;

    /**
     * Trains the classifier with the provided training data and vocabulary size
     */
    @Override
    public void train(List<Instance> trainData, int v) {
        // TODO : Implement
        // Hint: First, calculate the documents and words counts per label and store them. 
        // Then, for all the words in the documents of each label, count the number of occurrences of each word.
        // Save these information as you will need them to calculate the log probabilities later.
        //
        // e.g.
        // Assume m_map is the map that stores the occurrences per word for positive documents
        // m_map.get("catch") should return the number of "catch" es, in the documents labeled positive
        // m_map.get("asdasd") would return null, when the word has not appeared before.
        // Use m_map.put(word,1) to put the first count in.
        // Use m_map.replace(word, count+1) to update the value
        nCount = new HashMap<String, Integer>();
        pCount = new HashMap<String, Integer>();
        docLabel = getDocumentsCountPerLabel(trainData);
        wLabel = getWordsCountPerLabel(trainData);
        for (Instance ins : trainData) {
            if (ins.label != Label.NEGATIVE) {
                for (String w : ins.words) {
                    if (pCount.containsKey(w)) {
                        pCount.put(w, pCount.get(w) + 1);
                    } else {
                        pCount.put(w, 1);
                    }
                }
            } else {
                for (String w : ins.words) {
                    if (nCount.containsKey(w)) {
                        nCount.put(w, nCount.get(w) + 1);
                    } else {
                        nCount.put(w, 1);
                    }
                }
            }
        }
        this.numDoc = trainData.size();
        this.vSize = v;
    }

    /*
     * Counts the number of words for each label
     */
    @Override
    public Map<Label, Integer> getWordsCountPerLabel(List<Instance> trainData) {
        Map<Label, Integer> mappingLabelSize = new HashMap<>();
        for (Instance ins : trainData) {
            if (mappingLabelSize.containsKey(ins.label)) {
                mappingLabelSize.put(ins.label, ins.words.size() + mappingLabelSize.get(ins.label));
            } else {
                mappingLabelSize.put(ins.label, ins.words.size());
            }
        }
        return mappingLabelSize;
    }


    /*
     * Counts the total number of documents for each label
     */
    @Override
    public Map<Label, Integer> getDocumentsCountPerLabel(List<Instance> trainData) {
        Map<Label, Integer> mappingLabelCount = new HashMap<>();
        for (Instance instance : trainData) {
            if (mappingLabelCount.containsKey(instance.label)) {
                mappingLabelCount.put(instance.label, mappingLabelCount.get(instance.label) + 1);
            } else {
                mappingLabelCount.put(instance.label, 1);
            }
        }
        return mappingLabelCount;
    }


    /**
     * Returns the prior probability of the label parameter, i.e. P(POSITIVE) or P(NEGATIVE)
     */
    private double p_l(Label label) {
        // TODO : Implement
        // Calculate the probability for the label. No smoothing here.
        // Just the number of label counts divided by the number of documents.
        double pLabel = Math.log((double) docLabel.get(label) / numDoc);
        return pLabel;
    }

    /**
     * Returns the smoothed conditional probability of the word given the label, i.e. P(word|POSITIVE) or
     * P(word|NEGATIVE)
     */
    private double p_w_given_l(String word, Label label) {
        // TODO : Implement
        // Calculate the probability with Laplace smoothing for word in class(label)
        double del = 1.0;
        double u;
        double d;
        if (label == Label.NEGATIVE) {
            if (nCount.get(word) != null) {
                u = del + nCount.get(word);
            } else {
                u = del;
            }
            d = vSize * del + wLabel.get(label);
            return Math.log(u / d);
        } else {
            if (pCount.get(word) != null) {
                u = del + pCount.get(word);
            } else {
                u = del;
            }
            d = vSize * del + wLabel.get(label);
            return Math.log(u / d);
        }
    }

    /**
     * Classifies an array of words as either POSITIVE or NEGATIVE.
     */
    @Override
    public ClassifyResult classify(List<String> words) {
        // TODO : Implement
        // Sum up the log probabilities for each word in the input data, and the probability of the label
        // Set the label to the class with larger log probability
        double proba = Integer.MIN_VALUE;
        Label pred = null;
        ClassifyResult returnVal = new ClassifyResult();
        Map<Label, Double> lgPbLabel = new HashMap<Label, Double>();
        for (Label l : Label.values()) {
            double p = p_l(l);
            for (String w : words) {
                p = p_w_given_l(w, l) + p;
            }
            lgPbLabel.put(l, p);
            if (p > proba) {
                pred = l;
                proba = p;
            }
        }
        returnVal.label = pred;
        returnVal.logProbPerLabel = lgPbLabel;
        return returnVal;
    }
}
