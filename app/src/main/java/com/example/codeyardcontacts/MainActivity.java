package com.example.codeyardcontacts;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {



    // firestore elérése
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // contact listázásnézet
   private RecyclerView mRecyclerView;
   private ArrayList<Contact> mContactList;
   private ContactsItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // recycle view
        mRecyclerView = findViewById(R.id.recyclerView);
        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                this, 1));
        // Initialize the ArrayList that will contain the data.
        mContactList = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new ContactsItemAdapter(this, mContactList);
        mRecyclerView.setAdapter(mAdapter);

        initContactsList();
    }

    private void initContactsList() {
        mContactList.clear();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("contacts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Adatok lekérdezése és feldolgozása
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Minden egyes gyermek elem feldolgozása

                    String name = snapshot.child("name").getValue(String.class);
                    String email =  snapshot.child("email").getValue(String.class);
                    String address =  snapshot.child("address").getValue(String.class);
                    String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                    String imageURL =  snapshot.child("imageURL").getValue(String.class);
                    mContactList.add( new Contact(name,email,address,phoneNumber,imageURL));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiba esetén kezeld a hibát itt
            }
        });



        // Notify the adapter of the change.
        mAdapter.notifyDataSetChanged();

    }



    //plusz gomb event
    public void addContact(View view) {
        generateContact();

    }

    // ami a contact obj menti firestore-ba
    private void addFirebase(Contact contact) {
        db.collection("contacts")
                .add(contact)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    // egyszerü cb alakítás miatti interface
    public interface RandomUserCallback {
        void onContactLoaded(Contact contact);
    }

    // kontakt generálásárt felel ö futatja le az aszinkron task osztályt
    private void generateContact() {

        RandomUserAsyncTask task = new RandomUserAsyncTask(new RandomUserCallback() {
            @Override
            public void onContactLoaded(Contact contact) {
                if (contact != null) {
                    handleContact(contact);
                }
            }
        });
        task.execute();
    }
    // event et figyeli és köldi tovább a kész kontakt objektumot
    private void handleContact(Contact contact) {
      addFirebase(contact);
    }
    // async osztály randomuser.me hez
    public class RandomUserAsyncTask extends AsyncTask<Void, Void, Contact> {
        private static final String API_URL = "https://randomuser.me/api/";
        private RandomUserCallback callback;

        public RandomUserAsyncTask(RandomUserCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Contact doInBackground(Void... params) {

            try {
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray results = jsonResponse.getJSONArray("results");
                    if (results.length() > 0) {
                        JSONObject jsonUser = results.getJSONObject(0);
                        return parseContact(jsonUser);
                    }
                }
            } catch (IOException | JSONException e) {
                Log.e("RandomUserAsyncTask", "Hálózati hiba történt", e);
            }
            return null;
        }

        private Contact parseContact(JSONObject jsonUser) throws JSONException {



            String name = jsonUser.getJSONObject("name").getString("first") + " " +
                          jsonUser.getJSONObject("name").getString("last");
            String email = jsonUser.getString("email");
            String address = jsonUser.getJSONObject("location").getJSONObject("street").getString("name")+", "+
                             jsonUser.getJSONObject("location").getJSONObject("street").getString("number");
            String phoneNumber = jsonUser.getString("phone");
            String imageURL = jsonUser.getJSONObject("picture").getString("large");

            return new Contact(name, email, address, phoneNumber, imageURL);
        }

        @Override
        protected void onPostExecute(Contact contact) {
            if (callback != null) {
                callback.onContactLoaded(contact);
            }
        }
    }

}






