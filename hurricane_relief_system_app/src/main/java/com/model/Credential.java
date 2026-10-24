package com.model;

/**
 * Represents a credential that a volunteer has, like a medical license.
 */
public class Credential {

    private CredentialType credentialType;
    private String status;

    /**
     * Constructor for a credential.
     * @param credentialType Type of credential
     * @param status Status of the credential, like Valid
     */
    public Credential(CredentialType credentialType, String status) {
        this.credentialType = credentialType;
        this.status = status;
    }

    /**
     * Gets the type of credential.
     * @return Type of credential
     */
    public CredentialType getType() {
        return credentialType;
    }

    /**
     * Gets the status of the credential.
     * @return Status of the credential
     */
    public String getStatus() {
        return status;
    }
}