package com.example.voting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.voting.models.Candidate;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AddCandidate extends AppCompatActivity {
    public DatabaseReference dref;
    public DatabaseReference userDatabase;
    FirebaseAuth userauth;
    static FirebaseUser currentuser;
    EditText description;
    String positionId;
    Snackbar snackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_candidate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        description =(EditText)findViewById(R.id.candidatestatement);
        Button addposition =(Button)findViewById(R.id.submitcandidate);
        positionId = getIntent().getExtras().getString("positionId");
        userauth = FirebaseAuth.getInstance();
        currentuser = userauth.getCurrentUser();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddCandidate.this,CandidateDash.class);
                startActivity(intent);
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

                dref.setValue(new Candidate(dataSnapshot.child("name").getValue().toString(),
                        description.getText().toString().trim()));
//                newPost.child("candidateName").setValue(dataSnapshot.child("name").getValue());
//                newPost.child("candidateBio").setValue(description.getText().toString().trim());
                Intent intent = new Intent(AddCandidate.this,CandidateDash.class);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
