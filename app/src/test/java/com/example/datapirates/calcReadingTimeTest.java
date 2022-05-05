package com.example.datapirates;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class calcReadingTimeTest {

    @Test
    public void readingTimeTest(){
        int result [] = calcReadingTime.findReadingTime(2,255,2);
        assertEquals(4, result[0]);
        assertEquals(15, result[1]);

        result = calcReadingTime.findReadingTime(1,200,2);
        assertEquals(6, result[0]);
        assertEquals(40, result[1]);
    }

}
