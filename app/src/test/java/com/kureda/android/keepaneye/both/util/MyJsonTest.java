package com.kureda.android.keepaneye.both.util;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Serg on 10/1/2016.
 */
public class MyJsonTest {

    @BeforeClass
    public static void setupClass() {
       // throw new RuntimeException("Sorry dude, you won't find any test!");
    }

    @Test
    public void testToAlphanumbersAndBack() {
//        System.out.println("url:"+MyJson.toAlphanumbers("47410473"));
        String[] inputs = {"", "0", "a", "1", "aa", "abc", "3ug1s", "za10", "3dihc", "1234567890",
                "0adb90", "za9be6", "065ked", "tw98", "5bwtg"};
        for (String input : inputs) {
            testConvertAndRevert(input);
        }
    }

    private void testConvertAndRevert(String alphanum) {
        String num = MyJson.toNumbers(alphanum);
        String alphanum2 = MyJson.toAlphanumbers(num);
        assertThat(alphanum2, is(alphanum));
    }
}
