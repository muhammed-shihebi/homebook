package com.mabem.homebook;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

// These tests are run on the local computer, so no emulator required.

/**
 * Characteristics of a good unit test
 *     Fast. It is not uncommon for mature projects to have thousands of unit tests. ...
 *     Isolated. Unit tests are standalone, can be run in isolation, and have no dependencies on any outside factors such as a file system or database.
 *     Repeatable. ...
 *     Self-Checking. ...
 *     Timely.
 */

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}