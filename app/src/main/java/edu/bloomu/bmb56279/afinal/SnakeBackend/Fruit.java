package edu.bloomu.bmb56279.afinal.SnakeBackend;
/**
 * A custom class that represents the fruit on the game board that the snake can eat.
 * Will increase the points as well as causing the snake to grow by one segment.
 *
 * @author Brett Bernardi
 */

import java.util.concurrent.ThreadLocalRandom;

public class Fruit implements SnakeBoardComponents {
    private final SnakeBoardComponentTypes fruitType;
    private int xCoord;
    private int yCoord;
    private static ThreadLocalRandom rand = ThreadLocalRandom.current();

    /**
     * Constructor that creates a fruit object of a random type and at a random
     * location on the game board.
     * @param numBoardRows - Number of rows in game board
     * @param numBoardCols - Number of columns in game board
     */
    public Fruit(int numBoardRows, int numBoardCols) {
        // creates a random fruit form the ComponentTypes enum. Have to simply hard
        // code and remember that the Snake Body Parts take up the first three indices
        // of the enum (if you look at it as an array) and the types of fruit take up
        // the rest.
        fruitType =
                SnakeBoardComponentTypes.values()[rand.nextInt(3,
                        SnakeBoardComponentTypes.values().length)];
        this.xCoord = rand.nextInt(0, numBoardCols);
        this.yCoord = rand.nextInt(0, numBoardRows);
    }


    protected int getxCoord() {
        return this.xCoord;
    }

    protected int getyCoord() {
        return yCoord;
    }

    /**
     * Override the abstract method to get the type of component. This will be used by
     * the drawing utility to know which image to draw.
     * @param x - the x coordinate
     * @param y - the y coordinate
     * @return - SnakeBoardComponentTypes
     */
    @Override
    public SnakeBoardComponentTypes getType(int x, int y) {
        return this.fruitType;
    }
}
