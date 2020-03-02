import java.util.*;

/**
 * Fill in the implementation details of the class DecisionTree using this file. Any methods or
 * secondary classes that you want are fine but we will only interact with those methods in the
 * DecisionTree framework.
 */
public class DecisionTreeImpl {
    public DecTreeNode root;
    public List<List<Integer>> trainData;
    public int maxPerLeaf;
    public int maxDepth;
    public int numAttr;

    // Build a decision tree given a training set
    DecisionTreeImpl(List<List<Integer>> trainDataSet, int mPerLeaf, int mDepth) {
        this.trainData = trainDataSet;
        this.maxPerLeaf = mPerLeaf;
        this.maxDepth = mDepth;
        if (this.trainData.size() > 0) {
            this.numAttr = trainDataSet.get(0).size() - 1;
        }
        this.root = buildTree();
    }

    private DecTreeNode buildTree() {
        return buildTree(this.trainData, 0);
    }

    private DecTreeNode buildTree(List<List<Integer>> data, int depth) {
        Map<Integer, Integer> count = count(data);

        int numOfZero = count.get(0);
        int numOfOne = count.get(1);

        DecTreeNode node = checkStop(numOfOne, numOfZero, depth, data);
        if (node != null) {
            return node;
        }

        List<Integer> attributeList = chooseAttribute(data);

        int attribute = attributeList.get(0);
        int threshold = attributeList.get(1);

        List<List<List<Integer>>> dataSet = split(data, attribute, threshold);

        node = new DecTreeNode(0, attribute, threshold);
        node.right = buildTree(dataSet.get(1), depth + 1);
        node.left = buildTree(dataSet.get(0), depth + 1);
        return node;
    }

    private List<Integer> chooseAttribute(List<List<Integer>> data) {
        double entropy = calculateEntropy(data);
        double bestInfoGain = 0;
        int threshold = 0;
        int attribute = 0;

        for (int i = 0; i < this.numAttr; i++) {
            for (int j = 0; j < 10; j++) {
                int selectedThreshold = j + 1;
                double infoGain = entropy - splitDataEntropy(data, i, selectedThreshold);
                if (bestInfoGain < infoGain) {
                    threshold = selectedThreshold;
                    attribute = i;
                    bestInfoGain = infoGain;
                }
            }
        }

        List<Integer> result = new ArrayList<Integer>();
        result.add(attribute);
        result.add(threshold);
        return result;
    }

    private double splitDataEntropy(List<List<Integer>> data, int attribute, int threshold) {
        List<List<List<Integer>>> result = split(data, attribute, threshold);
        List<List<Integer>> left = result.get(0);
        List<List<Integer>> right = result.get(1);
        double entropy = 0;

        if (left.size() != 0) {
            entropy = entropy + ((double) left.size() / data.size()) * calculateEntropy(left);
        }

        if (right.size() != 0) {
            entropy = entropy + ((double) right.size() / data.size()) * calculateEntropy(right);
        }

        return entropy;
    }

    private double calculateEntropy(List<List<Integer>> data) {
        double entropy = 0;
        Map<Integer, Integer> count = count(data);

        double numOfZero = count.get(0);
        double numOfOne = count.get(1);
        double sum = numOfZero + numOfOne;

        if (numOfZero != 0) {
            double probability = numOfZero / sum;
            entropy = entropy - probability * Math.log(probability) / Math.log(2);
        }

        if (numOfOne != 0) {
            double probability = numOfOne / sum;
            entropy = entropy - probability * Math.log(probability) / Math.log(2);
        }

        return entropy;
    }


    private List<List<List<Integer>>> split(List<List<Integer>> data, int attribute, int threshold) {
        List<List<List<Integer>>> result = new ArrayList<>();
        List<List<Integer>> lessThanEqual = new ArrayList<>();
        List<List<Integer>> greatThan = new ArrayList<>();
        result.add(lessThanEqual);
        result.add(greatThan);
        for (List<Integer> each : data) {
            if (each.get(attribute) <= threshold) {
                lessThanEqual.add(each);
            }
            if (each.get(attribute) > threshold) {
                greatThan.add(each);
            }
        }
        return result;
    }

    private DecTreeNode checkStop(int numOfOne, int numOfZero, int depth, List<List<Integer>> data) {
        if (numOfOne == 0) {
            return new DecTreeNode(0, 0, 0);
        }
        if (numOfZero == 0) {
            return new DecTreeNode(1, 0, 0);
        }
        if (depth >= this.maxDepth || data.size() <= this.maxPerLeaf) {
            if (numOfZero <= numOfOne) {
                return new DecTreeNode(1, 0, 0);
            }
            if (numOfZero > numOfOne) {
                return new DecTreeNode(0, 0, 0);
            }
        }
        return null;
    }

    private Map<Integer, Integer> count(List<List<Integer>> data) {
        Map<Integer, Integer> label = new HashMap<Integer, Integer>();
        label.put(1, 0);
        label.put(0, 0);
        for (List<Integer> each : data) {
            int value = each.get(each.size() - 1);
            if (!label.containsKey(value)) {
                label.put(value, 0);
            }
            label.put(value, label.get(value) + 1);
        }
        return label;
    }

    public int classify(List<Integer> instance) {
        // Note that the last element of the array is the label.
        return classify(instance, root);
    }

    private int classify(List<Integer> instance, DecTreeNode treeNode) {
        if (treeNode.isLeaf()) {
            return treeNode.classLabel;
        }

        int value = instance.get(treeNode.attribute);

        DecTreeNode child;

        if (value <= treeNode.threshold) {
            child = treeNode.left;
        } else {
            child = treeNode.right;
        }

        return classify(instance, child);
    }


    // Print the decision tree in the specified format
    public void printTree() {
        printTreeNode("", this.root);
    }

    public void printTreeNode(String prefixStr, DecTreeNode node) {
        String printStr = prefixStr + "X_" + node.attribute;
        System.out.print(printStr + " <= " + String.format("%d", node.threshold));
        if (node.left.isLeaf()) {
            System.out.println(" : " + String.valueOf(node.left.classLabel));
        } else {
            System.out.println();
            printTreeNode(prefixStr + "|\t", node.left);
        }
        System.out.print(printStr + " > " + String.format("%d", node.threshold));
        if (node.right.isLeaf()) {
            System.out.println(" : " + String.valueOf(node.right.classLabel));
        } else {
            System.out.println();
            printTreeNode(prefixStr + "|\t", node.right);
        }
    }

    public double printTest(List<List<Integer>> testDataSet) {
        int numEqual = 0;
        int numTotal = 0;
        for (int i = 0; i < testDataSet.size(); i++) {
            int prediction = classify(testDataSet.get(i));
            int groundTruth = testDataSet.get(i).get(testDataSet.get(i).size() - 1);
            System.out.println(prediction);
            if (groundTruth == prediction) {
                numEqual++;
            }
            numTotal++;
        }
        double accuracy = numEqual * 100.0 / (double) numTotal;
        System.out.println(String.format("%.2f", accuracy) + "%");
        return accuracy;
    }
}
