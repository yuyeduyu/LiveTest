package com.ascend.wangfeng.locationbyhand;

import com.ascend.wangfeng.locationbyhand.util.RegularExprssion;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void MacFormat() {
        String mac = "123456654321";
        System.out.print(RegularExprssion.macFormat(mac));
    }
    @Test
    public void test(){
        ArrayList<String> a= new ArrayList<>();
        a.add("a");
        a.add("a");
        a.add("a");
        a.set(1,"s");
        System.out.print(a.toString());
    }
}