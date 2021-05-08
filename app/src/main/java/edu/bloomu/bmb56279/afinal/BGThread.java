package edu.bloomu.bmb56279.afinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * A custom class that extends Thread to act as the worker thread for this game. This
 * thread will perform the update and rendering of the game.
 *
 * @author Brett Bernardi
 */
public class BGThread extends Thread {
    private boolean running;
    private SnakeView gameView;
    private static Canvas canvas;
    private boolean isRunning;
    Context context;
    boolean isThreadPause;
    SurfaceHolder surfaceHolder;

    /**
     * Constructor to create BGThread object.
     */
    public BGThread(SnakeView snakeView, SurfaceHolder surfaceHolder, Context c) {
        super();
        this.gameView = snakeView;
        this.context = c;
        this.isThreadPause = false;
        this.surfaceHolder = surfaceHolder;
    }

    /**
     * Sets the boolean running, which is true when we want the game to be running
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * This is my first attempt to actually implement a multithreaded application for a
     * real world situation. I am not sure how good this is. It seems ugly to me.
     * However, considering the main UI thread and this BG Thread, I cannot find any
     * possible racing conditions. I ended up using a few different booleans to get the
     * exact behavior that I want. I am sure there is a much better way to implement
     * this run method with this functionality, but this was the best I could do.
     */
    @Override
    public void run() {
        while (running) {
            // if game is over, run this code once.
            if (gameView.isGameOver()) {
                // Send Score to main UI Thread
                Intent intent = new Intent("ACTION_ADD_HIGH_SCORE");
                intent.putExtra("text", Integer.toString(gameView.getScore()));
                context.sendBroadcast(intent);
                // reset flag. This is the only place where this flag is set to false,
                // so no racing conditions.
                gameView.setGameOver(false);
            }

            if (!gameView.getIsThreadPaused()) {

                // check if game is paused
                if (gameView.updateNeeded()) {
                    gameView.updateGameState();

                    if (surfaceHolder != null) {

                        try {
                            // This hack was needed due to a rare bug that occurs every so often
                            // when the Surface is destroyed, but the thread is executing in this
                            // location of the run method. Thus, it will try to call the draw
                            // method below while the surface is destroyed and before it checks
                            // the running boolean at the top of the loop.
                            if (!running) {
                                break;
                            }
                            gameView.draw();
                        } catch (InterruptedException e) {
                            //
                        }
                    }
                    Intent intent = new Intent("ACTION_UPDATE_TEXT_VIEW");
                    intent.putExtra("text", Integer.toString(gameView.getScore()));
                    context.sendBroadcast(intent);
                }
            }
            // If flag set, run this code once to begin new game
            if (gameView.getStartNewGame()) {
                // Create instance of Snake game.
                gameView.newGame();
                // set flag to false
                gameView.setStartNewGame(false);
                gameView.togglePaused();
            }


        }

    }
}



