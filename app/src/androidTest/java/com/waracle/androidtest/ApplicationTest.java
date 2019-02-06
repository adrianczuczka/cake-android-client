package com.waracle.androidtest;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

/*
If I was allowed to use third-party libraries, I would use Robolectric and Espresso for better
testing capability. I would also construct the app with TDD in mind rather than writing tests after.
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
}