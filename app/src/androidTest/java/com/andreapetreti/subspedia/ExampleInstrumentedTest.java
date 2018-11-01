package com.andreapetreti.subspedia;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import androidx.work.test.TestDriver;
import androidx.work.test.WorkManagerTestInitHelper;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void test() {
        WorkManagerTestInitHelper.initializeTestWorkManager(Instrumentation.);
        TestDriver testDriver = WorkManagerTestInitHelper.getTestDriver();
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.andreapetreti.subspedia", appContext.getPackageName());
    }
}
