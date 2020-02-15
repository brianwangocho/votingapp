package com.example.voting.models;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

public class Candidate {
    public String candidateName;
    public String candidateBio;
    public String imageURL;

    public Candidate(){}

    public Candidate(String name, String trim, Task<Uri> downloadUrl){}

    public Candidate(String candidateName, String candidateBio, String imageURL) {
        this.candidateName = candidateName;
        this.candidateBio = candidateBio;
        this.imageURL = imageURL;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateBio() {
        return candidateBio;
    }

    public void setCandidateBio(String candidateBio) {
        this.candidateBio = candidateBio;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
