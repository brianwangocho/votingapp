package com.example.voting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voting.helpers.ConnectvivtyHelper;
import com.example.voting.scheduler.NotificationWorker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    public static final String MESSAGE_STATUS = "message_status";
    EditText emailAddress,password,reemailAddress,restudentNo,repassword,reconfirmPasword;
    Button loginBtn,regbtn;
    TextView registerLink;
    FirebaseAuth mAuth;
    DatabaseReference myuserDatabase;
    private ProgressDialog mprogress;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailAddress = (EditText)findViewById(R.id.emailedit);
        password =(EditText)findViewById(R.id.passwordedit);
        registerLink = (TextView)findViewById(R.id.registerlink);
        loginBtn =(Button)findViewById(R.id.btnlogin);

        mAuth = FirebaseAuth.getInstance();
        mprogress = new ProgressDialog(this);
        myuserDatabase= FirebaseDatabase.getInstance().getReference().child("users");

        final WorkManager mWorkManager = WorkManager.getInstance();
        final OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class).build();



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWorkManager.enqueue(mRequest);
                login();
            }
        });



    }

    public void showregistrationdialog(View view) {
     Intent intent = new Intent(MainActivity.this,Register.class);
     startActivity(intent);
    }

    public void login(){
        String Email = emailAddress.getText().toString().trim();
        String pass = password.getText().toString().trim();
            if(ConnectvivtyHelper.isConnectedToNetwork(getApplicationContext())){
                if(!TextUtils.isEmpty(Email) && !TextUtils.isEmpty(pass)){
                    mprogress .setMessage("checking login...");
                    mprogress.show();
                    mAuth.signInWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mprogress.dismiss();
                                checkUserexist();
                                Intent intent = new Intent(MainActivity.this,Dashboard.class);
                                startActivity(intent);
                            }
                            else {
                                mprogress.dismiss();
                                View view = findViewById(R.id.mainactivity);
                                snackbar=Snackbar.make(view,"You have entered the wrong details",Snackbar.LENGTH_LONG);
                                View snackbarview = snackbar.getView();
                                snackbarview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                snackbar.show();
                            }

                        }
                    });
                }
                else if(TextUtils.isEmpty(Email)){
                    emailAddress.setError("please enter email address");

                }
                else if(TextUtils.isEmpty(pass)){
                    password.setError("please enter password");

                }

                else{
                    View view = findViewById(R.id.mainactivity);
                    snackbar=Snackbar.make(view,"Ensure all the fields are not empty",Snackbar.LENGTH_LONG);
                    View snackbarview = snackbar.getView();
                    snackbarview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    snackbar.show();
                }
            }
            else{
                View view = findViewById(R.id.mainactivity);
                snackbar=Snackbar.make(view,"please connect to the internet",Snackbar.LENGTH_LONG);
                View snackbarview = snackbar.getView();
                snackbarview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                snackbar.show();
            }
        }

    private void checkUserexist() {
        final String user_id =mAuth.getCurrentUser().getUid();
        myuserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id)){
                 registerLink.setVisibility(View.GONE);
                }
                else {
                    Toast.makeText(MainActivity.this,"USER DOESN'T EXIST,SET UP ACCOUNT",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

