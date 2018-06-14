package com.aivean.raycasting2d;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 8, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 8, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1,
        jvmArgs = {"-Xms2048m", "-XX:+UseSuperWord"}
)
@State(Scope.Thread)
public class LightingBenchmark {

    @Param({/*"80", */"100"})
    int size;

    int[][] inputFullyFilled;
    int[][] inputSingleLight;
    int[][] objectsQuarterFilled;

    Lighting l;

    @Setup
    public void setup() {
        inputFullyFilled = new int[size][size];
        inputSingleLight = new int[size][size];
        objectsQuarterFilled = new int[size][size];

        for (int x = 0; x < size; x++) {
            Arrays.fill(inputFullyFilled[x], 1);
        }

        inputSingleLight[size / 2][size / 2] = 1;
        l = new Lighting(size);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (x < size / 2 && y < size / 2)
                    objectsQuarterFilled[x][y] = 1;
            }
        }
    }

    @Benchmark
    public void testLightingFullyFilled() {
        l.setInputRotated(inputFullyFilled, Rotation.NO);
        l.recalculateLighting(1F);
    }

    @Benchmark
    public void testLightingSingleLightSource() {
        l.setInputRotated(inputSingleLight, Rotation.NO);
        l.recalculateLighting(1F);
    }

    @Benchmark
    public void testLightingLightQuarterFilled() {
        l.setInputRotated(objectsQuarterFilled, Rotation.NO);
        l.recalculateLighting(1F);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + LightingBenchmark.class.getSimpleName() + ".*")
                .forks(1)
                .verbosity(VerboseMode.EXTRA) //VERBOSE OUTPUT
//                .addProfiler(LinuxPerfAsmProfiler.class)
//                .addProfiler(LinuxPerfNormProfiler.class)
                .build();

        new Runner(opt).run();
    }
}
