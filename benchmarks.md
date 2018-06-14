Benchmark results
=================


System:
* MacBook Pro
* Processor 2.9 GHz Intel Core i7


Rotation
--------

```
# JMH 1.14 (released 644 days ago, please consider updating!)
# VM version: JDK 1.8.0_51, VM 25.51-b03
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/bin/java
# VM options: -Xms2048m -XX:+UseSuperWord -XX:+UnlockDiagnosticVMOptions
# Warmup: 8 iterations, 1 s each
# Measurement: 8 iterations, 2 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: com.aivean.raycasting2d.RotationBenchmark.benchRotation

# Run complete. Total time: 00:01:37

Benchmark                        (rotation)  (size)  Mode  Cnt  Score    Error  Units
RotationBenchmark.benchRotation         CCW     100  avgt    8  0.010 ±  0.001  ms/op
RotationBenchmark.benchRotation          CW     100  avgt    8  0.008 ±  0.001  ms/op
RotationBenchmark.benchRotation          PI     100  avgt    8  0.004 ±  0.001  ms/op
RotationBenchmark.benchRotation          NO     100  avgt    8  0.002 ±  0.001  ms/op
```


Lighting
--------

```
# JMH 1.14 (released 644 days ago, please consider updating!)
# VM version: JDK 1.8.0_51, VM 25.51-b03
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/jre/bin/java
# VM options: -Xms2048m -XX:+UseSuperWord
# Warmup: 8 iterations, 1 s each
# Measurement: 8 iterations, 2 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: com.aivean.raycasting2d.LightingBenchmark.testLighting
# Parameters: (size = 100)

Result "testLightingFullyFilled":
  63.567 ±(99.9%) 0.708 ms/op [Average]
  (min, avg, max) = (63.104, 63.567, 64.106), stdev = 0.370
  CI (99.9%): [62.859, 64.275] (assumes normal distribution)


Result "testLightingLightQuarterFilled":
  35.611 ±(99.9%) 0.725 ms/op [Average]
  (min, avg, max) = (35.048, 35.611, 36.092), stdev = 0.379
  CI (99.9%): [34.887, 36.336] (assumes normal distribution)


Result "testLightingSingleLightSource":
  0.211 ±(99.9%) 0.005 ms/op [Average]
  (min, avg, max) = (0.208, 0.211, 0.215), stdev = 0.003
  CI (99.9%): [0.206, 0.216] (assumes normal distribution)


# Run complete. Total time: 00:01:14

Benchmark                                        (size)  Mode  Cnt   Score   Error  Units
LightingBenchmark.testLightingFullyFilled           100  avgt    8  63.567 ± 0.708  ms/op
LightingBenchmark.testLightingLightQuarterFilled    100  avgt    8  35.611 ± 0.725  ms/op
LightingBenchmark.testLightingSingleLightSource     100  avgt    8   0.211 ± 0.005  ms/op

```