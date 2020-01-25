package com.example.voting;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.voting.models.Position;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends AppCompatActivity {
    private RecyclerView reportlist;
    private DatabaseReference mydatabase,candidateref;
    private FloatingActionButton fab;
    FirebaseAuth mAuth;
    static FirebaseUser currentuser;
    Snackbar snackbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        reportlist = (RecyclerView) findViewById(R.id.ongoingvoteslist);
        fab = (FloatingActionButton)findViewById(R.id.floating_action_button);
        reportlist.setLayoutManager(new LinearLayoutManager(this));
        mAuth= FirebaseAuth.getInstance();

        mydatabase= FirebaseDatabase.getInstance().getReference().child("Position");
        candidateref= FirebaseDatabase.getInstance().getReference().child("candidate");
        currentuser = mAuth.getCurrentUser();
        checkusertype();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this,AddPosition.class);
                startActivity(intent);

            }
        });

    }

    public void checkusertype(){
        if(currentuser !=null){
            FirebaseDatabase.getInstance().getReference().child("users").child(currentuser.getUid())
                    .orderByChild("usertype")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                final String usertypvalue = dataSnapshot.child("usertype")
                                        .getValue().toString();
                                if(usertypvalue.equals("administrator")){
                                    fab.setVisibility(View.VISIBLE);
                                }
                                else{
                                    fab.setVisibility(View.GONE);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseRecyclerAdapter<Position,Dashboard.ReportViewHolder>Adapter = new FirebaseRecyclerAdapter<Position, Dashboard.ReportViewHolder>(
                Position.class,
                R.layout.cardview,
                Dashboard.ReportViewHolder.class,
                mydatabase


        ) {
            @Override
            protected void populateViewHolder(Dashboard.ReportViewHolder reportViewHolder, Position position, int i) {
                final String post_id = getRef(i).getKey();
                reportViewHolder.setName(position.getName());
                reportViewHolder.setDescription(position.getDescription());
                ImageView view = reportViewHolder.mView.findViewById(R.id.view);
                final TextView candidate = reportViewHolder.mView.findViewById(R.id.candidate);

                candidateref.child(post_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println(dataSnapshot.getChildrenCount());
                        if(!dataSnapshot.hasChildren()){
                            candidate.setText(getResources().getString(R.string.no_candidate));
                        }
                        candidate.setText(String.valueOf(dataSnapshot.getChildrenCount())+"Candidates");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                reportViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        if(currentuser !=null){
                            FirebaseDatabase.getInstance().getReference().child("users").child(currentuser.getUid())
                                    .orderByChild("usertype")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @SuppressLint("RestrictedApi")
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                final String usertypvalue = dataSnapshot.child("usertype")
                                                        .getValue().toString();
                                                if(usertypvalue.equals("administrator")){
                                                    mydatabase.child(post_id).removeValue();
                                                    View view = findViewById(R.id.votesdash);
                                                    snackbar= Snackbar.make(view,"Poll has been deleted",Snackbar.LENGTH_LONG);
                                                    View snackbarview = snackbar.getView();
                                                    snackbarview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                                    snackbar.show();

                                                }
                                                else{
                                                    View view = findViewById(R.id.votesdash);
                                                    snackbar= Snackbar.make(view,"you don't have the right to do this",Snackbar.LENGTH_LONG);
                                                    View snackbarview = snackbar.getView();
                                                    snackbarview.setBackgroundColor(getResources().getColor(R.color.red));
                                                    snackbar.show();
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }




                        return false;
                    }
                });




                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      Intent intent = new Intent(Dashboard.this, CandidateDash.class);
                        intent.putExtra("positionId",post_id);
                        startActivity(intent);

                    }
                });

            }

        };
        reportlist.setAdapter(Adapter);

    }
    public static class ReportViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=  itemView;
        }
        public void setDescription(String description){
            TextView description1 =(TextView)mView.findViewById(R.id.votingpositiondescription);
            description1.setText(description);
        }

        public void setName(String name){
            TextView name1 =(TextView)mView.findViewById(R.id.votingposition);
            name1.setText(name);
        }
    }
}
