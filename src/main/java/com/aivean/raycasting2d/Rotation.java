package com.aivean.raycasting2d;

/**
 * Utility class that rotates matrices
 */
public enum Rotation {

    NO {
        @Override
        public void rotate(int[][] dst, int[][] a) {
            int size = a.length;
            for (int i = 0; i < size; i++) {
                System.arraycopy(a[i], 0, dst[i], 0, size);
            }
        }

        @Override
        public void rotateAndAdd(float[][] dst, float[][] src) {
            int size = dst.length;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dst[i][j] += src[i][j];
                }
            }
        }

        @Override
        public void rotateAndAdd(int[][] dst, int[][] src) {
            int size = dst.length;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dst[i][j] += src[i][j];
                }
            }
        }

        @Override
        public Rotation opposite() {
            return NO;
        }
    },
    CCW {
        @Override
        public void rotate(int[][] dst, int[][] a) {
            int size = a.length;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dst[size - j - 1][i] = a[i][j];
                }
            }
        }

        @Override
        public void rotateAndAdd(float[][] dst, float[][] src) {
            int size = dst.length;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dst[i][j] += src[j][size - i - 1];
                }
            }
        }

        @Override
        public void rotateAndAdd(int[][] dst, int[][] src) {
            int size = dst.length;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dst[i][j] += src[j][size - i - 1];
                }
            }
        }

        @Override
        public Rotation opposite() {
            return CW;
        }
    },

    CW {
        @Override
        public void rotate(int[][] dst, int[][] a) {
            int size = a.length;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dst[j][size - i - 1] = a[i][j];
                }
            }
        }

        @Override
        public void rotateAndAdd(float[][] dst, float[][] src) {
            int size = dst.length;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dst[i][j] += src[size - j - 1][i];
                }
            }
        }

        @Override
        public void rotateAndAdd(int[][] dst, int[][] src) {
            int size = dst.length;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dst[i][j] += src[size - j - 1][i];
                }
            }
        }

        @Override
        public Rotation opposite() {
            return CCW;
        }
    },

    PI {
        @Override
        public void rotate(int[][] dst, int[][] a) {
            int size = a.length;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dst[size - i - 1][size - j - 1] = a[i][j];
                }
            }
        }

        @Override
        public void rotateAndAdd(float[][] dst, float[][] src) {
            int size = dst.length;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dst[i][j] += src[size - i - 1][size - j - 1];
                }
            }
        }

        @Override
        public void rotateAndAdd(int[][] dst, int[][] src) {
            int size = dst.length;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    dst[i][j] += src[size - i - 1][size - j - 1];
                }
            }
        }

        @Override
        public Rotation opposite() {
            return PI;
        }
    };

    public abstract void rotate(int[][] dst, int[][] a);

    public abstract void rotateAndAdd(float[][] dst, float[][] src);

    public abstract void rotateAndAdd(int[][] dst, int[][] src);

    public abstract Rotation opposite();

}
