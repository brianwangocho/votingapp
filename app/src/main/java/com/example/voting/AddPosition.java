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

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddPosition extends AppCompatActivity {
    public DatabaseReference dref,mDatabase;
    FirebaseAuth userauth;
    EditText positionName;
    EditText description;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_position);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         positionName = (EditText)findViewById(R.id.positionedit);
         description =(EditText)findViewById(R.id.descriptionedit);
        Button addposition =(Button)findViewById(R.id.btnaddposition);
        userauth = FirebaseAuth.getInstance();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPosition.this,Dashboard.class);
                startActivity(intent);
            }
        });

        addposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPosition();
//                dref= FirebaseDatabase.getInstance().getReference().child("Position");
//                dref.removeValue();

            }
        });
    }

    public void addPosition(){
        dref= FirebaseDatabase.getInstance().getReference().child("Position");
        final DatabaseReference newPost = dref.push();
        final String POSITIONNAME = positionName.getText().toString().trim();
        final String DESCRIPTION = description.getText().toString().trim();
        if(TextUtils.isEmpty(POSITIONNAME) || TextUtils.isEmpty(DESCRIPTION)){
            View view = findViewById(R.id.addposition);
            snackbar= Snackbar.make(view,"fill all fields",Snackbar.LENGTH_LONG);
            View snackbarview = snackbar.getView();
            snackbarview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            snackbar.show();

        }
        else {
            newPost.child("name").setValue(POSITIONNAME.trim());
            newPost.child("description").setValue(DESCRIPTION.trim());
            Intent intent = new Intent(AddPosition.this, Dashboard.class);
            startActivity(intent);
        }

    }
}
