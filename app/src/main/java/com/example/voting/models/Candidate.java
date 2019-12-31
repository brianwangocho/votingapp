package com.example.voting.models;

public class Candidate {
    public String candidateName;
    public String candidateBio;

    public Candidate(){}

    public Candidate(String candidateName, String candidateBio) {
        this.candidateName = candidateName;
        this.candidateBio = candidateBio;
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
}
