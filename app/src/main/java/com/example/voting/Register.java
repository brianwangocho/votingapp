package com.example.voting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.voting.helpers.ConnectvivtyHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    EditText studentId,name;
    EditText emailAddress;
    EditText password;
    EditText confirmPassword;
    Button registerBtn;
    FirebaseAuth mAuth;
    DatabaseReference myuserDatabase;
    private ProgressDialog mprogress;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        studentId = (EditText)findViewById(R.id.regstudentidedit);
        name = (EditText)findViewById(R.id.regstudentname);
        emailAddress =(EditText)findViewById(R.id.regemailedit);
        password = (EditText)findViewById(R.id.regpasswordedit);
        confirmPassword = (EditText)findViewById(R.id.confirmpasswordedit);
        registerBtn = (Button)findViewById(R.id.btnregister);

        studentId.setFocusable(true);
        studentId.setFocusableInTouchMode(true);
        studentId.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(studentId, InputMethodManager.SHOW_IMPLICIT);

        mAuth = FirebaseAuth.getInstance();
        mprogress = new ProgressDialog(this);
        myuserDatabase= FirebaseDatabase.getInstance().getReference().child("users");

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    public void register()
    {
        final String REGSTUDENTID = studentId.getText().toString().trim();
        final String  REGEMAIL = emailAddress.getText().toString().trim();
        final String  NAME = name.getText().toString().trim();
        String REGPASSWORD = password.getText().toString().trim();
        String CONFIRMPASS = confirmPassword.getText().toString().trim();

        if(ConnectvivtyHelper.isConnectedToNetwork(getApplicationContext())){
            if(!TextUtils.isEmpty(REGSTUDENTID) && !TextUtils.isEmpty(REGEMAIL)
                    && !TextUtils.isEmpty(REGPASSWORD) && !TextUtils.isEmpty(CONFIRMPASS)){

                if(REGPASSWORD.equals(CONFIRMPASS)){
                    mprogress.setMessage("Signing Up...");
                    mprogress.show();
                    mAuth.createUserWithEmailAndPassword(REGEMAIL,REGPASSWORD).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String uid = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user = myuserDatabase.child(uid);
                                current_user.child("email").setValue(REGEMAIL);
                                current_user.child("name").setValue(NAME);
                                current_user.child("schoolid").setValue(REGSTUDENTID);
                                current_user.child("usertype").setValue("user");
                                current_user.child("image").setValue("default");
                                mprogress.dismiss();
                                Intent intent = new Intent(Register.this,MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                View view = findViewById(R.id.register);
                                snackbar= Snackbar.make(view,"something went wrong try again later",Snackbar.LENGTH_LONG);
                                View snackbarview = snackbar.getView();
                                snackbarview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                mprogress.dismiss();
                                snackbar.show();
                            }

                        }
                    });

                }
                else{
                    View view = findViewById(R.id.register);
                    snackbar=Snackbar.make(view,"Ensure password and confirm password are equal",Snackbar.LENGTH_LONG);
                    View snackbarview = snackbar.getView();
                    snackbarview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    snackbar.show();
                }
            }
            else{
                View view = findViewById(R.id.register);
                snackbar=Snackbar.make(view,"Ensure all the fields are not empty",Snackbar.LENGTH_LONG);
                View snackbarview = snackbar.getView();
                snackbarview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                snackbar.show();
            }
        }
        else{
            View view = findViewById(R.id.register);
            snackbar=Snackbar.make(view,"please connect to the internet",Snackbar.LENGTH_LONG);
            View snackbarview = snackbar.getView();
            snackbarview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            snackbar.show();
        }

    }
}
