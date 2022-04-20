package suyat.it32s1.finalproject.Artist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import suyat.it32s1.finalproject.AddTrackActivity.AddTrackActivity;
import suyat.it32s1.finalproject.MainActivity;
import suyat.it32s1.finalproject.R;

public class ArtistMain extends AppCompatActivity {

    //we will use this constants later to pass the artist name and id to another activity
    public static final String ARTIST_NAME = "artistname";
    public static final String ARTIST_ID = "artistid";

    //view objects
    EditText editTextName;
    Spinner spinnerGenre;
    Button btnAddArtist;
    ImageButton btnLogOut;

    ListView listViewArtists;

    List<Artist> artists;
    DatabaseReference databaseArtists;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        requestWindowFeature ( Window.FEATURE_NO_TITLE );
        getSupportActionBar ().hide ();
        setContentView ( R.layout.activity_artist_main );

        //getting the reference of artists node
        databaseArtists = FirebaseDatabase.getInstance().getReference("artists");

        //getting views
        editTextName = (EditText) findViewById(R.id.editTextName);
        btnAddArtist = (Button) findViewById(R.id.btnAddArtist);
        spinnerGenre = (Spinner) findViewById(R.id.spinnerGenre);
        btnLogOut = (ImageButton) findViewById(R.id.btnLogout);
        listViewArtists = (ListView) findViewById ( R.id.listViewArtists );

        mAuth = FirebaseAuth.getInstance();

        //list to store artists
        artists = new ArrayList<> ();

        btnAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calling the method addArtist()
                //the method is defined below
                //this method is actually performing the write operation
                addArtist();
            }
        });

        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // getting the selected artist
                Artist artist = artists.get(i);

                // creating an intent
                Intent intents = new Intent(ArtistMain.this, AddTrackActivity.class);

                // putting artist name and id to intent
                intents.putExtra(ARTIST_ID, artist.getArtistId());
                intents.putExtra(ARTIST_NAME, artist.getArtistName());

                // starting the activity with intent
                startActivity(intents);
            }
        });

        listViewArtists.setOnItemLongClickListener ( new AdapterView.OnItemLongClickListener () {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = artists.get ( i );
                showUpdateDeleteDialog ( artist.getArtistId (), artist.getArtistName () );
                return false;
            }
        } );

        btnLogOut.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                mAuth.signOut ();
                Intent intent = new Intent (ArtistMain.this, MainActivity.class );
                startActivity ( intent );
            }
        } );

    }

    private void showUpdateDeleteDialog (String artistId, String artistName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder ( ArtistMain.this );
        LayoutInflater inflater = getLayoutInflater ();
        final View dialogView = inflater.inflate ( R.layout.update_dialog, null );
        dialogBuilder.setView ( dialogView );

        final EditText editTextName = (EditText) findViewById ( R.id.editTextName );
        final Spinner spinnerGenre = (Spinner) findViewById ( R.id.spinnerGenres );
        final Button buttonUpdate = (Button) dialogView.findViewById ( R.id.buttonUpdateArtist );
        final Button buttonDelete = (Button) dialogView.findViewById ( R.id.buttonDeleteArtist );

        dialogBuilder.setTitle ( "Updating Artist " + artistId );

        AlertDialog alertDialog = dialogBuilder.create ();
        alertDialog.show ();

        buttonUpdate.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText ().toString ().trim ();
                String genre = spinnerGenre.getSelectedItem ().toString ();

                if (TextUtils.isEmpty ( name )) {
                    editTextName.setError ( "Name is Required" );
                    return;
                }
                updateArtist(artistId, name, genre);
                alertDialog.dismiss ();
            }
        } );

        buttonDelete.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                deleteArtist(artistId);
                alertDialog.dismiss ();
            }

            private void deleteArtist (String artistId) {
                //getting the specified artist reference
                DatabaseReference databaseReference = FirebaseDatabase.getInstance ().getReference ("artists").child ( artistId );
                //getting the tracks reference for the specified artist
                DatabaseReference dRTracks = FirebaseDatabase.getInstance ().getReference ("tracks").child ( artistId );

                //removing artist
                databaseReference.removeValue ();
                //removing all tracks
                dRTracks.removeValue ();

                Toast.makeText ( getApplicationContext (), "Artist Deleted", Toast.LENGTH_LONG ).show ();
            }
        } );
    }

    private boolean updateArtist (String id, String name, String genre ) {
        //getting the specified artist reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance ("artists").getReference ().child ( id );

        //updating artist
        Artist artist = new Artist (id, name, genre);
        databaseReference.setValue ( artist );
        Toast.makeText ( ArtistMain.this, "Artist updated", Toast.LENGTH_LONG).show ();
        return true;
    }

    /*
     * This method is saving a new artist to the
     * Firebase Realtime Database
     * */
    private void addArtist() {
        //getting the values to save
        String name = editTextName.getText().toString().trim();
        String genre = spinnerGenre.getSelectedItem().toString();

        //checking if the value is provided
        if (!TextUtils.isEmpty(name)) {

            //getting a unique id using push().getKey() method
            //it will create a unique id and we will use it as the Primary Key for our Artist
            String id = databaseArtists.push().getKey();

            //creating an Artist Object
            Artist artist = new Artist(id, name, genre);

            //Saving the Artist
            databaseArtists.child(id).setValue(artist);

            //setting edittext to blank again
            editTextName.setText("");

            //displaying a success toast
            Toast.makeText(this, "Artist added", Toast.LENGTH_LONG).show();
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart ();

        //attaching value event listener
        databaseArtists.addValueEventListener ( new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clearing the previous artist list
                artists.clear ();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting artist
                    Artist artist = postSnapshot.getValue (Artist.class);
                    //adding artist to the list
                    artists.add ( artist );
                }

                //creating adapter
                ArtistList artistAdapter = new ArtistList ( ArtistMain.this, artists );
                //attaching adapter to the Listview
                listViewArtists.setAdapter ( artistAdapter );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
}