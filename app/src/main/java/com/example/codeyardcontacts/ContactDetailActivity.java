package com.example.codeyardcontacts;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ContactDetailActivity extends AppCompatActivity{


    // változok a kontakt adatoknak .
    private String name, email, address, phoneNumber, imageURL;
    private TextView contactTV, nameTV;
    private ImageView  callIV, messageIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        // átadott változok
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        address = getIntent().getStringExtra("address");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        imageURL = getIntent().getStringExtra("imgURL");

        // initializing our views.
        nameTV = findViewById(R.id.idTVName);
        contactTV = findViewById(R.id.idTVPhone);
        nameTV.setText(name);
        contactTV.setText(phoneNumber);
        callIV = findViewById(R.id.idIVCall);
        messageIV = findViewById(R.id.idIVMessage);

    }

}
