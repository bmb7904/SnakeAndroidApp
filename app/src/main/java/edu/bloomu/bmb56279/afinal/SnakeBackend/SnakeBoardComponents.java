package edu.bloomu.bmb56279.afinal.SnakeBackend;

/**
 * An interface  for the different types of objects that occupy a 2D array game
 * grid. The only method returns the type of object at the specified coordinates (x,y),
 * which can be used to draw the board.
 *
 * @author Brett Bernardi
 */
public interface SnakeBoardComponents{
    /**
     * Will return the type of object at the specified coordinates of the game grid.
     * @return
     */
     SnakeBoardComponentTypes getType(int x, int y);
}
