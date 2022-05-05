package com.example.datapirates;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class calcReadingLevelTest {
    @Test
    public void testGrade() {
        String result = calcReadingLevel.calculateGrade("One fish. Two fish. Red fish. Blue fish.");
        assertEquals("Before Grade 1", result);

        result = calcReadingLevel.calculateGrade("Harry Potter was a highly unusual boy in many ways. For one thing, he hated the summer holidays more than any other time of year. For another, he really wanted to do his homework, but was forced to do it in secret, in the dead of the night. And he also happened to be a wizard.");
        assertEquals("Grade 5", result);

        result = calcReadingLevel.calculateGrade("In my younger and more vulnerable years my father gave me some advice that I've been turning over in my mind ever since.");
        assertEquals("Grade 7", result);

        result = calcReadingLevel.calculateGrade("It was a bright cold day in April, and the clocks were striking thirteen. Winston Smith, his chin nuzzled into his breast in an effort to escape the vile wind, slipped quickly through the glass doors of Victory Mansions, though not quickly enough to prevent a swirl of gritty dust from entering along with him.");
        assertEquals("Grade 10", result);

        result = calcReadingLevel.calculateGrade("A large class of computational problems involve the determination of properties of graphs, digraphs, integers, arrays of integers, finite families of finite sets, boolean formulas and elements of other countable domains.");
        assertEquals("Grade 16+", result);
    }
}
