package edu.bloomu.bmb56279.afinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Main Activity that will create the View layout programatically as wells a Snake
 * View and BGThread
 *
 * @author Brett Bernardi
 */
public class MainActivity extends AppCompatActivity {
    // The game engine
    SnakeView engine;
    // Displays the score
    TextView textView;

    BroadcastReceiver updateTextViewReciever;
    BroadcastReceiver addHighScoreReciever;
    Point point;
    // The root of the relative layout holding both the SurfaceView and the ImageView
    RelativeLayout rootPanel;
    // The parent of all views in this activity
    LinearLayout mainLinearLayout;
    // The linear layout for the top part of the screen holding the pause button and
    // the score textview
    LinearLayout upperLinearLayout;
    // Database helper to retrieve and write to high score database
    DatabaseHelper myDatabaseHelper;
    // User input. Name for high score
    String nameOfPlayer;
    // The Pause button
    Button pauseButton;
    // Controls sounds
    SoundPool soundPool;

    // The percentage of the total pixel size of the top Window
    public static final double percentageTopWindow = 0.10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the size of the display
        Display display = getWindowManager().getDefaultDisplay();
        this.point = new Point();
        display.getSize(point);

        // Remove the status bar for this activity
        //https://stackoverflow.com/questions/5431365/how-to-hide-status-bar-in-android
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // this will allow us to have
        // create new instance of SnakeEngine class
        this.engine = new SnakeView(point, this);

        this.myDatabaseHelper = new DatabaseHelper(this);

        // Sets up The various views needed.
        setUpViews();

        // Sets what happens when a message is received by the BG thread every single
        // iteration of the game loop. The message is the updated score.
        this.updateTextViewReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String text = intent.getStringExtra("text");
                textView.setText(String.format("%s%s", getString(R.string.score_string), text));
            }
        };

        // Sets what happens when a message is by the background thread indicating the
        // game is over. Will stop game music and check high score. Adds the score to
        // database if applicable.
        this.addHighScoreReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pauseButton.setEnabled(false);
                String text = intent.getStringExtra("text");
                int highScore = Integer.parseInt(text);
                // if score is a new high score, get Name from player and add it to
                // database.
                if(myDatabaseHelper.isNewScoreARecord(highScore)) {

                    try {
                        Thread.sleep(1200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getNameDialogBox(highScore);
                    engine.playHighScoreSound();
                }
                // If not a high score, sleep for a few seconds, and start new game
                else {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Cannot make Toasts on any thread besides UIThread
                    Toast.makeText(getApplicationContext(),
                            "New Game. Good Luck!", Toast.LENGTH_SHORT).show();
                    pauseButton.setEnabled(true);
                    engine.setStartNewGame(true);
                }


            }
        };

        setContentView(mainLinearLayout);

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(updateTextViewReciever, new IntentFilter(
                "ACTION_UPDATE_TEXT_VIEW"));
        registerReceiver(addHighScoreReciever, new IntentFilter(
                "ACTION_ADD_HIGH_SCORE"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        // unregister receiver
        unregisterReceiver(updateTextViewReciever);
        unregisterReceiver(addHighScoreReciever);
    }

    /**
     * IF called, a game is over and the player got a high score. Get the players name
     * with a diaglog box and add a new record into the database. Sends a toast upon
     * successful completion.
     * @param newHighScore
     */
    private void getNameDialogBox(int newHighScore) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("NEW HIGH SCORE!");
        alertDialog.setMessage("Enter Your Name");



        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.snakehead);

        alertDialog.setPositiveButton("ENTER",
                (dialog, which) -> {
                    this.nameOfPlayer = input.getText().toString();
                    // set flag to true
                    if(myDatabaseHelper.addData(nameOfPlayer, newHighScore)) {
                        Toast.makeText(getApplicationContext(),
                                "High Score Saved!", Toast.LENGTH_SHORT).show();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Cannot make Toasts on any thread besides UIThread
                    Toast.makeText(getApplicationContext(),
                            "New Game. Good Luck!", Toast.LENGTH_SHORT).show();
                    pauseButton.setEnabled(true);
                    engine.setStartNewGame(true);

                });

        alertDialog.show();
    }

    private void setUpViews() {
        // Setup your SurfaceView
        engine.setZOrderOnTop(true);
        // this must be called from the thread holding the SurfaceView's window (which
        // is the main UI thread)
        engine.getHolder().setFormat(PixelFormat.TRANSPARENT);


        textView = new TextView(this);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setText(String.format("%s%s", getString(R.string.score_string), "0"));
        textView.setTextSize(25);
        textView.setPadding(0,15,0,0);
        textView.setTextColor(getColor(R.color.black_score));
        textView.setVisibility(View.VISIBLE);


        pauseButton = new Button(this);
        pauseButton.setBackgroundResource(R.drawable.splash_button_shapes);

        ImageView imageView1=new ImageView(this);
        imageView1.setImageResource(R.drawable.cropped_plank);

        // Use a RelativeLayout to overlap both SurfaceView and ImageView
        RelativeLayout.LayoutParams upperRelativeLayoutParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout woodenPlankScore = new RelativeLayout(this);
        woodenPlankScore.setLayoutParams(upperRelativeLayoutParams);
        woodenPlankScore.addView(imageView1, upperRelativeLayoutParams);
        woodenPlankScore.addView(textView, upperRelativeLayoutParams);



        this.upperLinearLayout = new LinearLayout(this);
        this.upperLinearLayout.setBackgroundResource(R.drawable.gradient_upper_section);
        LinearLayout.LayoutParams upperParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1);

        upperLinearLayout.addView(woodenPlankScore, upperParams);
        upperLinearLayout.addView(pauseButton, upperParams);

        pauseButton.setText("Pause");
        pauseButton.setTextSize(20f);
        // Event Handler for clicking the Pause button.
        pauseButton.setOnClickListener(v -> {
            engine.togglePaused();
            if(engine.getIsThreadPaused()) {
                pauseButton.setText("Resume");
            }
            else {
                pauseButton.setText("Pause");
            }
        });

        this.mainLinearLayout = new LinearLayout(this);
        mainLinearLayout.setOrientation(LinearLayout.VERTICAL);

;




        // Setup up ImageView for background image
        ImageView bgImagePanel = new ImageView(this);
        bgImagePanel.setBackgroundResource(R.drawable.background);

        // Use a RelativeLayout to overlap both SurfaceView and ImageView
        RelativeLayout.LayoutParams fillParentLayout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        this.rootPanel = new RelativeLayout(this);
        rootPanel.setLayoutParams(fillParentLayout);
        rootPanel.addView(engine, fillParentLayout);
        rootPanel.addView(bgImagePanel, fillParentLayout);

        mainLinearLayout.addView(upperLinearLayout);
        mainLinearLayout.addView(rootPanel);

        // Gets linearlayout
        LinearLayout.LayoutParams layoutParmas = (LinearLayout.LayoutParams) upperLinearLayout.getLayoutParams();
        layoutParmas.height = (int)(percentageTopWindow * this.point.y);
        layoutParmas.width = LinearLayout.LayoutParams.MATCH_PARENT;

        upperLinearLayout.setLayoutParams(layoutParmas);
    }






}