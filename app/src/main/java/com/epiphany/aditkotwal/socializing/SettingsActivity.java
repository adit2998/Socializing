package com.epiphany.aditkotwal.socializing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button updateSettings;
    private EditText status, username, name, country, dob, gender, relationshipStatus;
    private CircleImageView profilePic;
    private StorageReference userProfileImageRef;


    private DatabaseReference settingsUserRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    private String currentUserId;
    final static int Gallery_Pic = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        settingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        updateSettings = (Button) findViewById(R.id.settings_save);
        status = (EditText) findViewById(R.id.settings_status);
        username = (EditText) findViewById(R.id.settings_username);
        name = (EditText) findViewById(R.id.settings_name);
        country = (EditText) findViewById(R.id.settings_country);
        dob = (EditText) findViewById(R.id.settings_dob);
        gender = (EditText) findViewById(R.id.settings_gender);
        relationshipStatus = (EditText) findViewById(R.id.settings_relationshipStatus);

        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile images");

        loadingBar = new ProgressDialog(this);

        profilePic = (CircleImageView) findViewById(R.id.settings_profile_image);

        settingsUserRef.addValueEventListener(new ValueEventListener() {
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

                    Picasso.get().load(myProfileImage).into(profilePic);
                    status.setText(myStatus);
                    username.setText(myUsername);
                    name.setText(myName);
                    country.setText(myCountry);
                    dob.setText(myDob);
                    gender.setText(myGender);
                    relationshipStatus.setText(myRelationshipStatus);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateAccountInfo();
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pic);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_Pic && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }


        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                loadingBar.setTitle("Image is being uploaded");
                loadingBar.setMessage("Please wait while the image uploads");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                Uri resultUri = result.getUri();

                StorageReference filePath = userProfileImageRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {

                            Toast.makeText(SettingsActivity.this, "Image stored successfully", Toast.LENGTH_SHORT).show();

                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    settingsUserRef.child("profileimage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent selfIntent = new Intent(SettingsActivity.this, SettingsActivity.class);
                                                        startActivity(selfIntent);

                                                        Toast.makeText(SettingsActivity.this, "Profile image stored in firebase successfully", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    }
                });
            }
            else {
                Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }


    private void ValidateAccountInfo() {
        String Status = status.getText().toString();
        String userName = username.getText().toString();
        String Name = name.getText().toString();
        String Country = country.getText().toString();
        String DOB = dob.getText().toString();
        String Gender = gender.getText().toString();
        String RelationshipStatus = relationshipStatus.getText().toString();

        if(TextUtils.isEmpty(Status))
        {
            Toast.makeText(this, "Please enter status", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(userName))
        {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(Name))
        {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(Country))
        {
            Toast.makeText(this, "Please enter country", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(DOB))
        {
            Toast.makeText(this, "Please enter date of birth", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(Gender))
        {
            Toast.makeText(this, "Please enter gender", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(RelationshipStatus))
        {
            Toast.makeText(this, "Please enter relationship status", Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle("Image is being uploaded");
            loadingBar.setMessage("Please wait while the image uploads");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            UpdateAccountInfo(Status, userName, Name, Country, DOB, Gender, RelationshipStatus);
        }
    }

    private void UpdateAccountInfo(String status, String userName, String name, String country, String dob, String gender, String relationshipStatus) {
        HashMap userMap = new HashMap();
            userMap.put("status", status);
            userMap.put("username", userName);
            userMap.put("name", name);
            userMap.put("country", country);
            userMap.put("dob", dob);
            userMap.put("gender", gender);
            userMap.put("relationship status", relationshipStatus);

        settingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    sendUserToMain();
                    Toast.makeText(SettingsActivity.this, "Account settings updated successfully", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
                else
                {
                    Toast.makeText(SettingsActivity.this, "Error occured while updating", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void sendUserToMain() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }
}
