package com.revjet.raycasting2d;

import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:ivan.zaytsev@webamg.com">Ivan Zaytsev</a>
 * 2018-02-06
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 8, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 8, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1,
//    jvmArgs = "-Xms2048m"
        jvmArgs = {"-Xms2048m", "-XX:+UseSuperWord"}
//    jvmArgs = {"-Xms2048m", "-XX:+UnlockDiagnosticVMOptions", "-XX:+TraceClassLoading", "-XX:+LogCompilation"}
)
@State(Scope.Thread)
public class LightingBenchmark {

    int w = 100;
    int h = 100;
    int[][] objects;

    Lighting l;

    @Setup
    public void setup() {
        objects = new int[w][h];
        for (int x = 0; x < w; x++) {
            Arrays.fill(objects[x], 1);
        }
//        objects[w / 2][h / 2] = 1;

        l = new Lighting(w, h);
    }

    @Benchmark
    public void testSimd() {
        l.recalculateLighting(objects, 1.0);
    }

    /*
    Best result:

    Result "testSimd":
      98.504 ±(99.9%) 13.158 ms/op [Average]
      (min, avg, max) = (95.557, 98.504, 104.421), stdev = 3.417
      CI (99.9%): [85.346, 111.663] (assumes normal distribution)


    Result "testSimd":
      84.876 ±(99.9%) 12.373 ms/op [Average]
      (min, avg, max) = (80.211, 84.876, 88.498), stdev = 3.213
      CI (99.9%): [72.503, 97.249] (assumes normal distribution)

    77.263 ±(99.9%) 3.022 ms/op [Average]
      (min, avg, max) = (75.065, 77.263, 79.338), stdev = 1.581
      CI (99.9%): [74.241, 80.285] (assumes normal distribution)

    */
}
