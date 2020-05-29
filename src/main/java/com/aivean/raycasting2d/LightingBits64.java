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


public class LightingBits64 {

    public static final int EMPTY = 0;
    public static final int LIGHT_SOURCE = 1;
    public static final int OBSTACLE = 2;

    /**
     * N — field size (field is NxN tiles)
     */
    final int fieldSize;

    /**
     * factor by which output is down scaled
     * compared to input.
     * Must be a power of two between 1 and 64
     */
    final int outDownScale;
    final long outDownScaleMask;

    /**
     * Resulting brightness of the tiles, bigger is brighter
     * 0 - no illumination
     */
    private final int[][] brightness;


    private final long beam[][];

    /*
     * Number of beams to cast
     * */
    private final int beamsN;


    /**
     * Input field (fieldSize x fieldSize)
     */
    private final int[][] input;


    /**
     * Light source bits (fieldSize x fieldSize/64)
     */
    private final long[][] inputLSBits;

    /**
     * Obstacle Bits (fieldSize x fieldSize/64)
     */
    private final long[][] inputObsBits;


    public LightingBits64(int size, int outDownScale) {
        if (outDownScale > 64 || outDownScale >>> Integer.numberOfTrailingZeros(outDownScale) != 1) {
            throw new IllegalArgumentException("outDownScale must be a power of two between 1 and 64");
        }
        this.outDownScale = outDownScale;

        long outDownScaleMask = 0;
        while (outDownScale > 0) {
            outDownScaleMask = (outDownScaleMask << 1) | 1;
            outDownScale--;
        }
        this.outDownScaleMask = outDownScaleMask;

        this.fieldSize = size;
        this.beamsN = size /** 2*/;


        // arrays are pre-allocated
        // to avoid creating garbage files of the Beams
        // in the arrays are modified directly
        input = new int[size][size];


        int size64 = size / 64 + (size % 64 > 0 ? 1 : 0);
        this.inputLSBits = new long[size][size64];
        this.inputObsBits = new long[size][size64];
        this.beam = new long[4][size64];

        brightness = new int[size / this.outDownScale][size / this.outDownScale];
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
        rotation.rotate(this.input, input);

        for (int y = 0; y < fieldSize; y++) {
            Arrays.fill(this.inputLSBits[y], 0L);
            Arrays.fill(this.inputObsBits[y], ~0L);

            for (int x = 0; x < fieldSize; x++) {
                int ix = x / 64;
                long x64 = 1L << (x % 64);

                if (this.input[y][x] == OBSTACLE) {
                    this.inputObsBits[y][ix] &= ~x64;
                } else if (this.input[y][x] == LIGHT_SOURCE) {
                    this.inputLSBits[y][ix] |= x64;
                }
            }
        }
    }


    public void accumulateLightRotatated(int[][] dst, Rotation rotation) {
        rotation.rotateAndAdd(dst, brightness);
    }

    public void recalculateLighting(final float startIntensity) {
        int x, y;

        /* cleaning lighting */
        for (x = 0; x < brightness.length; x++) {
            Arrays.fill(brightness[x], 0);
        }

        for (int a = -beamsN / 2; a < beamsN / 2; a++) { // slope
            for (int i = 0; i < 4; i++) {
                Arrays.fill(beam[i], 0);
            }

            /* angle of the currently processed beams */
            double alpha = Math.PI / 4 * (2.0 * a / beamsN);

            /*       l
                 /--------/
                /| beam  /
              b/-|------/
               slp

               If beam is a parallelogram with height=1, angle `alpha`, and base `l`,
               then `slp` is "left base", i.e. distance between the vertex and height projected on base,
            */
            float slp = (float) Math.tan(alpha);
            int intSLP = Math.round(slp * fieldSize);
            int curSLP = 0;

            /* "y" coordinate of the current row, assuming input is in row-first order, i.e. [y,x] */
            for (y = 0; y < fieldSize; y++) {
//                int[] inputRow = input[y];

                long[] inputLSRow = this.inputLSBits[y];
                long[] inputObsRow = this.inputObsBits[y];
                int[] brightnessRow = brightness[y / outDownScale];

                if (curSLP <= -fieldSize) {
                    curSLP += fieldSize;
                    // move beams left
                    for (int i = 0; i < 4; i++) {
                        long[] beam = this.beam[i];
                        for (int j = 0; j < beam.length - 1; j++) {
                            beam[j] = (beam[j] >>> 1) | ((beam[j + 1] & 1) << 63);
                        }
                        beam[beam.length - 1] >>>= 1;
                    }
                } else if (curSLP >= fieldSize) {
                    curSLP -= fieldSize;
                    /* move beams right */
                    for (int i = 0; i < 4; i++) {
                        long[] beam = this.beam[i];
                        long carry = 0;
                        for (int j = 0; j < beam.length; j++) {
                            long tmpCarry = beam[j] >>> 63;
                            beam[j] = (beam[j] << 1) | carry;
                            carry = tmpCarry;
                        }
                    }
                }
                curSLP += intSLP;

                for (int i = 0; i < 1; i++) {
                    long[] beam = this.beam[i];
                    for (int j = 0; j < beam.length; j++) {
                        beam[j] |= inputLSRow[j];

                        long bj = beam[j];
                        if (bj != 0) {
                            int k = j * 64 / outDownScale;
                            while (bj != 0 && k < brightnessRow.length) {
                                brightnessRow[k] += Long.bitCount(bj & outDownScaleMask) << i;
                                bj >>>= outDownScale;
                                k++;
                            }
                        }

                        beam[j] &= inputObsRow[j];
                    }
                }
            }
        }
    }
}
