package com.starters.medion;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class DecisionActivityTest {

    @Test
    public void testStartingMainActivity(){
        DecisionActivity decisionActivity = Robolectric.setupActivity(DecisionActivity.class);

    }


}