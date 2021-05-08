package edu.bloomu.bmb56279.afinal.SnakeBackend;

/**
 * A custom class that represents the game board. We can think of the array indices as
 * the coordinates of a coordinate system, where (0,0) is the upper left hand corner of
 * the grid.
 */
public class GameBoard {
    // 2D array representing the game grid
    // 2D array of objects that implement the SnakeBoardComponents interface
    private SnakeBoardComponents gameGrid[][];
    // the number of columns in the game grid
    private final int numCols;
    // the number of the rows in the game grid
    private final int numRows;

    public GameBoard(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.gameGrid = new SnakeBoardComponents[this.numCols][this.numRows];
        makeArrayAllNull();
    }

    /**
     * Returns the number of Columns in the game board.
     * @return
     */
    protected int getNumCols() {
        return numCols;
    }

    /**
     * Returns the number of columns in the game board.
     * @return
     */
    protected int getNumRows() {
        return numRows;
    }

    /**
     * All elements in the 2D array (game board) are made null. Not static or private,
     * as other objects may use this (like the SnakeGame class).
     */
    protected void makeArrayAllNull() {
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++) {
                gameGrid[j][i] = null;
            }
        }
    }

    protected boolean addToGameBoard(SnakeBoardComponents component, int x, int y) {
        if(x < 0 || x >= this.numCols || y < 0 || y >= this.numRows){
            return false;
        }
        this.gameGrid[x][y] = component;
        return true;
    }

    /**
     * Pre-conditions: Indices must be valid. No error checking.
     *
     * Returns the object at the specified index in the gameGrid array.
     * @param x
     * @param y
     * @return
     */
    protected SnakeBoardComponents getComponent(int x, int y) {
        if(this.gameGrid[x][y] == null) {
            return null;
        }
        else {
            return this.gameGrid[x][y];
        }
    }

    /**
     * Used for debugging.
     * @return
     */
    @Override
    public String toString() {
        String str = "";
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++) {
                if(gameGrid[j][i] == null) {
                    str += "-";
                }
                else if(gameGrid[j][i].getType(j, i) == SnakeBoardComponentTypes.HEAD) {
                    str += "H";
                }
                else if(gameGrid[j][i].getType(j, i) == SnakeBoardComponentTypes.TAIL) {
                    str += "T";
                }
                else if(gameGrid[j][i].getType(j, i) == SnakeBoardComponentTypes.BODY) {
                    str += "*";
                }
                else {
                    str += "F";
                }
            }
            str += "\n";
        }
        return str;
    }
}
