package systems.helius.commons;

import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import systems.helius.commons.reflection.BeanIntrospectorBenchmark;
import systems.helius.commons.reflection.ClassInspectorBenchmark;

import java.util.concurrent.TimeUnit;

public class JmhRunner {

    // Heavily inspired from https://mkyong.com/java/java-jmh-benchmark-tutorial/
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BeanIntrospectorBenchmark.class.getName())
                .include(ClassInspectorBenchmark.class.getName())
                .build();

        new Runner(opt).run();
    }
}
