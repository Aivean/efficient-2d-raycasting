package com.revjet.raycasting2d;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

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
        jvmArgs = {"-Xms2048m", "-XX:+UseSuperWord", "-XX:+UnlockDiagnosticVMOptions"
//                , "-XX:+PrintAssembly", "-XX:+PrintNMethods"
        }
//    jvmArgs = {"-Xms2048m", "-XX:+UnlockDiagnosticVMOptions", "-XX:+TraceClassLoading", "-XX:+LogCompilation"}
)
@State(Scope.Thread)
public class LightingBenchmark {


    @Param({/*"80", */"100"})
    int w;
    int h = w;

    int[][] objects;

    Lighting l;

    @Setup
    public void setup() {
        h = w;
        objects = new int[w][h];
        for (int x = 0; x < w; x++) {
            Arrays.fill(objects[x], 1);
        }
        l = new Lighting(w, h);
    }

    @Benchmark
    public void testLighting() {
        l.recalculateLighting(objects, 1F);
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + LightingBenchmark.class.getSimpleName() + ".*")
                .forks(1)
                .verbosity(VerboseMode.EXTRA) //VERBOSE OUTPUT
//                .addProfiler(LinuxPerfAsmProfiler.class)
                .addProfiler(LinuxPerfNormProfiler.class)
                .build();

        new Runner(opt).run();
    }

    /*
    Best result:

      76.020 ±(99.9%) 13.015 ms/op [Average]
  (min, avg, max) = (69.478, 76.020, 91.642), stdev = 6.807
  CI (99.9%): [63.005, 89.035] (assumes normal distribution)


    */
}
