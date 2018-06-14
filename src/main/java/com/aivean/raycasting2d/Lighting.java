/*
The MIT License (MIT)

Copyright (c) 2018 Ivan Zaitsev (https://github.com/Aivean/efficient-2d-raycasting)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.aivean.raycasting2d;

import java.util.Arrays;

/**
 * Approximate lighting calculation algorithm for 2d discrete tilemaps.
 * <p>
 * If N is the dimension of the field (while field is NxN) then:
 * Best-case complexity is O(N^2)  (for the constant number of light sources)
 * Worst-case complexity is O(N^3)  (for N..N^2 light sources)
 * <p>
 * Usage:
 * <p>
 * * set input via {@link #setInputRotated(int[][], Rotation)}
 * When Rotation.NO is provided, if input is in row-first([y,x]) order than light will be cast top-to-bottom,
 * if input is in column-first ([x,y]) order, than light is cast left-to-right.
 * <p>
 * Read description here:
 * https://github.com/CleverRaven/Cataclysm-DDA/issues/23996#issue-331403618
 */
public class Lighting {

    public static final int EMPTY = 0;
    public static final int LIGHT_SOURCE = 1;
    public static final int OBSTACLE = 2;

    /**
     * N — field size (field is NxN tiles)
     */
    final int fieldSize;

    /**
     * Resulting brightness of the tiles, bigger is brighter
     * 0 - no illumination
     */
    private final float[][] brightness;

    // these three arrays are the buffers for existing and newly added beams
    // important invariant: beams in the buffers are always ordered by their `b` field
    private Beam[] currentBeamsBuffer;
    private Beam[] nextBeamsBuffer;
    private Beam[] newlyAddedBeamsBuffer;

    private int newlyAddedBeamsBufferN;

    /*
     * Number of beams to cast
     * */
    private final int beamsN;

    /**
     *
     */
    private final boolean lightPresent[];

    /**
     * Input field (fieldSize x fieldSize)
     */
    private final int[][] input;

    public Lighting(int size) {
        this.fieldSize = size;
        this.beamsN = size * 2;

        // arrays are pre-allocated
        // to avoid creating garbage files of the Beams
        // in the arrays are modified directly
        input = new int[size][size];
        brightness = new float[size][size];
        currentBeamsBuffer = new Beam[size * 3];
        nextBeamsBuffer = new Beam[size * 3];
        newlyAddedBeamsBuffer = new Beam[size];

        initTArr(currentBeamsBuffer);
        initTArr(nextBeamsBuffer);
        initTArr(newlyAddedBeamsBuffer);

        lightPresent = new boolean[size];
    }

    private void initTArr(Beam[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new Beam(0, 0);
        }
    }

    /**
     * Copies input data from input, applying the given rotation.
     * <p>
     * Input is one of the:
     * * {@link #EMPTY}
     * * {@link #LIGHT_SOURCE}
     * * {@link #OBSTACLE}
     *
     * @param input
     * @param rotation
     */
    public void setInputRotated(int[][] input, Rotation rotation) {
        rotation.rotateInt(this.input, input);
    }

    public void accumulateLightRotatated(float[][] dst, Rotation rotation) {
        rotation.rotateAndAddFloat(dst, brightness);
    }

    public void recalculateLighting(final float startIntensity) {
        int x, y;

        /* cleaning lighting */
        for (x = 0; x < fieldSize; x++) {
            Arrays.fill(brightness[x], 0);
        }

        boolean firstPass = true;
        Arrays.fill(lightPresent, false);

        for (int a = -beamsN / 2; a < beamsN / 2; a++) { // slope
            int currentBeamsBufferN = 0; // n of intervals

            /* angle of the currently processed beams */
            double alpha = Math.PI / 4 * (2.0 * a / beamsN);

            /*       l
                 /--------/
                /| beam  /td
              b/-|------/
               slp

               If beam is a parallelogram with height=1, angle `alpha`, and base `l`,
               then `slp` is "left base", i.e. distance between the vertex and height projected on base,
               and `td` is the length of the sides of the parallelogram
            */
            float slp = (float) Math.tan(alpha);
            float td = (float) Math.sqrt(slp * slp + 1);

            /* "y" coordinate of the current row, assuming input is in row-first order, i.e. [y,x] */
            for (y = 0; y < fieldSize; y++) {
                int[] inputRow = input[y];
                float[] brightnessRow = brightness[y];

                /*
                 * Step 1
                 * move beams horizontally by slp, remove beams that are outside the field
                 * */
                for (int i = 0; i < currentBeamsBufferN; i++) {
                    Beam t = currentBeamsBuffer[i];
                    t.b += slp;

                    if (t.b < 0 || t.b + 1 >= fieldSize || t.i <= 0.0000001) {
                        t.d = true;
                    }
                }

                newlyAddedBeamsBufferN = 0; // clear newlyAddedBeamsBuffer

                /*
                 * Step 2 and 3
                 *
                 * Add beams intensity to the corresponding tiles of the current row
                 * (see {@link #applyLight(Beam, int, int, float)})
                 *
                 * If beam intersects with the obstacle, cut the beam accordingly.
                 * */
                for (int i = 0; i < currentBeamsBufferN; i++) {
                    Beam t = currentBeamsBuffer[i];
                    if (t.d) continue;
                    int fb = (int) (t.b);
                    int fe = fb + 1;
                    brightnessRow[fb] += applyLight(t, fb, y, td);
                    brightnessRow[fe] += applyLight(t, fe, y, td);

                    if (inputRow[fb] == OBSTACLE) {
                        cutBeam(t, fb);
                    }
                    if (inputRow[fe] == OBSTACLE) {
                        cutBeam(t, fe);
                    }
                }

                /*
                 * Step 4
                 *
                 * If current row contains any light sources, add them as new beams
                 * to the #newlyAddedBeamsBuffer
                 *
                 * Several optimizations here:
                 * 1. `lightPresent[y]` may be precomputed for this row from the previous passes.
                 *         It allows us to skip the pass entirely.
                 * 2. `y < fieldSize / 2 || a % 2 == 0` for the bottom part of the fields we can
                 *      cast only half of the beams without noticeable lose in precision
                 *      (but intensity of each beam should be doubled to compensate)
                 * */
                if ((firstPass || lightPresent[y]) && (y < fieldSize / 2 || a % 2 == 0)) {
                    for (x = 0; x < fieldSize; x++) {
                        if (inputRow[x] == LIGHT_SOURCE) {
                            brightnessRow[x] += startIntensity;
                            lightPresent[y] = true;
                            newlyAddedBeamsBuffer[newlyAddedBeamsBufferN++]
                                    .set(x, (y < fieldSize / 2 ? startIntensity : startIntensity * 2));
                        }
                    }
                }

                Beam[] tmpBuffer = currentBeamsBuffer; // swap currentBeamsBuffer and nextBeamsBuffer
                currentBeamsBuffer = nextBeamsBuffer;
                nextBeamsBuffer = tmpBuffer;

                currentBeamsBufferN = merge(currentBeamsBufferN);
            }
            firstPass = false;
        }
    }

    /**
     * Add Beam's brightness to given tile
     *
     * @param t  current beam
     * @param x  coordinate of the tile
     * @param y  coordinate of the tile
     * @param td length of the "side" of the beam
     * @return amount of light to add
     */
    private float applyLight(Beam t, int x, int y, float td) {
        /* calculating the intersection area between current beam and tile */
        return max(min(t.b + t.l, x + 1) - max(t.b, x), 0) * t.i * td;
    }

    /**
     * Cuts the `b` and `l` of the beam
     *
     * @param t beam to cut
     * @param x coordinate of the obstacle
     */
    private void cutBeam(Beam t, int x) {
        float e = t.b + t.l;
        if (e <= x + 1 && e > x) {
            t.l = x - t.b;
        }
        if (t.b >= x && t.b < x + 1) {
            float b = x + 1;
            t.l -= b - t.b;
            t.b = b;
        }
        if (t.l <= 0) {
            t.d = true;
        }
    }

    /**
     * Merges the beams from {@link #nextBeamsBuffer} and {@link #newlyAddedBeamsBuffer}
     * into the {@link #currentBeamsBuffer}, returning the size of the resulting buffer.
     * <p>
     * As all buffers are ordered by their `b` field, merge can be done in O(N),
     * where N is the number of beams.
     * <p>
     * Also, as the number of beams is bounded when following invariants are preserved:
     * <ol>
     * <li> width of all beams is  within (0..1] interval</li>
     * <li> if the combined width of two beams and the gap between them is less than 1 they are merged into one</li>
     * <li> intersecting beams are rearranged so that they satisfy items above</li>
     * </ol>
     * Invariants listed above guarantee that the number of beams is bounded at every time.
     * Depending on concrete implementation of the merge rules there cannot be more than 2N beams.
     *
     * @param nextBeamsBufferN size of the nextBeamsBuffer
     * @return size of the resulting {@link #currentBeamsBuffer}
     */
    private int merge(int nextBeamsBufferN) {
        int i = 0;
        int j = 0;

        /* size of the `currentBeamsBuffer` as it is being filled */
        int n = 0;

        j = 0;
        Beam tj;

        /*
         * Takes the next beam with the smallest `b` from either `nextBeamsBuffer` or `newlyAddedBeamsBuffer`
         * and `merges` it with the last beam in the `currentBeamsBuffer`.
         */
        for (i = 0; i < nextBeamsBufferN; i++) {
            Beam ti = nextBeamsBuffer[i];
            if (ti.d) continue;
            while (j < newlyAddedBeamsBufferN && (tj = newlyAddedBeamsBuffer[j]).b <= ti.b) {
                n = merge1(tj, n);
                j++;
            }
            n = merge1(nextBeamsBuffer[i], n);
        }

        while (j < newlyAddedBeamsBufferN) {
            n = merge1(newlyAddedBeamsBuffer[j], n);
            j++;
        }
        return n;
    }

    /**
     * Implements merge rules for two beams.
     * Adds the result of the merge to {@link #currentBeamsBuffer} and
     * returns new size of the currentBeamsBuffer
     *
     * @param t beam to merge into currentBeamsBuffer
     * @param n current size of the currentBeamsBuffer
     * @return new size of the currentBeamsBuffer
     */
    private int merge1(Beam t, int n) {
        if (n > 0) {
            Beam t2 = currentBeamsBuffer[n - 1];
            float b = min(t.b, t2.b);
            float e = max(t.b + t.l, t2.b + t2.l);
            float l = e - b;
            if (l <= 1) {
                t2.b = b;
                t2.l = l;
                t2.i = (t.i * t.l + t2.i * t2.l) / l;
                return n;
            } else if (l < 2 && t.b - t2.b - t2.l <= 0.5) {
                float l1 = l / 2;
                float i = (t2.i * t2.l + t.i * t.l) / l;
                t2.b = b;
                t2.l = l1;
                t2.i = i;

                t.b = t2.b + l1;
                t.l = l1;
                t.i = i;
            }
        }
        currentBeamsBuffer[n++].setFrom(t);
        return n;
    }

    /**
     * Data class representing single Beam
     */
    static class Beam {
        /**
         * (beginning) start coordinate of the beam
         */
        float b;

        /**
         * (intensity) brightness of the beam
         */
        float i;

        /*
         * (length) width of the beam
         * important invariant:
         *  0 < `l` <= 1
         * */
        float l;

        /**
         * (deleted) whether beam is marked for deletion
         */
        boolean d;

        Beam(float b, float i) {
            this.b = b;
            this.l = 1;
            this.i = i;
        }

        void set(float b, float i) {
            this.b = b;
            this.l = 1;
            this.i = i;
            this.d = false;
        }

        void setFrom(Beam other) {
            this.b = other.b;
            this.i = other.i;
            this.l = other.l;
            this.d = other.d;
        }
    }

    public float getLight(int y, int x) {
        return brightness[y][x];
    }

    private static float max(float a, float b) {
        return a > b ? a : b;
    }

    private static float min(float a, float b) {
        return a < b ? a : b;
    }
}
