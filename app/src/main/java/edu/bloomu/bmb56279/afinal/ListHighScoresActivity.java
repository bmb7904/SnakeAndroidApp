package edu.bloomu.bmb56279.afinal;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

/**
 * A custom activity that will populate a ListView with a query from a database.
 */
public class ListHighScoresActivity extends AppCompatActivity {
    // Reference to DatabaseHelper class
    DatabaseHelper myDatabaseHelper;
    // Reference to the ListView inflated from XML file.
    private ListView listView;
    private static final String title = "High Scores";
    TextView titleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get
        setContentView(R.layout.list_layout);
        listView = findViewById(R.id.list_view);
        // Add a Header text view for the Title
        this.titleView = new TextView(this);

        myDatabaseHelper = new DatabaseHelper(this);

        populateList();
    }

    /**
     * Gets a cursor from a query using the getData method of the DatabaseHelper class,
     * which is a references to a query of the database. The list should be sorted, so
     * using the cursor we convert the query into a list containing the high scores and
     * the name. We then convert the list to a List adapter, which can then be used to
     * set the ListView.
     */
    private void populateList() {
        // get the data
        Cursor data = myDatabaseHelper.getData();
        ArrayList<String> list = new ArrayList<>();

        while(data.moveToNext()) {
            // get the data from the DB from column 1 and
            // add it to the ArrayList
            list.add(String.format("%5s", data.getString(2) )+ "               " + data.getString(1));
        }

        // Create a ListAdapter from the ArrayList
        // This is the bridge between a ListView and the data that backs the list.
        // Frequently that data comes from a Cursor, but that is not required. The
        // ListView can display any data provided that it is wrapped in a ListAdapter.
        ListAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);


        listView.addHeaderView(titleView);
        titleView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleView.setText(title);
        titleView.setTextSize(100f);
        titleView.setTextColor(getColor(R.color.high_score_text));

    }
}
