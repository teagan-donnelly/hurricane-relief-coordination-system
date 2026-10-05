package com.model;

public class Credential {

    private CredentialType credentialType;
    private String status;

    public Credential(CredentialType credentialType, String status) {
        this.credentialType = credentialType;
        this.status = status;
    }
}