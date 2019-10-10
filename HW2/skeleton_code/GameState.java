import java.util.*;
import java.util.stream.*;

public class GameState {
    private int size;            // The number of stones
    private boolean[] stones;    // Game state: true for available stones, false for taken ones
    private int lastMove;        // The last move

    /**
     * Class constructor specifying the number of stones.
     */
    public GameState(int size) {

        this.size = size;

        //  For convenience, we use 1-based index, and set 0 to be unavailable
        this.stones = new boolean[this.size + 1];
        this.stones[0] = false;

        // Set default state of stones to available
        for (int i = 1; i <= this.size; ++i) {
            this.stones[i] = true;
        }

        // Set the last move be -1
        this.lastMove = -1;
    }

    /**
     * Copy constructor
     */
    public GameState(GameState other) {
        this.size = other.size;
        this.stones = Arrays.copyOf(other.stones, other.stones.length);
        this.lastMove = other.lastMove;
    }


    /**
     * This method is used to compute a list of legal moves
     *
     * @return This is the list of state's moves
     */
    public List<Integer> getMoves() {
        List<Integer> move = new ArrayList<Integer>();
        if (this.lastMove == -1) {
            if ((this.size % 2) == 1) {
                for (int i = 1; i <= this.size / 2; i += 2) {
                    move.add(i);
                }
            } else {
                for (int i = 1; i < this.size / 2; i += 2) {
                    move.add(i);
                }
            }
        } else {
            for (int i = 1; i <= this.lastMove; ++i) {
                if ((this.lastMove % i == 0) && (stones[i])) {
                    move.add(i);
                }
            }
            int multiple = 2;
            while ((multiple * this.lastMove) <= this.size) {
                if (stones[multiple * this.lastMove]) {
                    move.add(multiple * this.lastMove);
                }
                multiple++;
            }
        }
        return move;
    }


    /**
     * This method is used to generate a list of successors
     * using the getMoves() method
     *
     * @return This is the list of state's successors
     */
    public List<GameState> getSuccessors() {
        return this.getMoves().stream().map(move -> {
            GameState state = new GameState(this);
            state.removeStone(move);
            return state;
        }).collect(Collectors.toList());
    }


    /**
     * This method is used to evaluate a game state based on
     * the given heuristic function
     *
     * @return int This is the static score of given state
     */
    public double evaluate() {
        int score = 0;
        for (int i = 1; i <= this.size; ++i) {
            if (!this.stones[i]) {
				score++;
            }
        }
        if (score % 2 == 0 && this.getSuccessors().size() == 0) {
            return -1.0;
        } else if (score % 2 == 1 && this.getSuccessors().size() == 0) {
            return 1.0;
        } else {
            if (score % 2 == 0) {
                if (stones[1]) {
                    return 0.0;
                }
                if (this.lastMove == 1) {
                    if (this.getSuccessors().size() % 2 == 0) {
                        return -0.5;
                    } else {
                        return 0.5;
                    }
                }
                if (Helper.isPrime(this.lastMove)) {
                    int num = 0;
                    for (int i = 0; i < this.getSuccessors().size(); i++) {
                        if (this.getSuccessors().get(i).lastMove % lastMove == 0) {
							num++;
                        }
                    }
                    if (num % 2 == 1) {
                        return 0.7;
                    } else {
                        return -0.7;
                    }
                }
                if (!Helper.isPrime(this.lastMove)) {
                    int largest = Helper.getLargestPrimeFactor(this.lastMove);
                    int numLPF = 0;
                    for (int i = 0; i < this.getSuccessors().size(); i++) {
                        if (this.getSuccessors().get(i).lastMove % largest == 0) {
							numLPF++;
                        }
                    }
                    if (numLPF % 2 == 1) {
                        return 0.6;
                    } else {
                        return -0.6;
                    }
                }
            } else {
                if (stones[1]) {
                    return 0.0;
                }
                if (this.lastMove == 1) {
                    if (this.getSuccessors().size() % 2 == 0) {
                        return 0.5;
                    } else {
                        return -0.5;
                    }
                }
                if (Helper.isPrime(this.lastMove)) {
                    int numberPrime = 0;
                    for (int i = 0; i < this.getSuccessors().size(); i++) {
                        if (this.getSuccessors().get(i).lastMove % lastMove == 0) {
							numberPrime++;
                        }
                    }
                    if (numberPrime % 2 == 1) {
                        return -0.7;
                    } else {
                        return 0.7;
                    }
                }
                if (!Helper.isPrime(this.lastMove)) {
                    int largest = Helper.getLargestPrimeFactor(this.lastMove);
                    int numberOdd = 0;
                    for (int i = 0; i < this.getSuccessors().size(); i++) {
                        if (this.getSuccessors().get(i).lastMove % largest == 0) {
							numberOdd++;
                        }
                    }
                    if (numberOdd % 2 == 1) {
                        return -0.6;
                    } else {
                        return 0.6;
                    }
                }
            }
        }
        return 0.0;
    }

    /**
     * This method is used to take a stone out
     *
     * @param idx Index of the taken stone
     */
    public void removeStone(int idx) {
        this.stones[idx] = false;
        this.lastMove = idx;
    }

    /**
     * These are get/set methods for a stone
     *
     * @param idx Index of the taken stone
     */
    public void setStone(int idx) {
        this.stones[idx] = true;
    }

    public boolean getStone(int idx) {
        return this.stones[idx];
    }

    /**
     * These are get/set methods for lastMove variable
     *
     * @param move Index of the taken stone
     */
    public void setLastMove(int move) {
        this.lastMove = move;
    }

    public int getLastMove() {
        return this.lastMove;
    }

    /**
     * This is get method for game size
     *
     * @return int the number of stones
     */
    public int getSize() {
        return this.size;
    }

}	
