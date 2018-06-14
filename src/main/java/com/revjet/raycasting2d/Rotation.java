package com.revjet.raycasting2d;

/**
 * @author <a href="mailto:ivan.zaytsev@webamg.com">Ivan Zaytsev</a>
 * 2018-06-11
 */
public class Rotation {

    static void rotateCCW(int[][] a, int[][] res) {
        int size = a.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                res[size - j - 1][i] = a[i][j];
            }
        }
    }

    static void rotateCW(int a[][], int res[][]) {
        int size = a.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                res[j][size - i - 1] = a[i][j];
            }
        }
    }

    static void rotate180(int a[][], int res[][]) {
        int size = a.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                res[size - i - 1][size - j - 1] = a[i][j];
            }
        }
    }

    static void rotateCWAndAdd(float[][] dst, float[][] src) {
        int size = dst.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                dst[i][j] += src[size - j - 1][i];
            }
        }
    }

    static void rotateCCWAndAdd(float dst[][], float src[][]) {
        int size = dst.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                dst[i][j] += src[j][size - i - 1];
            }
        }
    }

    static void rotate180AndAdd(float dst[][], float src[][]) {
        int size = dst.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                dst[i][j] += src[size - i - 1][size - j - 1];
            }
        }
    }

}
