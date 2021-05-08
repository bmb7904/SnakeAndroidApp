package edu.bloomu.bmb56279.afinal.SnakeBackend;

import java.util.ArrayList;

/**
 * A custom class that extends the ArrayList class to model a Snake for the game
 * "Snake." The snake is represented as
 * an ArrayList of custom SnakeSegment objects, with one head and one tail, and the
 * appropriate number of body parts to create a snake of the specified size. This snake
 * has the ability to move in any of the 4 specified directions (the Direction enum)
 * and wrap around the game board instead of crashing into walls. It also has the
 * ability to get larger with the addition of a body segment part. This class also
 * implements the SnakeBoardComponents interface.
 *
 * @author Brett Bernardi
 */
public class Snake extends ArrayList<SnakeSegment> implements SnakeBoardComponents {
    // reference to the head of the snake for easy access
    private SnakeSegment head;
    // the number of rows in the game board. Will not change during the course of a
    // Snake object's life.
    private final int numBoardRows;
    // the number of columns in the game board. Will not change during the course of a
    // Snake object's life.
    private final int numBoardCols;
    // The index in the ArrayList of the head is always 0
    public static final int HEAD_INDEX = 0;

    /**
     * Creates a Snake object by creating the appropriate SnakeSegment and keeping them
     * in an ArrayList. Knowing the size of the board the snake will move on is crucial.
     * @param size
     */
    public Snake(int numRows, int numCols, int size) {
        super();
        this.numBoardRows = numRows;
        this.numBoardCols = numCols;

        // Create the snake by creating one head and one tail the correct number of
        // body segments, specified by the size parameter. Also sets the head and tail
        // reference fields for easy access.
        for(int i = 0; i < size; i++) {
            if(i==0) {
                this.add(i, new SnakeSegment(SnakeBoardComponentTypes.HEAD));
                head = this.get(i);
            }
            else {
                this.add(i, new SnakeSegment(SnakeBoardComponentTypes.BODY));
            }
        }
    }

    /**
     * return the x coordiante of the snakePart at the specified index on the ArrayList
     * holding the snake.
     * @param index
     * @return
     */
    public int getXCoord(int index) {
        return this.get(index).getxCoord();
    }

    /**
     * return the x coordiante of the snakePart at the specified index on the ArrayList
     * holding the snake.
     * @param index
     * @return
     */
    public int getYCoord(int index) {
        return this.get(index).getyCoord();
    }

    /**
     * Will add a middle body section to the snake. Requires the size of the gameboard
     * the snake is on, as well the current direction the snake is moving.
     * @param d
     * @param numGameRows
     * @param numGameCols
     */
    protected void addSegment(Direction d, int numGameRows, int numGameCols) {
        int headX, headY;
        headX = this.head.getxCoord();
        headY = this.head.getyCoord();

        // create new body part with the coordinates of the current head
        SnakeSegment newBodyPart = new SnakeSegment(SnakeBoardComponentTypes.BODY);
        newBodyPart.setxCoord(headX);
        newBodyPart.setyCoord(headY);

        // headIndexInArray of head in Snake ArrayList should always be 0, but
        // just in case
        int headIndexInArray = this.indexOf(head);
        // insets the new body part between the head and the rest of the snake
        this.add(headIndexInArray + 1, newBodyPart);

        switch (d) {
            case UP: {
                // the head and the new body part are currently occupying the same
                // coordinates, so change the head's coordinates appropriately

                // x coordinate doens't change when going up
                head.setxCoord(head.getxCoord());
                // if head is currently at the top row (y = 0), the new head location
                // will be the bottom the grid. Other wise it will be one row up.
                if (head.getyCoord() == 0) {
                    head.setyCoord(numGameRows - 1);
                } else {
                    head.setyCoord(headY - 1);
                }
            }
            break;
            case DOWN: {
                // the head and the new body part are currently occupying the same
                // coordinates, so change the head's coordinates appropriately

                // x coordinate doesn't change when going down
                head.setxCoord(head.getxCoord());
                // if head is currently at the bottom row (y = numGameRows - 1), the new
                // head location will be the top of grid. Otherwise it will be at the
                // one row down.
                if (head.getyCoord() == (numGameRows - 1)) {
                    head.setyCoord(0);
                } else {
                    head.setyCoord(headY + 1);
                }
            }
            break;
            case LEFT: {
                // the head and the new body part are currently occupying the same
                // coordinates, so change the head's coordinates appropriately

                // y coordinate doesn't change when going left
                head.setyCoord(head.getyCoord());
                // If head is currently at the first column (x = 0), the new head
                // location will be the other end of the game board. Otherwise the head
                // will be one columns to the left of the current head
                if (head.getxCoord() == 0) {
                    head.setxCoord(numGameCols - 1);
                } else {
                    head.setxCoord(headX - 1);
                }
            }
            break;
            case RIGHT: {
                // the head and the new body part are currently occupying the same
                // coordinates, so change the head's coordinates appropriately

                // y coordinate doesn't change when going right
                head.setyCoord(head.getyCoord());
                // If head is currently at the last column (numGameCols - 1), the new
                // head location will be the other end of the game board. Otherwise the
                // new head will be one column to the right of the current head.
                if (head.getxCoord() == (numGameCols - 1)) {
                    head.setxCoord(0);
                } else {
                    head.setxCoord(headX + 1);
                }
            }
            break;
            default: // something went wrong to get here
                break;
        }
    }

    /**
     * Moves the snake one block in the specified direction. If head of snake is at the
     * edge of the game board, wrap around.
     * @param d
     */
    protected void move(Direction d) {
        // iterate through snake (arrayList) but don't touch head yet.
        // Set each segment of the snake equal to the one in front of it (in front
        // meaning with an arrayList index less than. Head index is always 0 in
        // arrayList, the tail is always at index snake.size() - 1).
        for(int i = this.size() - 1; i > Snake.HEAD_INDEX; i--) {
            this.get(i).setxCoord(this.get(i - 1).getxCoord());
            this.get(i).setyCoord(this.get(i - 1).getyCoord());
        }

        switch(d) {
            // x coordinate will not change in a move up
            case UP: {
                // if the head is at the top-most row (index 0) and the snake is moving
                // up, then wrap around
                if(this.head.getyCoord() == 0) {
                    this.head.setyCoord(this.numBoardRows - 1);
                }
                // else move head one up by changing x coordinate
                else {
                    this.head.setyCoord(this.head.getyCoord() - 1);
                }
            }
            break;

            // x coordinate of head will not change in a move down
            case DOWN: {
                // if head is in bottom-most row, wrap around
                if(this.head.getyCoord() == this.numBoardRows - 1) {
                    this.head.setyCoord(0);
                }

                // else move one down by changing y coordinate
                else {
                    this.head.setyCoord(this.head.getyCoord() + 1);
                }
            }
            break;

            // y coordinate of head will not change in a move to the left.
            case LEFT: {
                // if the head is at the left-most column(index 0) in the game grid,
                // then wrap around.
                if(this.head.getxCoord() == 0) {
                    this.head.setxCoord(this.numBoardCols - 1);
                }
                // else, move head one left by changing X coordinate
                else {
                    this.head.setxCoord(this.head.getxCoord() - 1);
                }
            }
            break;

            // y coordinates of head will not change in a move to the right.
            case RIGHT: {
                // if the head is at the right-most column in the game grid,
                // then wrap around.
                if(this.head.getxCoord() == this.numBoardCols - 1) {
                    this.head.setxCoord(0);
                }
                // else, move head one right by changing X coordinate
                else {
                    this.head.setxCoord(this.head.getxCoord() + 1);
                }
            }
            break;

            default:
                throw new IllegalStateException("Unexpected value: " + d);
        }
    }
    /**
     * Returns a reference to the head of the snake
     */
    public SnakeSegment getHead() {
        return this.head;
    }

    /**
     * OVerrides the abstract method getType() to implement the interface.
     * @param x
     * @param y
     * @return
     */
    @Override
    public SnakeBoardComponentTypes getType(int x, int y) {
        for(int i = 0; i < this.size(); i++) {
            if(this.get(i).getxCoord() == x && this.get(i).getyCoord() == y) {
                return this.get(i).getSnakePartType();
            }
        }
        // shouldn't ever get here, as the object calling it must exist, and thus must
        // have a type.
        return null;
    }
}
