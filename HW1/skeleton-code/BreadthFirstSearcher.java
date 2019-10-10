import java.util.*;

/**
 * Breadth-First Search (BFS)
 * <p>
 * You should fill the search() method of this class.
 */
public class BreadthFirstSearcher extends Searcher {

    /**
     * Calls the parent class constructor.
     *
     * @param maze initial maze.
     * @see Searcher
     */
    public BreadthFirstSearcher(Maze maze) {
        super(maze);
    }

    /**
     * Main breadth first search algorithm.
     *
     * @return true if the search finds a solution, false otherwise.
     */
    public boolean search() {
        // explored list is a 2D Boolean array that indicates if a state associated with a
        // given position in the maze has already been explored.
        boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
        for (int i = 0; i < maze.getNoOfRows(); i++)
        {
            for (int j = 0; j < maze.getNoOfCols(); j++)
            {
                explored[i][j] = false;
            }
        }
        State start = new State(maze.getPlayerSquare(), null, 0, 0);
        // Queue implementing the Frontier list
        Queue<State> queue = new LinkedList<State>();
        queue.offer(start);
        noOfNodesExpanded++;
        State poll;
        while (!queue.isEmpty())
        {
            poll = queue.poll();
            explored[poll.getX()][poll.getY()] = true;
            maxSizeOfFrontier = Math.max(queue.size(), maxSizeOfFrontier);
            List<State> successors = poll.getSuccessors(explored, maze);
            for (int i = 0; i < successors.size(); i++)
            {
                if (successors.get(i).isGoal(maze))
                {
                    noOfNodesExpanded++;
                    maxDepthSearched++;
                    State trace = successors.get(i); //goal
                    while (!trace.getParent().equals(start))
                    {  // update the maze if a solution found
                        maze.setOneSquare(new Square(trace.getX(), trace.getY()), '.');
                        maxDepthSearched++;
                        trace = trace.getParent();
                    }
                    maze.setOneSquare(new Square(trace.getX(), trace.getY()), '.');
                    maze.setOneSquare(new Square(maze.getGoalSquare().X, maze.getGoalSquare().Y), 'G');
                    cost = maxDepthSearched;
                    if (start.getSquare().X == maze.getGoalSquare().X)
                    {
                        maxSizeOfFrontier++;
                    }
                    // return true if find a solution
                    return true;
                }
                if (!check_path(successors.get(i), queue))
                {
                    noOfNodesExpanded++;
                }
                queue.offer(successors.get(i));
            }
        }
        // return false if no solution
        return false;

    }

    private boolean check_path(State current, Queue<State> queue)
    {
        for (State each : queue)
        {
            if (current.getX() == each.getX() && current.getY() == each.getY())
            {
                return true;
            }
        }
        return false;
    }
}
