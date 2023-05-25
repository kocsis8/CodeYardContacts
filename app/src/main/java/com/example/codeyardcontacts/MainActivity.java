package com.example.codeyardcontacts;

import static android.content.ContentValues.TAG;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
        mContactList = new ArrayList<Contact>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new ContactsItemAdapter(this, mContactList);

        mRecyclerView.setAdapter(mAdapter);

        initContactsList();

    }

    //lista betöltése
    private void initContactsList() {
        mContactList.clear();

        db.collection("contacts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String name = document.getString("name");
                                String email =  document.getString("email");
                                String address =  document.getString("address");
                                String phoneNumber = document.getString("phoneNumber");
                                String imageURL =  document.getString("imageURL");
                                Contact c = new Contact(name,email,address,phoneNumber,imageURL);

                                mContactList.add(c);

                            }
                            //lista fissítés
                            mAdapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Hiba történt a Firestore adatok lekérdezése közben: ", task.getException());
                        }
                    }

                });
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
      initContactsList();
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

    //menu inicializálása keresés miatt
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        Log.d(TAG, "keres: "+searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

}






