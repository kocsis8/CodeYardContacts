package com.example.codeyardcontacts;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void addContact(View view) {
        ArrayList<String> emails = new ArrayList<>();
        emails.add("kocsis.marton.pal@gmail.com");

        ArrayList<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add("+36703102944");

        Contact contact = new Contact("Kocsis Marci",emails,"5600 Békéscsaba Lencsési út 28",phoneNumbers,"/kep");
        
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
        Toast.makeText(MainActivity.this, "A gombra kattintottál!", Toast.LENGTH_SHORT).show();
    }
}