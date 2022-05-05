package com.example.datapirates;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class averageReviewRatingTest {

    @Test
    public void averageRatingTest(){
        float result = reviewShow.calcAvgRate(8, 2);
        assertEquals(4, result, 0);
    }
}
