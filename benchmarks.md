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

