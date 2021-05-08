package edu.bloomu.bmb56279.afinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import edu.bloomu.bmb56279.afinal.SnakeBackend.Direction;
import edu.bloomu.bmb56279.afinal.SnakeBackend.SnakeBoardComponentTypes;

/**
 * A custom view that extends ImageView. This is a way to represent and draw the body
 * parts of the snake.
 */
public class SnakeImage extends androidx.appcompat.widget.AppCompatImageView {
    // The bitmap to be drawn associated with this object
    private Bitmap bitmap;
    // A drawable object
    Drawable d;
    private int row;
    private int col;
    private int widthGameArea;
    private int heightGameArea;

    private int blocksize;
    private Direction direction;
    private Context context;
    private SnakeBoardComponentTypes type;
    // coordinates on the screen
    private float xPos;
    private float YPos;

    /**
     * Constructor to create the custom view.
     */
    public SnakeImage(@NonNull Context context, SnakeBoardComponentTypes type,
                      int blockSize, int r, int c, int w, int h, Direction dir) {
        super(context);
        this.context = context;
        this.blocksize = blockSize;
        this.row = r;
        this.col = c;
        this.widthGameArea = w;
        this.heightGameArea = h;
        this.direction = dir;
        this.type = type;
        this.xPos = ((this.col * blocksize) + ((float)this.blocksize / 2));
        this.YPos = ((this.row * blocksize) + ((float)this.blocksize / 2));
        if(this.type == SnakeBoardComponentTypes.HEAD) {
            switch(this.direction) {
                case UP: {
                    this.d = ContextCompat.getDrawable(context, R.drawable.snake_up);
                }
                    break;
                case DOWN: {
                    this.d = ContextCompat.getDrawable(context, R.drawable.snake_down);
                }
                    break;
                case LEFT: {
                    this.d = ContextCompat.getDrawable(context, R.drawable.snake_left);
                }
                    break;
                case RIGHT: {
                    this.d = ContextCompat.getDrawable(context, R.drawable.snake_right);
                }
                    break;

            }

        }
        else if(type == SnakeBoardComponentTypes.BODY) {
            this.d = ContextCompat.getDrawable(context, R.drawable.segment);
        }
    }

    /**
     * Updates coordinates (r,c) in the game array as well as direction after a move.

     */
    public void updateCoord(int r, int c, Direction d) {
        // if direction changed, and this is the head, change orientation of snake head
        // by changing to the correct image.
        if (this.direction != d && this.type == SnakeBoardComponentTypes.HEAD) {
            this.direction = d;
            switch (this.direction) {
                case UP: {
                    this.d = ContextCompat.getDrawable(context, R.drawable.snake_up);
                }
                break;
                case DOWN: {
                    this.d = ContextCompat.getDrawable(context, R.drawable.snake_down);
                }
                break;
                case LEFT: {
                    this.d = ContextCompat.getDrawable(context, R.drawable.snake_left);
                }
                break;
                case RIGHT: {
                    this.d = ContextCompat.getDrawable(context, R.drawable.snake_right);
                }
                break;
            }
        }
        // coordinates in the game array SnakeBoard
        this.row = r;
        this.col = c;
        // update coordinate position of view
        this.xPos = ((this.col * blocksize) + ((float)this.blocksize / 2));
        this.YPos = ((this.row * blocksize) + ((float)this.blocksize / 2));

    }

    /**
     * Overwritten draw method for this custom view. Because we are calling this method
     * directly, we override draw() instead of onDraw().
     * @param canvas
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // First set the bounding box where to draw the bitmap. These pixels values are
        // relative to the container. In this case, relative to the SnakeView
        // (SurfaceView).
        this.d.setBounds(this.col * blocksize, this.row * blocksize,
                this.col * blocksize + blocksize, this.row * blocksize + blocksize);
        // Draw the drawable object using the canvas.
        this.d.draw(canvas);

    }

    /**
     * Override to get X position of exactly center of image.
     * @return
     */
    @Override
    public float getX() {
        super.getX();
        return this.xPos;
    }

    /**
     * Override to get Y pos of exactly center of image
     * @return
     */
    @Override
    public float getY() {
        super.getX();
        return this.YPos;
    }
}
