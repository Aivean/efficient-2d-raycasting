package com.revjet.raycasting2d;

/**
 * @author <a href="mailto:ivan.zaytsev@webamg.com">Ivan Zaytsev</a>
 * 2018-06-11
 */
public class Rotation {

    static void rotateCCW(int size, int a[][], int res[][]) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                res[size - j - 1][i] = a[i][j];
            }
        }
    }

    static void rotateCW(int size, int a[][], int res[][]) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                res[j][size - i - 1] = a[i][j];
            }
        }
    }

    static void rotate180(int size, int a[][], int res[][]) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                res[size - i - 1][size - j - 1] = a[i][j];
            }
        }
    }
}
