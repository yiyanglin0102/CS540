public class AlphaBetaPruning {
	int move;
	int visited;
	int evaluation;
	int maxDepth;
	int explored;
	int moveMax;
	int moveMin;
	double average;
	double value;


	public AlphaBetaPruning() {
		move = 0;
		value = 0.0;
		visited = 1;
		evaluation = 0;
		maxDepth = 0;
		explored = 0;
		moveMax = 0;
		moveMin = 0;
		average = 0.0;
	}

	/**
	 * This function will print out the information to the terminal,
	 * as specified in the homework description.
	 */
	public void printStats() {
		System.out.println("Move: " + move);
		System.out.println("Value: " + value);
		System.out.println("Number of Nodes Visited: " + visited);
		System.out.println("Number of Nodes Evaluated: " + evaluation);
		System.out.println("Max Depth Reached : " + maxDepth);
		System.out.println("Avg Effective Branching Factor: " + average);
	}

	/**
	 * This function will start the alpha-beta search
	 * @param state This is the current game state
	 * @param depth This is the specified search depth
	 */
	public void run(GameState state, int depth) {
		int numTaken = 0;
		explored = depth;
		boolean maxPlayer = false;
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		for(int i = 1; i < state.getSize(); i++)
		{
			if(state.getStone(i) == false)
			{
				numTaken++;
			}
		}
		if (numTaken % 2 == 0) {
			maxPlayer = true;
		}
		this.value = alphabeta(state, depth, alpha, beta, maxPlayer);

		if (maxPlayer) {
			move = moveMax;
		} else {
			move = moveMin;
		}
		average = (double) (this.visited - 1) / (double) (this.visited - this.evaluation);
		maxDepth = depth - explored;
	}

	/**
	 * This method is used to implement alpha-beta pruning for both 2 players
	 * @param state This is the current game state
	 * @param depth Current depth of search
	 * @param alpha Current Alpha value
	 * @param beta Current Beta value
	 * @param maxPlayer True if player is Max Player; Otherwise, false
	 * @return int This is the number indicating score of the best next move
	 */
	private double alphabeta(GameState state, int depth, double alpha, double beta, boolean maxPlayer) {
		double value;
		if(depth < this.explored)
		{
			explored = depth;
		}
		if (depth == 0)
		{
			this.evaluation++;
			return state.evaluate();
		}
		if(state.getSuccessors().size() == 0)
		{
			this.evaluation++;
			return state.evaluate();
		}

		if(maxPlayer)
		{
			double alphaEachNode = Double.NEGATIVE_INFINITY;
			for(int i = 0; i< state.getSuccessors().size(); i++)
			{
				this.visited++;
				value = alphabeta(state.getSuccessors().get(i), depth - 1, alpha, beta, false);
				if(alphaEachNode < value)
				{
					moveMax = state.getSuccessors().get(i).getLastMove();
				}
				alphaEachNode = Math.max(alphaEachNode, value);
				alpha = Math.max(alpha, alphaEachNode);
				if(alpha >= beta)
				{
					break;
				}
			}
			return alphaEachNode;
		}
		else
			{
			double betaEachNode = Double.POSITIVE_INFINITY;
			for(int i = 0; i< state.getSuccessors().size(); i++)
			{
				this.visited++;
				value = alphabeta(state.getSuccessors().get(i), depth - 1, alpha, beta, true);
				if(betaEachNode > value)
				{
					moveMin = state.getSuccessors().get(i).getLastMove();
				}
				betaEachNode = Math.min(betaEachNode, value);
				beta = Math.min(beta, betaEachNode);
				if(alpha >= beta)
				{
					break;
				}
			}
			return betaEachNode;
		}
	}
}





















