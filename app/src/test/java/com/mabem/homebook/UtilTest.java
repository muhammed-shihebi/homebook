package com.mabem.homebook;

import com.mabem.homebook.Model.Home;
import com.mabem.homebook.Utils.Util;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.*;



/**
 * Characteristics of a good unit test
 *     Fast. It is not uncommon for mature projects to have thousands of unit tests. ...
 *     Isolated. Unit tests are standalone, can be run in isolation, and have no dependencies on any outside factors such as a file system or database.
 *     Repeatable. ...
 *     Self-Checking. ...
 *     Timely.
 */

public class UtilTest {
    @Test
    public void valueWithThreeDecimalPlacesRoundedAsExpected_returnsTrue() {
        Double value = 10.232;
        int places = 2;
        Double expected = 10.23;
        assertThat(Util.round(value, places)).isEqualTo(expected);

    }



}