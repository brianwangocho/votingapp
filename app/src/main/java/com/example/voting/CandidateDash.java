package com.example.voting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voting.models.Candidate;
import com.example.voting.models.Position;
import com.example.voting.models.VotingObject;
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

import java.util.HashMap;
import java.util.Map;

public class CandidateDash extends AppCompatActivity {

    private RecyclerView candidateList;
    private DatabaseReference mydatabase;
    private FloatingActionButton fab;
    FirebaseAuth mAuth;
    static FirebaseUser currentuser;
    public String positionId;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate);
        candidateList = (RecyclerView) findViewById(R.id.candidatelist);
        fab = (FloatingActionButton) findViewById(R.id.addCandidate);
        candidateList.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        positionId = getIntent().getExtras().getString("positionId");
        mydatabase = FirebaseDatabase.getInstance().getReference().child("candidate").child(positionId);
        currentuser = mAuth.getCurrentUser();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CandidateDash.this, AddCandidate.class);
                intent.putExtra("positionId", positionId);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseRecyclerAdapter<Candidate, CandidateDash.ReportViewHolder> Adapter = new FirebaseRecyclerAdapter<Candidate, CandidateDash.ReportViewHolder>(
                Candidate.class,
                R.layout.candidatecardview,
                CandidateDash.ReportViewHolder.class,
                mydatabase


        ) {
            @Override
            protected void populateViewHolder(CandidateDash.ReportViewHolder reportViewHolder, Candidate candidate, int i) {
                final String candidate_id = getRef(i).getKey();
                reportViewHolder.setCandidateName(candidate.getCandidateName());
                reportViewHolder.setCandidateBio(candidate.getCandidateBio());
                Button view = reportViewHolder.mView.findViewById(R.id.vote);
                final TextView textView = reportViewHolder.mView.findViewById(R.id.percentage);

                DatabaseReference votenode = FirebaseDatabase.getInstance().
                        getReference().child("Voting")
                        .child(positionId).child(candidate_id);

                votenode.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChildren()) {

                            textView.setText("No votes have been casted");

                        }
                        textView.setText(String.valueOf(dataSnapshot.getChildrenCount()) + " Votes");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //todo:understand this usage
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        voteCandidate(candidate_id, positionId);

                    }
                });

            }

        };
        candidateList.setAdapter(Adapter);
    }

    private void voteCandidate(final String candidateId, final String PositionId) {
        ///todo:check if the current user id is present in this tree


        final DatabaseReference voterStatus = FirebaseDatabase.getInstance().getReference()
                .child("Voting").child(PositionId).child(candidateId).child(currentuser.getUid());
        voterStatus.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("voteCandidate:1", "Already voted");
                    Log.d("voter:1", currentuser.getUid());
                    showDialogue("you have already voted","Error",R.mipmap.stop);

                } else {
                    //2
                    final DatabaseReference allVotes = FirebaseDatabase.getInstance().getReference()
                            .child("Voting").child(PositionId);
                    allVotes.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    final String candidateKey = dataSnapshot1.getKey();
                                    Log.d("candidateKey", (String.valueOf(candidateKey)));

                                    final DatabaseReference candidateVotes = FirebaseDatabase.getInstance().getReference()
                                            .child("Voting").child(PositionId).child(candidateKey).child(currentuser.getUid());
                                    candidateVotes.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Log.d("voteCandidate:3", "Already voted");
                                                Log.d("voter:3", currentuser.getUid());
                                                showDialogue("you can only vote once","Stop",R.mipmap.stop);
                                            } else {
                                                Log.d("voteCandidate:2", "Not voted");
                                                Log.d("voter:2", currentuser.getUid());
                                                voterStatus.child("id").setValue(currentuser.getUid());
                                                showDialogue("success", "Success",R.mipmap.thumbs_up_2);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                            } else {
                                Log.d("voteCandidate:4", "Position not available");
                                Log.d("voter:4", currentuser.getUid());
                                voterStatus.child("id").setValue(currentuser.getUid());
                                showDialogue("success", "Success",R.mipmap.thumbs_up_2);
                                //todo;prompt alert position is not available
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

//                    voterStatus.child("id").setValue(currentuser.getUid());
//                    showDialogue("success","Success");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        final DatabaseReference newvote = FirebaseDatabase.getInstance().getReference()
//                .child("Voting").child(PositionId).child(candidateId);
//        DatabaseReference votenode =FirebaseDatabase.getInstance().getReference()
//                .child("Voting").child(PositionId).child(candidateId).child(currentuser.getUid());
//
//        votenode.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//
//                    Log.d("vote_status","It exists");
//                    showDialogue("you have already voted","Error");
//                }
//                else{
//                    Log.d("vote_status","Doesn't exist");
//                    newvote.child(currentuser.getUid()).child("id").setValue(currentuser.getUid());
//                    showDialogue("success","Success");
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        //final DatabaseReference voter = votenode.push();
//        Map<String, VotingObject> voter = new HashMap<>();
//        voter.put(currentuser.getUid(), new VotingObject(currentuser.getUid()));
//        votenode.setValue(voter);

//        votenode.child(currentuser.getUid()).child("id").setValue(currentuser.getUid());

//        voter.child("voterId").setValue(currentuser.getUid());


//
//        final Dialog dialog = new Dialog(CandidateDash.this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        R.layout.success_dialog
//        dialog.setContentView(getResources().getString(R.string.thank_you));
//        Button confirm = dialog.findViewById(R.id.confirm);
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();


    }


    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCandidateName(String candidateName) {
            TextView candidateName1 = (TextView) mView.findViewById(R.id.candidateName);
            candidateName1.setText(candidateName);
        }

        public void setCandidateBio(String bio) {
            TextView candidateBio = (TextView) mView.findViewById(R.id.candidateBio);
            candidateBio.setText(bio);
        }
    }

    public void showDialogue(String message, String title,int id) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
//        alertDialog.setMessage(getResources().getString(R.string.thank_you));
        alertDialog.setIcon(id);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }
}
