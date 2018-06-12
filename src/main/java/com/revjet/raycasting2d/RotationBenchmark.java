package com.revjet.raycasting2d;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.ThreadLocalRandom;
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
        jvmArgs = {"-Xms2048m", "-XX:+UseSuperWord", "-XX:+UnlockDiagnosticVMOptions"
//                , "-XX:+PrintAssembly", "-XX:+PrintNMethods"
        }
//    jvmArgs = {"-Xms2048m", "-XX:+UnlockDiagnosticVMOptions", "-XX:+TraceClassLoading", "-XX:+LogCompilation"}
)
@State(Scope.Thread)
public class RotationBenchmark {


    @Param({/*"80", */"100"})
    int size;

    int[][] arg;
    int[][] res;

    @Setup
    public void setup() {
        arg = new int[size][size];
        res = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                arg[i][j] = ThreadLocalRandom.current().nextInt();
            }
        }
    }

    @Benchmark
    public void testRotationCCW() {
        Rotation.rotateCCW(arg, res);
    }

    @Benchmark
    public void testRotationCW() {
        Rotation.rotateCW(arg, res);
    }

    @Benchmark
    public void testRotation180() {
        Rotation.rotate180(arg, res);
    }

}
