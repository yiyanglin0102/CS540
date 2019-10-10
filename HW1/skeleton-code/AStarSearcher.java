import java.util.*;

/**
 * A* algorithm search
 * <p>
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

    /**
     * Calls the parent class constructor.
     *
     * @param maze initial maze.
     * @see Searcher
     */
    public AStarSearcher(Maze maze) {
        super(maze);
    }

    /**
     * Main a-star search algorithm.
     *
     * @return true if the search finds a solution, false otherwise.
     */
    public boolean search() {
        // explored list is a Boolean array that indicates if a state associated with a given
        // position in the maze has already been explored.
        boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
        for (int i = 0; i < maze.getNoOfRows(); i++) {
            for (int j = 0; j < maze.getNoOfCols(); j++) {
                explored[i][j] = false;
            }
        }
        int goalX = maze.getGoalSquare().X;
        int goalY = maze.getGoalSquare().Y;
        PriorityQueue<StateFValuePair> frontier = new PriorityQueue<StateFValuePair>();
        double startH = Math.sqrt((maze.getPlayerSquare().X - maze.getGoalSquare().X) * (maze.getPlayerSquare().X
                - maze.getGoalSquare().X) + (maze.getPlayerSquare().Y - maze.getGoalSquare().Y) * (maze.getPlayerSquare().Y
                - maze.getGoalSquare().Y));
        StateFValuePair start = new StateFValuePair(new State(maze.getPlayerSquare(), null, 0, 0), startH);
        frontier.add(start);
        StateFValuePair poll;
        while (!frontier.isEmpty())
        {
            maxSizeOfFrontier = Math.max(frontier.size(), maxSizeOfFrontier);
            poll = frontier.poll();
            noOfNodesExpanded++;
            explored[poll.getState().getX()][poll.getState().getY()] = true;
            ArrayList<State> successors = poll.getState().getSuccessors(explored, maze);
            for (int i = 0; i < successors.size(); i++)
            {
                if (successors.get(i).isGoal(maze))
                {
                    noOfNodesExpanded++;
                    maxDepthSearched++;
                    State trace = successors.get(i); //goal
                    while (!trace.getParent().equals(start.getState()))
                    {   // update the maze if a solution found
                        maze.setOneSquare(new Square(trace.getX(), trace.getY()), '.');
                        maxDepthSearched++;
                        trace = trace.getParent();
                    }
                    maze.setOneSquare(new Square(trace.getX(), trace.getY()), '.');
                    maze.setOneSquare(new Square(maze.getGoalSquare().X, maze.getGoalSquare().Y), 'G');
                    cost = maxDepthSearched;
                    // return true if a solution has been found
                    return true;
                }
                double euclideanDistance = Math.sqrt((successors.get(i).getX() - goalX) * (successors.get(i).getX()
                        - goalX) + (successors.get(i).getY() - goalY) * (successors.get(i).getY() - goalY));
                double f = successors.get(i).getParent().getGValue()+1 + euclideanDistance;
                StateFValuePair sp = new StateFValuePair(successors.get(i), f);
                boolean check = true;
                for(StateFValuePair eachStatePair : frontier)
                {
                    if (sp.getState().getX() == eachStatePair.getState().getX()
                            && sp.getState().getY() == eachStatePair.getState().getY())
                    {
                        if(eachStatePair.compareTo(sp) == 1 )
                        {
                            frontier.remove(eachStatePair);
                            frontier.add(sp);
                            break;
                        }
                        check = false;
                    }
                }
                if (check == true){
                    frontier.add(sp);
                }
            }
        }
        return false;
    }
}