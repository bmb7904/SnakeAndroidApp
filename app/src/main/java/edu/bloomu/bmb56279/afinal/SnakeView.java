package edu.bloomu.bmb56279.afinal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import edu.bloomu.bmb56279.afinal.SnakeBackend.Direction;
import edu.bloomu.bmb56279.afinal.SnakeBackend.Snake;
import edu.bloomu.bmb56279.afinal.SnakeBackend.SnakeBoardComponentTypes;
import edu.bloomu.bmb56279.afinal.SnakeBackend.SnakeGame;

/**
 * A custom view that extends Surface view and implements the SurfaceHolder callback,
 * which allows a separate thread from the main UIThread to manually draw on the
 * SurfaceView area with a canvas object.
 * A custom View that extends the SurfaceView class. SurfaceView is a view that has
 * the ability to be modified by a different Thread other than the main UI thread. We will
 * utilize this fact, along with a background thread, separate from the main UI thread,
 * to update the state of the game as well as draw the state of the game onto the
 * screen. Because of the nature of the game Snake, with the Snake always moving even
 * without user input, there will need to be some type of game loop that keeps the
 * snake moving as well as checking for user
 * input.This loop will update the state of the game by calling the appropriate methods in
 * the SnakeGame class like moveSnake(), as well as drawing the state of the game to
 * the screen by drawing Images to the surface of the SurfaceView object.
 *
 * One option is to simply have a while loop that executes under a boolean condition,
 * with a
 * Thread.sleep(milliseconds) call for every iteration of the loop. You would then
 * adjust the argument of Thread.sleep() through experimentation for what looks the
 * best. This works,but Thread.sleep() can be unreliable and imprecise. It's also
 * true that the game would run at different speeds depending upon the device, as
 * the the method calls to update the game and draw the game take different amounts
 * of time, depending on the device. The time these method calls take can also vary
 * on the same device, for instance, as the size of the snake grows, the method call to
 * move the snake will take a longer amount of time.There is a better, more precise,
 * and more consistent way to implement a game loop in Android.
 *
 * There are two things to consider
 * when making a game loop: the FrameRate and the UpdateRate. The frame rate is
 * how many times the screen is updated per second, while the UpdateRate is the how
 * many times the state of the game is updated per second. Ideally, we would like
 * these to be equal. Because of the simple nature of this game, a FrameRate of more than
 * 10fps wouldn't make much sense, and through experimentation, a frame rate of 3
 * fps proved to provide a smooth appearance of animation while the snake was moving. In
 * modern, fast-paced games, with lots of components to be drawn, frame rates of at
 * least 30 fps are considered standard.
 *
 * To keep our times consistent, we will aim to update the game state and update the
 * board in regular intervals. There are 1000 milliseconds in 1 second, so if we are
 * aiming for a 3 fps FrameRate, we want the game to update the game state and draw the
 * state of the board every 333 milliseconds. We can accomplish this by having a method
 * that keeps track of the times and tells us if it is too early to update and draw the
 * game.
 */

public class SnakeView extends SurfaceView implements SurfaceHolder.Callback {
    // The width and height of the SurfaceView area. I.E. The area where the game is
    // being played. These are final for every instance of this class.
    private final int width;
    private final int height;

    // A point that holds the real pixels dimensions of the entire screen. This will be
    // used to set up game accordingly.
    private final Point point;

    // A canvas for our paint
    private Canvas canvas;

    // Required to use canvas
    private SurfaceHolder surfaceHolder;

    // Some paint for our canvas
    private Paint paint;
    // The instance of our SnakeGame
    private SnakeGame game;

    private static final int newSnakeBodyPartIndex = 1;

    //here are two things we should consider here: FPS and UPS.

    // FPS – Frames per Second – the number of times displayGameState() is being
    //      called per second.

    // UPS – Update per Second – the number of times updateGameState() is being called
    // per second.
    private final int frameRate = 3;

    // number of milliseconds in one second.
    private static final long millisInOneSecond = 1000;

    private long frameTime;

    private Context context;

    // size of each component in the game in pixels
    private final int blockSize = 80;

    // The number of rows and columns will be constant for any particular instance of
    // this SnakeView class (even if a new game is created). Thus, make final
    private final int numRows;
    private final int numCols;

    // The current direction the of the Snake
    private Direction direction;

    // A list that holds the custom imageViews
    // This should hold every segment of the snake
    ArrayList<SnakeImage> list = new ArrayList<>();

    private final int headIndex = 0;
    // The workerThread to update game and draw the game.
    BGThread thread;

    // boolean to determine is Thread should be paused or not
    private boolean isThreadPause = false;

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    // true if UIThread is currently calling MotionEvent method. Will be used to lock
    // BG Thread until method is finished.
    private boolean accessing;
    // Set to true when collision is detected and BG Thread will react appropriately
    public boolean isGameOver;
    // will be set true by UI Thread, after the completion of a game and score is
    // checked to be added to database or not.
    private boolean startNewGame;

    int counter;

    SoundPool soundPool;
    // Various sounds
    int gameOverSound;
    int highScoreSound;
    int newGameSound;
    int fruitEatenSound;

    /**
     * Constructor for the custom View SurfaceView.
     */
    public SnakeView(Point p, Context context) {
        super(context);
        this.point = p;
        this.context = context;
        this.width = p.x;
        // width of game area is height of screen - height of upper window area
        this.height = p.y - ((int)(p.y * MainActivity.percentageTopWindow));
        this.paint = new Paint();
        this.surfaceHolder = getHolder();
        getHolder().addCallback(this);
        // Calculate num of rows and cols depending on screen size.
        this.numCols = (int)((float)this.width / blockSize);
        this.numRows = (int)((float)this.height / blockSize);

        // initialize the frameTime current System Time
        this.frameTime = System.currentTimeMillis();

        this.thread = new BGThread(this, getHolder(), this.context);

        this.accessing = false;

        this.isGameOver = false;

        setUpSounds();
        // only called on the first game of a session.
        newGame();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        // set the running boolean to true
        thread.setRunning(true);

        // start the thread
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry = true;
        while(retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                // not handled
            }
            retry = false;
        }

    }

    /**
     * Synchronized draw method. The surface view is not always available for
     * modification, and it must be checked before doing any drawing with the canvas.
     */
    public void draw() throws InterruptedException {
        lock.lock();

        if(this.accessing) {
            condition.await();
        }

        // Get the lock for the surface
        // To draw to the Surface, the surface must be valid to draw on, which is not
        // all the time. If the surface is valid, obtain the lock.
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            //https://stackoverflow.com/questions/6078967/clearing-surfaceview-in-android/9035709
            // A way to clear the canvas of anything previously drawn
            canvas.drawColor( 0, PorterDuff.Mode.CLEAR );

            // go through arrayList of custom image Views and draw each one
            for(int i = 0; i < this.list.size(); i++) {

                list.get(i).draw(canvas);
            }

            // the following nested loops are for drawing the fruit by finding them in
            // the gameboard and checking their type.
            for(int i = 0; i < this.game.getNumRows(); i++) {
                for(int j = 0; j < this.game.getNumCols(); j++) {
                    // Ugly if statement, but necessary. Check for fruit. If null or
                    // part of the snake, ignore.
                    if(this.game.getBoardElement(j, i) != null &&
                            this.game.getBoardElement(j, i).getType(j, i)
                                    != SnakeBoardComponentTypes.HEAD &&
                    this.game.getBoardElement(j, i).getType(j, i) !=
                    SnakeBoardComponentTypes.BODY && this.game.getBoardElement(j,i).getType(j,i)
                    != SnakeBoardComponentTypes.TAIL)  {
                        Drawable d = null;

                        switch(this.game.getBoardElement(j,i).getType(j,i)) {
                            case APPLE: {
                                d = ContextCompat.getDrawable(context,
                                        R.drawable.apple);
                            }
                                break;
                            case GRAPES: {
                                d = ContextCompat.getDrawable(context,
                                        R.drawable.grapes);
                            }
                                break;
                            case STRAWBERRY: {
                                d = ContextCompat.getDrawable(context,
                                        R.drawable.strawberry);
                            }
                                break;
                            case CHERRY: {
                                d = ContextCompat.getDrawable(context,
                                        R.drawable.cherry);
                            }
                                break;

                        }
                        // Use the canvas to draw the drawable fruit object
                        if(d != null) {
                            // set the bounds of the drawable bitmap in a rectangle
                            d.setBounds(j * blockSize, i * blockSize,
                                    j * blockSize + blockSize, i * blockSize + blockSize);
                            d.draw(canvas);
                        }
                    }
                }
            }

            // Unlock the canvas and reveal the graphics for this frame
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
        lock.unlock();
    }

    /**
     * Updates the state of the game by calling the appropriate methods on the backend
     * SnakeGame class. Also updates the ImageView ArrayList, which is always analogous
     * to the ArrayList in the Snake class.
     */
    public synchronized void updateGameState() {

        // if new body part was added (fruit was eaten) the new body part is at index 1
        // in the Snake Array and thus we should add an ImageView at the same index into
        // the our listOfSnakeImages. Also, play a sound for fruit eaten.
        if(this.game.moveSnake(direction)) {
            soundPool.play(fruitEatenSound, 1, 1, 1, 0, 1);
            // new body segment added in Snake, thus we must add it to the ArrayList of
            // imageViews. This will insert it.
            SnakeImage newBodyPart = new SnakeImage(context,
                    SnakeBoardComponentTypes.BODY, blockSize,
                    this.game.getSnakeRef().get(newSnakeBodyPartIndex).getyCoord(),
                    this.game.getSnakeRef().get(newSnakeBodyPartIndex).getxCoord(),
                    this.width, this.height, direction);
            list.add(1, newBodyPart);


        }
        // update the state of the game on the backend
        this.game.updateGameBoard();

        // update the list of ImageViews
        // Update coordinates in each Snake Image View from the Snake in the game.
        for(int i = 0; i < this.list.size(); i++) {
            list.get(i).updateCoord(this.game.getSnakeRef().get(i).getyCoord(),
                    this.game.getSnakeRef().get(i).getxCoord(), direction);
        }

        // check for collision
        if(this.game.collisionDetected()) {
            // pause BG Thread and set isGameOver flag to true
            this.isThreadPause = true;
            this.isGameOver = true;
            // play game over sound
            soundPool.play(gameOverSound, 1, 1, 1, 0, 1);
        }

    }

    /**
     * Checks to see if an update is required at this time.
     */
    public boolean updateNeeded() {
        // If the frameTime is <= Current time in millis, we are due to update the game
        // state and draw the game.
        if(this.frameTime <= System.currentTimeMillis()) {
            // calculate next frameTime
            this.frameTime = System.currentTimeMillis() + (millisInOneSecond/frameRate);
            return true;
        }
        return false;
    }

    /**
     * Setup new game by creating new SnakeGame object and new ArrayList of custom
     * SnakeImage views.
     */
    public void newGame() {
        this.counter = 0;
        // Thank God for garbage collecting
        this.game = null;
        this.game = new SnakeGame(numRows, numCols);
        this.direction = Direction.RIGHT;

        int headIndex = Snake.HEAD_INDEX;
        if(list != null) {
            list = null;
            this.list = new ArrayList<>();
        }

        for(int i = 0; i < this.game.getSizeOfSnake(); i++) {
            // create Head custom ImageView
            if(i == headIndex) {
                SnakeImage headImage = new SnakeImage(context,
                        SnakeBoardComponentTypes.HEAD, blockSize,
                        this.game.getSnakeRef().getHead().getyCoord(),
                        this.game.getSnakeRef().getHead().getxCoord(), this.width,
                        this.height, this.direction);
                list.add(headImage);
            }

            else {
                SnakeImage bodyImage = new SnakeImage(context,
                        SnakeBoardComponentTypes.BODY, blockSize,
                        this.game.getSnakeRef().get(i).getyCoord(),
                        this.game.getSnakeRef().get(i).getxCoord(), this.width,
                        this.height, this.direction);
                list.add(bodyImage);
            }
        }
        soundPool.play(newGameSound, 1, 1, 1, 0, 1);
    }

    /**
     * Gets user input from where the user touched on Screen. Will control direction of
     * Snake. Only UI Thread calls this method. If this method is being run, block the
     * BG Thread. I found this to make for a smoother and more responsive game. I'm
     * pretty sure I synchronzed this correctly. I hope!
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // will block BG thread from accessing draw method in this class while UI
        // thread is executing this method call. This made the game smoother in that a
        // new frame cannot be drawn until user input is stored.
        this.accessing = true;
        lock.lock();

        if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            float headX = list.get(0).getX();
            float headY = list.get(0).getY();

            // Give some wiggle room of half the block size
            switch (direction) {
                // if snake if moving up or down, we only care about the X coordinate
                // of the touch event. Also, if going up or down, we can't change to
                // either down or up. Only two options: left or right.
                case UP:
                case DOWN: {
                    if (x > headX + (blockSize/2)) {
                        direction = Direction.RIGHT;
                    } else if (x < headX - (blockSize/2)) {
                        direction = Direction.LEFT;
                    } else {
                        // do nothing
                    }
                }
                break;
                // if snake if moving down, we only care about the X coordinates of
                // the touch event. Also, if going up, we can't change to either
                // down or up. Only two options: left or right.
                case LEFT:
                case RIGHT: {
                    if (y < headY - (blockSize/2)) {
                        direction = Direction.UP;
                    } else if (y > headY + (blockSize/2)) {
                        direction = Direction.DOWN;
                    } else {
                        // do nothing
                    }
                }
                break;
            }
        }
            this.accessing = false;
            // wake up sleeping BG Thread
            condition.signalAll();
            lock.unlock();
            return true;
    }

    /**
     * Wrapper to getScore from SnakeGame class
     */
    public int getScore() {
        return game.getScore();
    }

    /**
     * Toggles the boolean field isThreadPaused. Will be called by UIThread on
     * MainActivity when pauseButton is pressed.
     */
    public void togglePaused() {
        if(this.isThreadPause) {
            this.isThreadPause = false;
        }
        else {
            this.isThreadPause = true;
        }
    }

    /**
     * Gets the value of the isThreadPaused variable.
     * @return boolean if BGThread Paused
     */
    public boolean getIsThreadPaused() {
        return this.isThreadPause;
    }

    /**
     * @return boolean - return true if game is over. False Otherwise.
     */
    public boolean isGameOver() {
        return this.isGameOver;
    }

    /**
     * Set isGameOver flag with parameter
     */
    public void setGameOver(boolean b) {
        this.isGameOver = b;
    }

    /**
     *
     * @return boolean - returns true if BG Thread should start new game
     */
    public boolean getStartNewGame() {
        return this.startNewGame;
    }

    /**
     * Set's startNewGame flag
     */
    public void setStartNewGame(boolean b) {
        this.startNewGame = b;
    }

    /**
     * Sets up soundPool and sounds.
     */
    public void setUpSounds() {
        AudioAttributes audioAttributes =
                new AudioAttributes.Builder().setUsage(AudioAttributes.
                        USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.
                        CONTENT_TYPE_SONIFICATION).build();
        soundPool =
                new SoundPool.Builder().setMaxStreams(3).
                        setAudioAttributes(audioAttributes).build();
        gameOverSound = soundPool.load(context, R.raw.gameover, 1);
        highScoreSound = soundPool.load(context, R.raw.high_score_sound, 1);
        newGameSound = soundPool.load(context, R.raw.new_game_sound, 1);
        fruitEatenSound = soundPool.load(context, R.raw.fruit_eaten, 1);

    }

    /**
     * Plays a sound for a high score. Called by UI thread in main activity.
     */
    public void playHighScoreSound() {
        this.soundPool.play(highScoreSound,1, 1, 1, 0, 1);
    }
}
