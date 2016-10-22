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
        int now = Util.now();
        assertThat(cared.calculateColorCode(0, 2, 4), is(Cared.UNKNOWN));
        assertThat(cared.calculateColorCode(now, 2, 4), is(Cared.GREEN));
        assertThat(cared.calculateColorCode(now - 3, 2, 4), is(Cared.YELLOW));
        assertThat(cared.calculateColorCode(now - 5, 2, 4), is(Cared.RED));
    }
}
