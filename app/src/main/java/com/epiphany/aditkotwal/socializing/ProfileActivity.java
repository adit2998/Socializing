package com.epiphany.aditkotwal.socializing;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView Name, Username, Status, Country, Gender, Dob, RelationShipStat;
    private CircleImageView profileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference profileUserRef;

    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        profileUserRef =FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        Name = (TextView) findViewById(R.id.my_profile_name);
        Username = (TextView) findViewById(R.id.my_profile_username);
        Status = (TextView) findViewById(R.id.my_profile_status);
        Country = (TextView) findViewById(R.id.my_profile_country);
        Gender = (TextView) findViewById(R.id.my_profile_gender);
        Dob = (TextView) findViewById(R.id.my_profile_dob);
        RelationShipStat = (TextView) findViewById(R.id.my_profile_relationship);
        profileImage = (CircleImageView) findViewById(R.id.my_profile_image);

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myStatus = dataSnapshot.child("status").getValue().toString();
                    String myUsername = dataSnapshot.child("username").getValue().toString();
                    String myName = dataSnapshot.child("name").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myDob = dataSnapshot.child("dob").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationshipStatus = dataSnapshot.child("relationship status").getValue().toString();

                    Picasso.get().load(myProfileImage).into(profileImage);
                    Status.setText(myStatus);
                    Username.setText("@"+ myUsername);
                    Name.setText(myName);
                    Country.setText("Country: "+myCountry);
                    Dob.setText("DOB: " +myDob);
                    Gender.setText("Gender: " +myGender);
                    RelationShipStat.setText("Relationship status: "+myRelationshipStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
