package com.example.voting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.voting.models.Candidate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddCandidate extends AppCompatActivity {
    public DatabaseReference dref;
    public DatabaseReference userDatabase;
    public StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    FirebaseAuth userauth;
    Uri downloadUrl;
    //Firebase
    FirebaseStorage storage;


    static FirebaseUser currentuser;
    String name;
    Button uploadImage;
    ImageView imageView;
    EditText description;
     String mDownloadUrl;
    String positionId;
    Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_candidate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        description =(EditText)findViewById(R.id.candidatestatement);
        imageView =(ImageView)findViewById(R.id.candidateUploadImage);
        Button addposition =(Button)findViewById(R.id.submitcandidate);
        positionId = getIntent().getExtras().getString("positionId");
        userauth = FirebaseAuth.getInstance();
        FirebaseStorage storage;
                currentuser = userauth.getCurrentUser();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCandidate.this,CandidateDash.class);
                startActivity(intent);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();

            }
        });
        addposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///check if the candidate has already registered in this pool using the id
                FirebaseDatabase.getInstance().getReference().child("candidate")
                        .child(positionId).child(currentuser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            View view = findViewById(R.id.addcandidateview);
                            snackbar= Snackbar.make(view,"You have already registered to this poll",Snackbar.LENGTH_LONG);
                            View snackbarview = snackbar.getView();
                            snackbarview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            snackbar.show();
                        }
                        else{
                                //// if candidate doesnt exist
                            addCandidate();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    private void addCandidate() {
        userDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(currentuser.getUid());
        dref= FirebaseDatabase.getInstance().getReference().child("candidate").child(positionId).child(currentuser.getUid());

        final DatabaseReference newPost = dref.push();
             final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/"+ UUID.randomUUID().toString());

        if(filePath != null)
        {
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDownloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();



                    userDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<Object,String> map = new HashMap<Object, String>();
//                Candidate c = new Candidate();
//                c.setCandidateBio(description.getText().toString().trim());
//                c.setCandidateName(dataSnapshot.child("name").getValue().toString());
                            String bio = description.getText().toString();
                            if(TextUtils.isEmpty(bio)){
                                description.setError("please add bio");
                            }

                            name = dataSnapshot.child("name").getValue().toString();
                dref.setValue(new Candidate(name,
                        description.getText().toString().trim(),mDownloadUrl));
//                newPost.child("candidateName").setValue(dataSnapshot.child("name").getValue());
//                newPost.child("candidateBio").setValue(description.getText().toString().trim());
                Intent intent = new Intent(AddCandidate.this,CandidateDash.class);
                startActivity(intent);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            System.out.println("databaseError = " + databaseError);

                        }
                    });

//                    dref.setValue(new Candidate(name,
//                        description.getText().toString().trim(),
//                            taskSnapshot.getMetadata().getReference().getDownloadUrl()
//                            ));
                    Intent intent = new Intent(AddCandidate.this,CandidateDash.class);
                    startActivity(intent);

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddCandidate.this,
                                    "Failed "+e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("e = " + e);
                        }
                    })

                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });

        }




    }


    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
