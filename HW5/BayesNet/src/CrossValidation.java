import java.util.ArrayList;
import java.util.List;

public class CrossValidation {
    /*
     * Returns the k-fold cross validation score of classifier clf on training data.
     */
    public static double kFoldScore(Classifier clf, List<Instance> trainData, int k, int v) {
        // TODO : Implement
        List<List<Instance>> splits = new ArrayList<>();
        int size = trainData.size() / k;
        for (int i = 0; i < k - 1; i++) {
            splits.add(trainData.subList(i * size, (i + 1) * size));
        }
        splits.add(trainData.subList((k - 1) * size, trainData.size()));

        double totalRatio = 0;
        for (int i = 0; i < k; i++) {
            List<Instance> test = splits.get(i);
            List<Instance> train = new ArrayList<>();
            for (int j = 0; j < k; j++) {
                if (j != i) {
                    train.addAll(splits.get(j));
                }
            }
            clf.train(train, v);

            int predictCorrects = 0;
            for (Instance instance : test) {
                if (instance.label == clf.classify(instance.words).label) {
                    predictCorrects++;
                }
            }
            double ratio = (double) predictCorrects / test.size();
            totalRatio += ratio;
        }
        return totalRatio / k;
    }
}
