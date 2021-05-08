package edu.bloomu.bmb56279.afinal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

/**
 * Activity for the Splash Screen. Allows the player to check the High Score board as
 * well as begin a new game. Has a floating action bar that will briefly explain how to
 * play the game.
 *
 * @author Brett Bernardi
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        Display display = ((Activity) this).getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        System.out.println(screenHeight);
        System.out.println(screenWidth);

        ImageView img = findViewById(R.id.splash_image);
        img.setTranslationX(-screenWidth/2);
        img.animate().translationX(0)
                .setDuration(1000)
                .setStartDelay(400).start();

        TextView textView = findViewById(R.id.title_text_view);
        textView.setTranslationX(screenWidth/2);
        textView.animate().translationX(0)
                .setDuration(1000)
                .setStartDelay(400).start();

        // Set event handler for Floating Action Bar
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String instructions =
                        getResources().getString(R.string.instructions);
                // Display a SnackBar with the instructions.
                Snackbar.make(v, instructions, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    public void startGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("doesn't matter", 1);
        startActivity(intent);
    }


    public void getHighScores(View view) {
        Intent intent = new Intent(this, ListHighScoresActivity.class);
        intent.putExtra("start", 1);
        startActivity(intent);
    }
}