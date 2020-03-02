import java.util.*;

/**
 * The main class that handles the entire network
 * Has multiple attributes each with its own use
 */

public class NNImpl {
    private ArrayList<Node> inputNodes; //list of the output layer nodes.
    private ArrayList<Node> hiddenNodes;    //list of the hidden layer nodes
    private ArrayList<Node> outputNodes;    // list of the output layer nodes

    private ArrayList<Instance> trainingSet;    //the training set

    private double learningRate;    // variable to store the learning rate
    private int maxEpoch;   // variable to store the maximum number of epochs
    private Random random;  // random number generator to shuffle the training set

    /**
     * This constructor creates the nodes necessary for the neural network
     * Also connects the nodes of different layers
     * After calling the constructor the last node of both inputNodes and
     * hiddenNodes will be bias nodes.
     */

    NNImpl(ArrayList<Instance> trainingSet, int hiddenNodeCount, Double learningRate, int maxEpoch, Random random, Double[][] hiddenWeights, Double[][] outputWeights) {
        this.trainingSet = trainingSet;
        this.learningRate = learningRate;
        this.maxEpoch = maxEpoch;
        this.random = random;

        //input layer nodes
        inputNodes = new ArrayList<>();
        int inputNodeCount = trainingSet.get(0).attributes.size();
        int outputNodeCount = trainingSet.get(0).classValues.size();
        for (int i = 0; i < inputNodeCount; i++) {
            Node node = new Node(0);
            inputNodes.add(node);
        }

        //bias node from input layer to hidden
        Node biasToHidden = new Node(1);
        inputNodes.add(biasToHidden);

        //hidden layer nodes
        hiddenNodes = new ArrayList<>();
        for (int i = 0; i < hiddenNodeCount; i++) {
            Node node = new Node(2);
            //Connecting hidden layer nodes with input layer nodes
            for (int j = 0; j < inputNodes.size(); j++) {
                NodeWeightPair nwp = new NodeWeightPair(inputNodes.get(j), hiddenWeights[i][j]);
                node.parents.add(nwp);
            }
            hiddenNodes.add(node);
        }

        //bias node from hidden layer to output
        Node biasToOutput = new Node(3);
        hiddenNodes.add(biasToOutput);

        //Output node layer
        outputNodes = new ArrayList<>();
        for (int i = 0; i < outputNodeCount; i++) {
            Node node = new Node(4);
            //Connecting output layer nodes with hidden layer nodes
            for (int j = 0; j < hiddenNodes.size(); j++) {
                NodeWeightPair nwp = new NodeWeightPair(hiddenNodes.get(j), outputWeights[i][j]);
                node.parents.add(nwp);
            }
            outputNodes.add(node);
        }
    }

    /**
     * Get the prediction from the neural network for a single instance
     * Return the idx with highest output values. For example if the outputs
     * of the outputNodes are [0.1, 0.5, 0.2], it should return 1.
     * The parameter is a single instance
     */

    public int predict(Instance instance) {
        for (int i = 0; i < instance.attributes.size(); i++) {
            inputNodes.get(i).setInput(instance.attributes.get(i));
        }

        for (Node n : hiddenNodes) {
            n.calculateOutput();
        }
        for (Node n : outputNodes) {
            n.calculateOutput();
        }
        double sum = 0;
        for (Node n : outputNodes) {
            sum = sum + Math.exp(n.getOutput());
        }
        for (Node n : outputNodes) {
            n.calculateSM(sum);
        }
        double max = Double.MIN_VALUE;
        int check = -1;
        for (int i = outputNodes.size() - 1; i >= 0; i--) {
            if (outputNodes.get(i).getOutput() > max) {
                max = outputNodes.get(i).getOutput();
                check = i;
            }
        }
        return check;
    }


    /**
     * Train the neural networks with the given parameters
     * <p>
     * The parameters are stored as attributes of this class
     */

    public void train() {
        for (int i = 0; i < maxEpoch; i++) {
            Collections.shuffle(trainingSet, random);
            for (Instance ins : trainingSet) {
                for (int j = 0; j < ins.attributes.size(); j++) {
                    inputNodes.get(j).setInput(ins.attributes.get(j));
                }

                for (Node n : hiddenNodes) {
                    n.calculateOutput();
                }
                for (Node n : outputNodes) {
                    n.calculateOutput();
                }
                double sum = 0;
                for (Node n : outputNodes) {
                    sum += Math.exp(n.getOutput());
                }
                for (Node n : outputNodes) {
                    n.calculateSM(sum);
                }
                for (int j = 0; j < outputNodes.size(); j++) {
                    outputNodes.get(j).calculateDelta(this, ins.classValues.get(j));
                }

                for (int j = 0; j < hiddenNodes.size(); j++) {
                    hiddenNodes.get(j).calculateDelta(this, 0);
                }

                // updating w
                for (Node node : outputNodes) {
                    node.updateWeight(learningRate);
                }

                for (Node node : hiddenNodes) {
                    node.updateWeight(learningRate);
                }
            }

            double loss = 0;
            for (Instance ins : trainingSet) {
                for (int j = 0; j < ins.attributes.size(); j++) {
                    inputNodes.get(j).setInput(ins.attributes.get(j));
                }

                for (Node n : hiddenNodes) {
                    n.calculateOutput();
                }
                for (Node n : outputNodes) {
                    n.calculateOutput();
                }

                double s = 0;
                for (Node node : outputNodes) {
                    s += Math.exp(node.getOutput());
                }
                for (Node node : outputNodes) {
                    node.calculateSM(s);
                }
                loss += loss(ins);
            }
            loss = loss / trainingSet.size();
            System.out.printf("Epoch: %d, Loss: %.3e\n", i, loss);
        }
    }

    /**
     * Calculate the cross entropy loss from the neural network for
     * a single instance.
     * The parameter is a single instance
     */
    private double loss(Instance instance) {
        double l = 0;
        for (int i = 0; i < outputNodes.size(); i++) {
            l = l + Math.log(outputNodes.get(i).getOutput()) * instance.classValues.get(i);
        }
        return -l;
    }

    public List<Node> output() {
        return outputNodes;
    }
}
