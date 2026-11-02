package com.model;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests for constructing a {@link Credential}.
 *
 * +------------------------------------------------+--------------------------------------------------------------------------------------------------+
 * | Test                                           | Reasoning                                                                                        |
 * +------------------------------------------------+--------------------------------------------------------------------------------------------------+
 * | creatingValidCredentialKeepsTypeAndStatus      | Creating a credential should retain the supplied certification type and status.                  |
 * | creatingExpiredCredentialKeepsExpiredStatus    | An expired credential must keep its actual status instead of being recorded as valid.            |
 * +------------------------------------------------+--------------------------------------------------------------------------------------------------+
 */
public class CredentialTest {

    /**
     * Tests that the constructor keeps a valid credential's type and status.
     */
    @Test
    public void creatingValidCredentialKeepsTypeAndStatus() {
        Credential credential = new Credential(CredentialType.EMT, "Valid");
        assertEquals(CredentialType.EMT, credential.getType());
        assertEquals("Valid", credential.getStatus());
    }

    /**
     * Tests that creating an expired credential preserves its expired status.
     */
    @Test
    public void creatingExpiredCredentialKeepsExpiredStatus() {
        Credential credential = new Credential(CredentialType.NURSE, "Expired");
        assertEquals("Expired", credential.getStatus());
    }
}
