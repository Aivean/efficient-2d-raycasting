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
        l = new Lighting(w, h);
    }

    @Benchmark
    public void testSimd() {
        l.recalculateLighting(objects, 1F);
    }

    /*
    Best result:

      76.020 ±(99.9%) 13.015 ms/op [Average]
  (min, avg, max) = (69.478, 76.020, 91.642), stdev = 6.807
  CI (99.9%): [63.005, 89.035] (assumes normal distribution)


    */
}
