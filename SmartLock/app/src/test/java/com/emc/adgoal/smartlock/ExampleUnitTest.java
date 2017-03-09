package com.emc.adgoal.smartlock;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void parser() throws Exception {
        new RestClient("http://google.com:80/").doGet(null, null);
    }
}