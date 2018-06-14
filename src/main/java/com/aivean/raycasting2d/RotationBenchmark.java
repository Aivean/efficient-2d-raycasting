package com.aivean.raycasting2d;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 8, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 8, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1,
        jvmArgs = {"-Xms2048m", "-XX:+UseSuperWord"}
)
@State(Scope.Thread)
public class RotationBenchmark {

    @Param({/*"80", */"100"})
    int size;

    @Param({"CCW", "CW", "PI", "NO"})
    String rotation;

    Rotation rot;

    int[][] arg;
    int[][] res;

    @Setup
    public void setup() {
        rot = Rotation.valueOf(rotation);

        arg = new int[size][size];
        res = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                arg[i][j] = ThreadLocalRandom.current().nextInt();
            }
        }
    }

    @Benchmark
    public void benchRotation() {
        rot.rotateInt(res, arg);
    }

}
