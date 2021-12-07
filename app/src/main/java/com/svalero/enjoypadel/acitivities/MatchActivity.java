package com.svalero.enjoypadel.acitivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.svalero.enjoypadel.R;
import com.svalero.enjoypadel.adapter.PlayerAdapter;
import com.svalero.enjoypadel.database.AppDatabase;
import com.svalero.enjoypadel.domain.Match;
import com.svalero.enjoypadel.domain.Player;

import java.util.ArrayList;
import java.util.List;

public class MatchActivity extends AppCompatActivity {

    private List<Match> matches;
    private ArrayAdapter<Match> matchesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        matches = new ArrayList<>();
        ListView lvMatches = findViewById(R.id.match_list);
        matchesAdapter = new ArrayAdapter<Match>(this, android.R.layout.simple_list_item_1, matches);
        lvMatches.setAdapter(matchesAdapter);
        registerForContextMenu(lvMatches);
    }

    @Override
    protected void onResume() {
        super.onResume();

        matches.clear();
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "tournament").allowMainThreadQueries()
                .fallbackToDestructiveMigration().build();

        matches.addAll(db.matchDao().getAll());
        matchesAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_match_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_match) {
            Intent intent = new Intent(this, AddMatchActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.contextual_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Match match = matches.get(info.position);

        switch (item.getItemId()) {
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure?")
                        .setPositiveButton("Si",
                                (dialog, which) -> {
                                    AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                                            AppDatabase.class, "tournament").allowMainThreadQueries()
                                            .fallbackToDestructiveMigration().build();
                                    db.matchDao().delete(match);
                                    matches.remove(match);
                                    matchesAdapter.notifyDataSetChanged();
                                }
                        ).setNegativeButton("No",
                        (dialog, which) -> dialog.dismiss());
                builder.create().show();
                return true;


            case R.id.action_edit:
                Intent intent = new Intent(this, AddMatchActivity.class);
                intent.putExtra("round", match.getRound());
                intent.putExtra("duration", match.getDuration());
                intent.putExtra("date", match.getDate());
                intent.putExtra("matchScore", match.getMatchScore());
                intent.putExtra("playerOne", match.getPlayerOne());
                intent.putExtra("playerTwo", match.getPlayerTwo());
                intent.putExtra("playerThree", match.getPlayerThree());
                intent.putExtra("playerFour", match.getPlayerFour());
                intent.putExtra("matchId", match.getId());
                intent.putExtra("modify", 1);
                startActivity(intent);
                return true;
        }
        return super.onContextItemSelected(item);
    }
}