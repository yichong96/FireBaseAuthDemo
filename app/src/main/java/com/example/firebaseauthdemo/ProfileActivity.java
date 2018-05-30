package com.example.firebaseauthdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    // add comment
    // add  comment1

    private FirebaseAuth firebaseAuth;

    private static final int CHOOSE_IMAGE = 101;
    private TextView textViewUserEmail;
    private Button buttonLogout;

    private String name;
    private String address;

    private DatabaseReference databaseReference;
    private EditText editTextName, editTextAddress;
    private Button buttonSave;

    private FirebaseUser user;

    private EditText imageName;
    private ImageView imageView;
    String imageDescription;

    StorageReference mStorage;

    Uri uriProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = firebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        user = firebaseAuth.getCurrentUser();


        mStorage = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        editTextName = findViewById(R.id.editTextName);
        editTextAddress = findViewById(R.id.editTextAddress);
        buttonSave = findViewById(R.id.buttonSave);

        imageName = findViewById(R.id.imageName);
        imageView = findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });

        textViewUserEmail = findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText("Welcome " + user.getEmail());
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);
        buttonSave.setOnClickListener(this);

    }

    private void saveUserInformation() {
        name = editTextName.getText().toString().trim();
        address = editTextAddress.getText().toString().trim();
        imageDescription = imageName.getText().toString().trim();
////
//        UserInformation userInformation = new UserInformation(name,address);
//
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        databaseReference.child(user.getUid() + "/UserInfo/").setValue(userInformation).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(ProfileActivity.this,"Information saved...",Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
//            }
//        });
        //Toast.makeText(this,"Information saved...",Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(imageDescription) || uriProfileImage == null) {
            Toast.makeText(ProfileActivity.this, "Please fill in all entries and choose image", Toast.LENGTH_LONG).show();
        } else {
            StorageReference filepath = mStorage.child("Photos/UserProfilePicture/" + System.currentTimeMillis() + "." + "jpg");
            filepath.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    UserInformation userInformation = new UserInformation(name, address, imageDescription, uriProfileImage.toString());
                    DatabaseReference database = databaseReference.child(user.getUid()+ "/UserInfo");
                    database.setValue(userInformation);
                    Toast.makeText(ProfileActivity.this, "Upload done", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    //
    @Override
    public void onClick(View view) {
        if (view == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (view == buttonSave) {
            saveUserInformation();
        }
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK &&
                data != null & data.getData() != null) {
            uriProfileImage = data.getData();
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select profile Image"), CHOOSE_IMAGE);
    }
}
