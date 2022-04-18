package suyat.it32s1.finalproject.AddTrackActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import suyat.it32s1.finalproject.Artist.ArtistMain;
import suyat.it32s1.finalproject.Artist.Track;
import suyat.it32s1.finalproject.Artist.TrackList;
import suyat.it32s1.finalproject.R;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AddTrackActivity extends AppCompatActivity {
    //define the view
    Button buttonAddTrack;
    EditText editTextTrackName;
    SeekBar seekBarRating;
    TextView textViewRating, textViewArtistName;
    ListView listViewTracks;

    //define the databaseReference for Tracks Database
    DatabaseReference databaseTracks;

    List<Track> tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_track);

        //initialize all the view
        buttonAddTrack = (Button) findViewById(R.id.btnAddTrack);
        editTextTrackName = (EditText) findViewById(R.id.editTrackName);
        seekBarRating = (SeekBar) findViewById(R.id.seekBarRating);
        textViewRating = (TextView) findViewById(R.id.textViewRating);
        textViewArtistName = (TextView) findViewById(R.id.textViewArtistName);
        listViewTracks = (ListView) findViewById(R.id.listViewTracks);

        //Add another intent
        Intent intent = getIntent();

        tracks = new ArrayList<> ();

        String id = intent.getStringExtra(ArtistMain.ARTIST_ID);
        String name = intent.getStringExtra(ArtistMain.ARTIST_NAME);

        textViewArtistName.setText(name);

        //get the track node from the firebase database
        databaseTracks = FirebaseDatabase.getInstance().getReference("tracks").child(id);

        buttonAddTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTrack();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart ();

        databaseTracks.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tracks.clear ();
                for (DataSnapshot trackSnapshot : dataSnapshot.getChildren ()) {
                    Track track = trackSnapshot.getValue (Track.class);
                    tracks.add ( track );
                }
                TrackList trackListAdapter = new TrackList ( AddTrackActivity.this, tracks );
                listViewTracks.setAdapter ( trackListAdapter );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void saveTrack() {
        String trackName = editTextTrackName.getText().toString().trim();
        int trackRating = seekBarRating.getProgress();
        if (!TextUtils.isEmpty(trackName)) {
            String id = databaseTracks.push().getKey();
            Track track = new Track(id, trackName, trackRating);
            databaseTracks.child(id).setValue(track);
            Toast.makeText(this, "Track saved", Toast.LENGTH_LONG).show();
            editTextTrackName.setText("");
        } else {
            Toast.makeText(this, "Please enter track name", Toast.LENGTH_LONG).show();
        }
    }

}