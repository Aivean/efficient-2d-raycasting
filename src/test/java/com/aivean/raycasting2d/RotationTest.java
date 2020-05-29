package com.aivean.raycasting2d;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RotationTest {


    int[][] arg = new int[][]{
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8}
    };

    int size = arg.length;

    private void assertRotation(Rotation rot, int[][] eta) {
        int[][] res = new int[size][size];

        rot.rotate(res, arg);
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(res[i], eta[i]);
        }
    }

    @Test
    public void testRotateCCW() {
        int[][] eta = new int[][]{
                {2, 5, 8},
                {1, 4, 7},
                {0, 3, 6}
        };

        assertRotation(Rotation.CCW, eta);
    }

    @Test
    public void testRotateCW() {
        int[][] eta = new int[][]{
                {6, 3, 0},
                {7, 4, 1},
                {8, 5, 2}
        };

        assertRotation(Rotation.CW, eta);
    }

    @Test
    public void testRotatePI() {
        int[][] eta = new int[][]{
                {8, 7, 6},
                {5, 4, 3},
                {2, 1, 0}
        };

        assertRotation(Rotation.PI, eta);
    }

}