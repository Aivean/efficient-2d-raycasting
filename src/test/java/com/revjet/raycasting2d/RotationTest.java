package com.revjet.raycasting2d;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:ivan.zaytsev@webamg.com">Ivan Zaytsev</a>
 * 2018-06-11
 */
public class RotationTest {


    int[][] arg = new int[][]{
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8}
    };

    int size = arg.length;

    @Test
    public void testRotateCCW() {
        int[][] eta = new int[][]{
                {2, 5, 8},
                {1, 4, 7},
                {0, 3, 6}
        };

        int[][] res = new int[size][size];

        Rotation.rotateCCW(size, arg, res);
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(res[i], eta[i]);
        }
    }

    @Test
    public void testRotateCW() {
        int[][] eta = new int[][]{
                {6, 3, 0},
                {7, 4, 1},
                {8, 5, 2}
        };

        int[][] res = new int[size][size];

        Rotation.rotateCW(size, arg, res);
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(res[i], eta[i]);
        }
    }

    @Test
    public void testRotate180() {
        int[][] eta = new int[][]{
                {8, 7, 6},
                {5, 4, 3},
                {2, 1, 0}
        };

        int[][] res = new int[size][size];

        Rotation.rotate180(size, arg, res);


        for (int i = 0; i < size; i++) {
            Assert.assertEquals(res[i], eta[i]);
        }
    }

}