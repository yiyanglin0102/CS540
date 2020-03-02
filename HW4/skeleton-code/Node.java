import java.util.*;

/**
 * Class for internal organization of a Neural Network.
 * There are 5 types of nodes. Check the type attribute of the node for details.
 * Feel free to modify the provided function signatures to fit your own implementation
 */

public class Node {
    private int type = 0; //0=input,1=biasToHidden,2=hidden,3=biasToOutput,4=Output
    public ArrayList<NodeWeightPair> parents = null; //Array List that will contain the parents (including the bias node) with weights if applicable

    private double inputValue = 0.0;
    private double outputValue = 0.0;
    private double outputGradient = 0.0;
    private double delta = 0.0; //input gradient

    //Create a node with a specific type
    Node(int type) {
        if (type > 4 || type < 0) {
            System.out.println("Incorrect value for node type");
            System.exit(1);

        } else {
            this.type = type;
        }

        if (type == 2 || type == 4) {
            parents = new ArrayList<NodeWeightPair>();
        }
    }

    //For an input node sets the input value which will be the value of a particular attribute
    public void setInput(double inputValue) {
        if (type == 0) {    //If input node
            this.inputValue = inputValue;
        }
    }

    /**
     * Calculate the output of a node.
     * You can get this value by using getOutput()
     */
    public void calculateOutput() {
        if (type == 2 || type == 4) {
            // TODO: add code here
            double outputLayer = 0;
            for (NodeWeightPair pair : parents) {
                outputLayer = pair.node.getOutput() * pair.weight + outputLayer;
            }
            if (type == 2) {
                outputValue = Math.max(outputLayer, 0);
                if (outputLayer <= 0) {
                    outputGradient = 0;
                } else {
                    outputGradient = 1;
                }
            } else {
                outputValue = outputLayer;
            }
        }
    }

    public void calculateSM(double lo) {
        outputValue = Math.exp(outputValue) / lo;
    }

    //Gets the output value
    public double getOutput() {

        if (type == 0) {    //Input node
            return inputValue;
        } else if (type == 1 || type == 3) {    //Bias node
            return 1.00;
        } else {
            return outputValue;
        }
    }

    //Calculate the delta value of a node.
    public void calculateDelta(NNImpl n, int cValue) {
        if (type == 2 || type == 4) {
            // TODO: add code here
            if (type == 2) {
                List<Node> nodes = n.output();
                delta = 0;
                for (Node node : nodes) {
                    double w = findWeight(this, node.parents);
                    delta = w * node.delta + delta;
                }
                delta = outputGradient * delta;
            } else {
                delta = cValue - getOutput();
            }
        }
    }


    //Update the weights between parents node and current node
    public void updateWeight(double learningRate) {
        if (type == 2 || type == 4) {
            // TODO: add code here
            for (NodeWeightPair each : parents) {
                each.weight = each.weight + learningRate * each.node.getOutput() * delta;
            }
        }
    }

    private double findWeight(Node node, List<NodeWeightPair> pairs) {
        for (NodeWeightPair each : pairs) {
            if (each.node == node) {
                return each.weight;
            }
        }
        System.out.println("return 0");
        return 0;
    }
}


