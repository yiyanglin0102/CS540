import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Your implementation of a naive bayes classifier. Please implement all four methods.
 */

public class NaiveBayesClassifier implements Classifier {

    private int vSize;

    private Map<Label, Integer> wordsCountPerLabel;

    private Map<Label, Integer> documentsPerLabel;

    private Map<String, Integer> negWordsCount;

    private Map<String, Integer> posWordsCount;

    private int numDocuments;


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
        wordsCountPerLabel = getWordsCountPerLabel(trainData);
        documentsPerLabel = getDocumentsCountPerLabel(trainData);

        negWordsCount = new HashMap<>();
        posWordsCount = new HashMap<>();
        for (Instance instance : trainData) {
            if (instance.label==Label.NEGATIVE) {
                for (String word : instance.words) {
                    if (negWordsCount.containsKey(word)) {
                        negWordsCount.put(word, negWordsCount.get(word) + 1);
                    } else {
                        negWordsCount.put(word, 1);
                    }
                }
            } else {
                for (String word: instance.words) {
                    if (posWordsCount.containsKey(word)) {
                        posWordsCount.put(word, posWordsCount.get(word) + 1);
                    } else {
                        posWordsCount.put(word, 1);
                    }
                }
            }
        }

        this.vSize = v;
        this.numDocuments = trainData.size();
    }

    /*
     * Counts the number of words for each label
     */
    @Override
    public Map<Label, Integer> getWordsCountPerLabel(List<Instance> trainData) {
        Map<Label, Integer> map = new HashMap<>();
        for (Instance instance : trainData) {
            if (map.containsKey(instance.label)) {
                map.put(instance.label, map.get(instance.label) + instance.words.size());
            } else {
                map.put(instance.label, instance.words.size());
            }
        }
        return map;
    }


    /*
     * Counts the total number of documents for each label
     */
    @Override
    public Map<Label, Integer> getDocumentsCountPerLabel(List<Instance> trainData) {
        Map<Label, Integer> map = new HashMap<>();
        for (Instance instance : trainData) {
            if (map.containsKey(instance.label)) {
                map.put(instance.label, map.get(instance.label) + 1);
            } else {
                map.put(instance.label, 1);
            }
        }
        return map;
    }


    /**
     * Returns the prior probability of the label parameter, i.e. P(POSITIVE) or P(NEGATIVE)
     */
    private double p_l(Label label) {
        // TODO : Implement
        // Calculate the probability for the label. No smoothing here.
        // Just the number of label counts divided by the number of documents.
        return Math.log((double)documentsPerLabel.get(label) / numDocuments);
    }

    /**
     * Returns the smoothed conditional probability of the word given the label, i.e. P(word|POSITIVE) or
     * P(word|NEGATIVE)
     */
    private double p_w_given_l(String word, Label label) {
        // TODO : Implement
        // Calculate the probability with Laplace smoothing for word in class(label)
        double delta = 1.0;
        if (label == Label.NEGATIVE){
            double up = (negWordsCount.get(word)==null?0:negWordsCount.get(word)) + delta;
            double down = vSize * delta + wordsCountPerLabel.get(label);
            return Math.log(up/down);
        }else {
            double up = (posWordsCount.get(word)==null?0:posWordsCount.get(word)) + delta;
            double down = vSize * delta + wordsCountPerLabel.get(label);
            return Math.log(up/down);
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
        ClassifyResult result = new ClassifyResult();
        Map<Label, Double> logProbPerLabel = new HashMap<>();
        Label predict = null;
        double prob = -99999999;
        for (Label label: Label.values()){
            double p = p_l(label);
            for (String word: words){
                p += p_w_given_l(word, label);
            }
            logProbPerLabel.put(label, p);
            if (p > prob){
                prob = p;
                predict = label;
            }
        }
        result.label = predict;
        result.logProbPerLabel = logProbPerLabel;
        return result;
    }


}
