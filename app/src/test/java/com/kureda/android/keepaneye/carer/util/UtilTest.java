package com.kureda.android.keepaneye.carer.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Serg on 10/18/2016.
 */

public class UtilTest {

    @Test
    public void testHoursToIndex() {
        assertThat(Util.hoursToIndex(-1), is(0));
        assertThat(Util.hoursToIndex(0), is(0));
        assertThat(Util.hoursToIndex(1), is(0));
        assertThat(Util.hoursToIndex(2), is(1));
        assertThat(Util.hoursToIndex(9), is(8));
        assertThat(Util.hoursToIndex(23), is(22));
        assertThat(Util.hoursToIndex(24), is(23));
        assertThat(Util.hoursToIndex(25), is(23));
        assertThat(Util.hoursToIndex(10000), is(23));
    }

    @Test
    public void testIndexToHours() {
        assertThat(Util.indexToHours(-1), is(1));
        assertThat(Util.indexToHours(0), is(1));
        assertThat(Util.indexToHours(1), is(2));
        assertThat(Util.indexToHours(2), is(3));
        assertThat(Util.indexToHours(9), is(10));
        assertThat(Util.indexToHours(23), is(24));
        assertThat(Util.indexToHours(24), is(24));
        assertThat(Util.indexToHours(25), is(24));
        assertThat(Util.indexToHours(10000), is(24));
    }

    @Test
    public void testIndexToHoursAndBack() {
        assertThat(Util.hoursToIndex(Util.indexToHours(0)), is(0));
        assertThat(Util.hoursToIndex(Util.indexToHours(1)), is(1));
        assertThat(Util.hoursToIndex(Util.indexToHours(23)), is(23));
    }


    @Test
    public void testIndexToIntervals() {
        assertThat(Util.indexToInterval(-1), is(60));
        assertThat(Util.indexToInterval(0), is(60));
        assertThat(Util.indexToInterval(8), is(60 * 24));
        assertThat(Util.indexToInterval(17), is(60 * 24 * 30));
        assertThat(Util.indexToInterval(18), is(Util.THOUSAND_YEARS));
    }

    @Test
    public void testIntervalsToIndex() {
        assertThat(Util.intervalToIndex(Util.THOUSAND_YEARS), is(18));
        assertThat(Util.intervalToIndex(60), is(0));
        assertThat(Util.intervalToIndex(120), is(1));
    }

    @Test
    public void testIndexToIntervalsAndBack() {
        assertThat(Util.intervalToIndex(Util.indexToInterval(18)), is(18));
        assertThat(Util.intervalToIndex(Util.indexToInterval(17)), is(17));
        assertThat(Util.intervalToIndex(Util.indexToInterval(1)), is(1));
        assertThat(Util.intervalToIndex(Util.indexToInterval(5)), is(5));
        assertThat(Util.intervalToIndex(Util.indexToInterval(0)), is(0));
    }
}
