package systems.helius.commons.reflection;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1)
@Warmup(time = 5, iterations = 2)
@Measurement(time = 5, iterations = 2)
public class ClassInspectorBenchmark {

    @Benchmark
    public void getAllFieldsHierarchical_noCache(ExecutionPlan plan, Blackhole bh) {
        bh.consume(ClassInspector.getAllFieldsHierarchical(plan.classToInspect));
    }

    @Benchmark
    public void getAllFieldsHierarchical_cached(Inspector inspector, ExecutionPlan plan, Blackhole bh) {
        bh.consume(inspector.cachingClassInspector.getAllFieldsHierarchical(plan.classToInspect));
    }

    @State(Scope.Benchmark)
    public static class ExecutionPlan {
        @Param(value = {
                "systems.helius.commons.types.Foo",
                "systems.helius.commons.types.DataClass",
                "systems.helius.commons.types.ChildClassA",
                "systems.helius.commons.types.ChildClassB",
                "systems.helius.commons.types.ComplexChild"
        })
        String className;

        Class<?> classToInspect;

        @Setup
        public void getClassToInspect() {
            try {
                System.out.println("Setting class to inspect: " + className);
                classToInspect = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @State(Scope.Benchmark)
    public static class Inspector {
        CachingClassInspector cachingClassInspector;

        @Setup(Level.Iteration)
        public void setup() {
            System.out.println("Initializing CachingClassInspector");
            cachingClassInspector = new CachingClassInspector();
        }
    }
}
