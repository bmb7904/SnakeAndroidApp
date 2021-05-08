package edu.bloomu.bmb56279.afinal.SnakeBackend;

/**
 * A custom class that models the segments of a Snake. Can be the head of a snake, the
 * tail, or a middle body part. Keeps track of its x,y coordinates in 2D space and has
 * methods for modifying and retrieving it's coordinates. Implements the
 * SnakeBoardComponents interface, thus it overrides the (only) abstract method getType
 * () which returns the type of component (from the list of enums).
 *
 * @author Brett Bernardi
 */
public class SnakeSegment implements SnakeBoardComponents {

    private final SnakeBoardComponentTypes type;
    // the coordinates in 2D space where (0,0) is the upper left corner of the grid.
    private int xCoord;
    private int yCoord;

    public SnakeSegment(int x, int y, SnakeBoardComponentTypes t){
        this.xCoord = x;
        this.yCoord = y;
        this.type = t;
    }

    /**
     * Second constructor. Just sets the coordinates to (0,0).
     */
    protected SnakeSegment(SnakeBoardComponentTypes t) {
        this(0,0, t);
    }

    // Getters and Setters
    protected void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }
    protected void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }

    public int getxCoord() {
        return this.xCoord;
    }
    public int getyCoord(){
        return this.yCoord;
    }

    /**
     * Returns the type of body part this object is (head, tail, or body).
     */
    protected SnakeBoardComponentTypes getSnakePartType() {
        return type;
    }

    @Override
    public SnakeBoardComponentTypes getType(int x, int y) {
        return this.type;
    }
}
