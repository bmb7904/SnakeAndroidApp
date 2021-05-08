package edu.bloomu.bmb56279.afinal.SnakeBackend;

/**
 * A custom class that brings all of these disparate classes together like the Snake
 * class, fruit class, and the game board. The Snake class is essentially an arrayList
 * that holds SnakeSegment objects. This class will have methods that initialize the
 * GameBoard, add the Snake to the board, check the board for a game over, keep score
 * by keeping track of the when the Snake object ate fruit and thus added a segment.
 * Serves as the main interface between the UI component of the app and the backend
 * game logic component.
 *
 * @author Brett Bernardi
 */
public class SnakeGame {
    private static final int INITSIZESNAKE = 3;
    private Snake snake;
    private GameBoard board;
    // The fruit object in the game
    private Fruit fruit;
    // The player's score
    private int score;
    // The coordinates in the board array of the fruit.
    // Only one fruit allowed at a time. Better to store the coordinates, than search
    // the array.
    private int fruitX;
    private int fruitY;

    /**
     * Creates the Snake object and the GameBoard object. Adds the snake to the
     * gameboard, and adds one fruit object as the initial fruit.
     */
    public SnakeGame(int numRows, int numCols) {
        this.board = new GameBoard(numRows, numCols);
        this.snake = new Snake(numRows, numCols, INITSIZESNAKE);
        initSnakeAtStart();
        updateGameBoard();
        createFruit();
        // init the Score
        this.score = 0;
    }

    /**
     * When a game is first started, this sets the initial orientation and direction
     * of the Snake for a new game.
     */
    private void initSnakeAtStart() {
        // puts snake in the middle of the screen initially
        int startHeadX = this.getNumCols() / 2 + 1;
        int startHeadY = this.getNumRows() / 2;

        for (int i = 0; i < snake.size(); i++) {
            if (i == 0) {
                snake.get(i).setxCoord(startHeadX);
                snake.get(i).setyCoord(startHeadY);
            }
            // same Y, each segment is one less than the segment to the right of it.
            else {
                snake.get(i).setxCoord(snake.get(i - 1).getxCoord() - 1);
                snake.get(i).setyCoord(startHeadY);
            }
        }
    }

    public int getSizeOfSnake() {
        return this.snake.size();
    }

    /**
     * Returns a reference to the snake object if needed by UI.
     */
    public Snake getSnakeRef() {
        return this.snake;
    }

    /**
     * Update the GameBoard with the coordinates of the snake object.
     * Pre-conditions: The snake object must be initialized before ever calling this
     * method. Meaning that each component of the snake must not have null for it's x
     * or y coordinates. The same is true of the fruit object.
     */
    public void updateGameBoard() {
        // first make all elements in game board null BEFORE updating.
        this.board.makeArrayAllNull();
        // y
        for (int i = 0; i < board.getNumRows(); i++) {
            // x
            for (int j = 0; j < board.getNumCols(); j++) {
                for (int k = 0; k < snake.size(); k++) {

                    if (snake.get(k).getxCoord() == j && snake.get(k).getyCoord() == i) {
                        // add the snake body part to the game grid (it extends the
                        // interface)
                        board.addToGameBoard(snake.get(k), j, i);
                    }
                }
            }
        }
        // if fruit happens to be null.
        if(fruit != null) {
            board.addToGameBoard(fruit, fruit.getxCoord(), fruit.getyCoord());
        }

    }
    /**
     * Fruit is eaten by snake, so a segment is added (a BODY segment)
     */
    public void fruitEaten(Direction d){
        snake.addSegment(d, board.getNumRows(), board.getNumCols());
        createFruit();
    }

    /**
     * Creates a fruit object. In an infinite loop, checks if the fruit object that was
     * created is valid (meaning it's not in the same cell on the board as any snake
     * component. Keeps looping until a valid location is found. Once found, the fruit
     * is added to the board.
     */
    public void createFruit() {
        // first discard current fruit if it exists
        fruit = null;

        // Will loop until a valid location for fruit is found
        while (true) {
            boolean flag = false;
            fruit = new Fruit(board.getNumRows(), board.getNumCols());
            for (SnakeSegment sp : snake) {
                if (sp.getxCoord() == fruit.getxCoord() && sp.getyCoord() == fruit.getyCoord()) {
                    flag = true;
                }
            }
            if (flag == false) {
                // valid fruit and fruit position created
                break;
            }
        }

        System.out.println("Fruit coordinates:    " + fruit.getxCoord() + "," + fruit.getyCoord());
    }

    /**
     * Move the snake in the specified direction. Also checks for collision with any
     * fruit. If collision is detected, another fruit is created, and the score is
     * incremented. If fruit is eaten during this move, return true. Otherwise, return
     * false.
     * @param d - The direction to move the snake.
     */
    public boolean moveSnake(Direction d) {
        // now call the instance method in the Snake class to move the Snake object in
        // the specified direction.
        this.snake.move(d);

        if(this.snake.getHead().getxCoord() == this.fruit.getxCoord() &&
                this.snake.getHead().getyCoord() == this.fruit.getyCoord()) {
            // if true, fruit was eaten by collision with snakeHead
            this.fruitEaten(d);
            score += 10;
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Checks the Snake for Collision. The only way to lose the game is if the game
     * intersects with itself.
     */
    public boolean collisionDetected() {
        for(int i = 0; i < this.snake.size(); i++) {
            // obviously, don't check if head collides with head
            // equating references
            if(this.snake.get(i) != this.snake.getHead()) {
                // only way game can be lose is if head collides with any other body
                // part of snake
                if((this.snake.getHead().getxCoord() == this.snake.get(i).getxCoord()) &&
                        (this.snake.getHead().getyCoord() == this.snake.get(i).getyCoord())) {
                    return true;
                }
            }
        }
        return false;
    }
    public int getScore() {
        return this.score;
    }


    public int getNumRows() {
        return this.board.getNumRows();
    }

    public int getNumCols() {
        return this.board.getNumCols();
    }

    /**
     * Essentially a wrapper function for the board.getComponent method to get the
     * component at the specified indicies.
     */
    public SnakeBoardComponents getBoardElement(int x, int y) {
        return this.board.getComponent(x,y);
    }

    @Override
    public String toString() {
        return board.toString();
    }

}
