package com.example.datapirates;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class progressPercentageTest {

    @Test
    public void calcPercentageTest(){
        int result = progressShow.calcPercentage(100, 200);
        assertEquals(50, result);

        result = progressShow.calcPercentage(10, 255);
        assertEquals(4, result);
    }

}
