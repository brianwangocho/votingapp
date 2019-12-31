package com.example.voting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voting.models.Candidate;
import com.example.voting.models.Position;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CandidateDash extends AppCompatActivity {

    private RecyclerView candidateList;
    private DatabaseReference mydatabase;
    private FloatingActionButton fab;
    FirebaseAuth mAuth;
    static FirebaseUser currentuser;
    public String positionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate);
        candidateList = (RecyclerView)findViewById(R.id.candidatelist);
        fab =(FloatingActionButton)findViewById(R.id.addCandidate);
        candidateList.setLayoutManager(new LinearLayoutManager(this));
        mAuth= FirebaseAuth.getInstance();
        positionId = getIntent().getExtras().getString("positionId");
        Toast.makeText(CandidateDash.this,positionId,Toast.LENGTH_SHORT).show();
        mydatabase= FirebaseDatabase.getInstance().getReference().child("candidate").child(positionId);
        currentuser = mAuth.getCurrentUser();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CandidateDash.this,AddCandidate.class);
                intent.putExtra("positionId",positionId);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseRecyclerAdapter<Candidate,CandidateDash.ReportViewHolder> Adapter = new FirebaseRecyclerAdapter<Candidate,CandidateDash.ReportViewHolder>(
                Candidate.class,
                R.layout.candidatecardview,
                CandidateDash.ReportViewHolder.class,
                mydatabase


        ){
            @Override
            protected void populateViewHolder(CandidateDash.ReportViewHolder reportViewHolder, Candidate candidate, int i) {
                final String candidate_id = getRef(i).getKey();
                reportViewHolder.setCandidateName(candidate.getCandidateName());
                reportViewHolder.setCandidateBio(candidate.getCandidateBio());
                Button view = reportViewHolder.mView.findViewById(R.id.vote);
                final TextView textView = reportViewHolder.mView.findViewById(R.id.percentage);

                DatabaseReference votenode =FirebaseDatabase.getInstance().
                        getReference().child("Voting")
                        .child(positionId).child(candidate_id);

                votenode.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChildren()){

                            textView.setText("No votes have been casted");

                        }
                        textView.setText(String.valueOf(dataSnapshot.getChildrenCount())+" Votes");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                    //todo:understand this usage
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("clicked");
                     Boolean vote =voteCandidate(candidate_id,positionId);
                     if(vote){
                         final Dialog dialog = new Dialog(CandidateDash.this);
                         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                         dialog.setContentView(R.layout.success_dialog);
                         Button confirm = dialog.findViewById(R.id.confirm);
                         confirm.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                dialog.dismiss();
                             }
                         });
                         dialog.show();
                     }
                     else{
                         Toast.makeText(CandidateDash.this,"you have already voted",Toast.LENGTH_LONG);
                     }
                    }
                });

            }

        };
        candidateList.setAdapter(Adapter);
    }

    private Boolean voteCandidate(String candidateId,String PositionId) {
        DatabaseReference votenode =FirebaseDatabase.getInstance().getReference().child("Voting").child(PositionId).child(candidateId);
        final DatabaseReference voter = votenode.push();
        voter.child("voterId").setValue(currentuser.getUid());
        return true;

    }


    public static class ReportViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=  itemView;
        }
        public void setCandidateName(String candidateName){
            TextView candidateName1 =(TextView)mView.findViewById(R.id.candidateName);
            candidateName1.setText(candidateName);
        }

        public void setCandidateBio(String bio){
            TextView candidateBio =(TextView)mView.findViewById(R.id.candidateBio);
            candidateBio.setText(bio);
        }
    }
}
