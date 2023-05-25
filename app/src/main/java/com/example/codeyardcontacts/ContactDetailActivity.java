package com.example.codeyardcontacts;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ContactDetailActivity extends AppCompatActivity{


    // változok a kontakt adatoknak .
    private String name, email, address, phoneNumber, imageURL;
    private TextView contactTV, nameTV, emailText;
    private ImageView  idIVContact;

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

        // változoknak átatjuk az elemeket
        nameTV = findViewById(R.id.idTVName);
        contactTV = findViewById(R.id.idTVPhone);
        emailText = findViewById(R.id.emailText);
        idIVContact = findViewById(R.id.idIVContact);

        // feltöltjük a kapott értékekkel
        nameTV.setText(name);
        emailText.setText(email);
        contactTV.setText(phoneNumber);
        Picasso.get()
                .load(imageURL)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(idIVContact);


    }

}
