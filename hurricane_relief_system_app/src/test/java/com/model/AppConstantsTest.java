package com.model;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import java.util.concurrent.CompletableFuture;

/**
 * Tests for {@link AppConstants}.
 * @author APR
 *
 * +------------------------------------------------+-------------------------------------------------------------------+
 * | Test                                           | Reasoning                                                         |
 * +------------------------------------------------+-------------------------------------------------------------------+
 * | isJunitTestTest_returnTrue                     | A method to return a boolean test to make it return true          |
 * | isJunitTestTest_returnFalse                    | A method to return a boolean test to make it return true          |
 * +------------------------------------------------+-------------------------------------------------------------------+
 */

/**
 * A class for testing the methods of class AppConstants
 * @author APR
 */
public class AppConstantsTest {

    /**
     * Test for return true of method isJunitTest
     */
    @Test 
    public void isJunitTestTest_returnTrue() {
        assertTrue(AppConstants.isJUnitTest());
    }

    /**
     * Test for return flase of method isJunitTest
     */
    @Test 
    public void isJunitTestTest_returnFalse() {
        boolean result = CompletableFuture.supplyAsync(AppConstants::isJUnitTest).join();

        assertFalse(result);
    }
}