package com.example.android.sunshine;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
/**
 * Created by Apoorva on 6/16/2017.
 */

public class FullTestSuite {
    public static Test suite()
    {
        return new TestSuiteBuilder(FullTestSuite.class
        ).includeAllPackagesUnderHere().build();

    }
    public FullTestSuite(){
        super();
    }
}
