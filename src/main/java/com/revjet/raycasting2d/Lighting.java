package com.revjet.raycasting2d;

import java.util.Arrays;

/**
 * @author <a href="mailto:ivan.zaytsev@webamg.com">Ivan Zaytsev</a>
 * 2018-04-17
 */
public class Lighting {

    final int w, h;
    final double[][] light;
    T[] tmp;
    T[] tmp2;
    T[] tmpNew;

    int tmp2IntN = 0;
    private int tmpNewIntN;


    public Lighting(int w, int h) {
        this.w = w;
        this.h = h;
        light = new double[w][h];
        tmp = new T[w * 3];
        tmp2 = new T[w * 3];
        tmpNew = new T[w];

        initTArr(tmp);
        initTArr(tmp2);
        initTArr(tmpNew);
    }

    private void initTArr(T[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new T(0, 0);
        }
    }

    void recalculateLighting(int[][] objects, double startIntensity) {
        int x, y;

        /* cleaning lighting */
        for (x = 0; x < w; x++) {
            Arrays.fill(light[x], 0);
        }

        int raysN = w * 2;
        for (int a = -raysN / 2; a < raysN / 2; a++) { // slope
            int tmpN = 0; // n of intervals
            tmp2IntN = 0; // clear tmp2
            tmpNewIntN = 0; // clear tmpNew

            double alpha = Math.PI / 4 * (2.0 * a / raysN);
            double slp = Math.tan(alpha);
            double td = Math.sqrt(slp * slp + 1);

            for (y = 0; y < h; y++) {
                tmpN = merge();
                int[] objectsY = objects[y];
                double[] lightY = light[y];

                for (int i = 0; i < tmpN; i++) {
                    T t = tmp[i];
                    t.b += slp;

                    if (t.b < 0 || t.b + 1 >= w || t.i <= 0.0000001) {
                        t.d = true;
                    }
                }

                tmp2IntN = 0; // clear tmp2
                tmpNewIntN = 0; // clear tmpNew

                for (int i = 0; i < tmpN; i++) {
                    T t = tmp[i];
                    if (t.d) continue;
                    int fb = (int) (t.b);
                    int fe = fb + 1;
                    lightY[fb] += applyLight(t, fb, y, td);
                    lightY[fb] += applyLight(t, fe, y, td);

                    if (objectsY[fb] == 2) {
                        cutInterval(t, fb);
                    }
                    if (objectsY[fe] == 2) {
                        cutInterval(t, fe);
                    }
                }
                if (y < h / 2 || a % 2 == 0) {
                    for (x = 0; x < w; x++) {
                        if (objectsY[x] == 1) { // light
                            lightY[x] += startIntensity;
//                            tmpNew[tmpNewIntN++] = new T(x, (y < h / 2 ? startIntensity : startIntensity * 2));
                            lightY[x] += startIntensity;
//                            tmpNew[tmpNewIntN++] = new T(x, (y < h / 2 ? startIntensity : startIntensity * 2));
                            tmpNew[tmpNewIntN++].set(x, (y < h / 2 ? startIntensity : startIntensity * 2));
                        }
                    }
                }

                T[] tmpTmp = tmp; // swap tmp and tmp2
                tmp = tmp2;
                tmp2 = tmpTmp;
                tmp2IntN = tmpN;
            }
        }
    }

    private double applyLight(T t, int x, int y, double td) {
        return max(min(t.b + t.l, x + 1) - max(t.b, x), 0) * t.i * td;
    }

    private void cutInterval(T t, int x) {
        double e = t.b + t.l;
        if (e <= x + 1 && e > x) {
            t.l = x - t.b;
        }
        if (t.b >= x && t.b < x + 1) {
            double b = x + 1;
            t.l -= b - t.b;
            t.b = b;
        }
        if (t.l <= 0) {
            t.d = true;
        }
    }

    private int merge() {
        int i = 0;
        int j = 0;
        int n = 0;

        j = 0;
        T tj;
        for (i = 0; i < tmp2IntN; i++) {
            T ti = tmp2[i];
            if (ti.d) continue;
            while (j < tmpNewIntN && (tj = tmpNew[j]).b <= ti.b) {
                n = merge1(tj, n);
                j++;
            }
            n = merge1(tmp2[i], n);
        }

        while (j < tmpNewIntN) {
            n = merge1(tmpNew[j], n);
            j++;
        }
        return n;
    }

    private int merge1(T t, int n) {
        if (n > 0) {
            T t2 = tmp[n - 1];
            double b = min(t.b, t2.b);
            double e = max(t.b + t.l, t2.b + t2.l);
            double l = e - b;
            if (l <= 1) {
                t2.b = b;
                t2.l = l;
                t2.i = (t.i * t.l + t2.i * t2.l) / l;
                return n;
            } else if (l <= 2 && t.b - t2.b - t2.l <= 0.5) {
                double l1 = l / 2;
                double i = (t2.i * t2.l + t.i * t.l) / l;
                t2.b = b;
                t2.l = l1;
                t2.i = i;

                t.b = t2.b + l1;
                t.l = l1;
                t.i = i;
            }
        }
        tmp[n++].setFrom(t);
        return n;
    }

    private static class T {
        double b, i, l;
        boolean d;

        public T(double b, double i) {
            this.b = b;
            this.l = 1;
            this.i = i;
        }

        public void set(double b, double i) {
            this.b = b;
            this.l = 1;
            this.i = i;
            this.d = false;
        }

        public void setFrom(T other) {
            this.b = other.b;
            this.i = other.i;
            this.l = other.l;
            this.d = other.d;
        }
    }

    double getLight(int x, int y) {
        return light[x][y];
    }

    private static double max(double a, double b) {
        return a > b ? a : b;
    }

    private static double min(double a, double b) {
        return a < b ? a : b;
    }
}
