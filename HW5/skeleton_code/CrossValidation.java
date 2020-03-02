import java.util.ArrayList;
import java.util.List;

public class CrossValidation {
    /*
     * Returns the k-fold cross validation score of classifier clf on training data.
     */
    public static double kFoldScore(Classifier clf, List<Instance> trainData, int k, int v) {
        ArrayList<List<Instance>> sp = new ArrayList<List<Instance>>();
        int dataSize = trainData.size() / k;
        double ttRatio = 0;
        for (int i = 0; i < k - 1; i++) {
            sp.add(trainData.subList(dataSize * i, dataSize * (i + 1)));
        }
        sp.add(trainData.subList(dataSize * (k - 1), trainData.size()));
        for (int i = 0; i < k; i++) {
            List<Instance> test = sp.get(i);
            ArrayList<Instance> training = new ArrayList<Instance>();
            for (int j = 0; j < k; j++) {
                if (j != i) {
                    training.addAll(sp.get(j));
                }
            }
            clf.train(training, v);
            int pred = 0;
            for (int l = 0; l < test.size(); l++) {
                if (test.get(l).label == clf.classify(test.get(l).words).label) {
                    pred++;
                }
            }
            double r = (double) pred / test.size();
            ttRatio += r;
        }
        return ttRatio / k;
    }
}
