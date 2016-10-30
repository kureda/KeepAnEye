package com.kureda.android.keepaneye.carer.ui;

import com.kureda.android.keepaneye.both.util.Util;
import com.kureda.android.keepaneye.carer.util.Cared;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Serg on 10/11/2016.
 */

public class CaredTest {

    @Test
    public void testCalculateColor() {
        Cared cared = new Cared(null);
        cared.calculateColorCodes();//to set mNow
        int now = Util.now();
        assertThat(cared.calculateColorCode(0, 2, 4), is(Cared.UNKNOWN));
        assertThat(cared.calculateColorCode(now, 2, 4), is(Cared.GREEN));
        assertThat(cared.calculateColorCode(now - 3, 2, 4), is(Cared.YELLOW));
        assertThat(cared.calculateColorCode(now - 5, 2, 4), is(Cared.RED));
    }

    @Test
    public void testIsSleeping() {

        Cared cared = new Cared(null);
        test(1, 2, 3, false);
        test(1, 2, 1, true);
        test(22, 2, 23, true);
        test(22, 2, 20, false);
        test(22, 6, 5, true);
        test(22, 6, 21, false);
        test(6, 22, 10, true);
        test(6, 22, 24, false);
        test(22, 6, 24, true);
        test(22, 6, 1, true);
        test(22, 6, 7, false);
        test(11, 11, 12, false);
        test(11, 11, 10, false);
        test(11, 10, 11, true);
        test(12, 10, 11, false);
        test(11, 10, 7, true);
    }

    private void test(int sleep, int wake, int now, boolean result) {
        int nowInMinutes = now * 60;
        Cared cared = new Cared(null);
        cared.wake = wake;
        cared.sleep = sleep;
        assertThat(cared.isSleeping(nowInMinutes), is(result));
    }
}
