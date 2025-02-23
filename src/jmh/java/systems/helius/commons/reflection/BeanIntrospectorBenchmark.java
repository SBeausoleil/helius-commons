package systems.helius.commons.reflection;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import systems.helius.commons.types.School;
import systems.helius.commons.types.SchoolGenerator;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 2)
@Warmup(time = 5, iterations = 5)
@Measurement(time = 5, iterations = 5)
public class BeanIntrospectorBenchmark {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    @Benchmark
    public void getAllFieldsHierarchical_noCache(Basic inspector, ExecutionPlan plan, Blackhole bh) throws IllegalAccessException {
        bh.consume(inspector.introspector.seek(String.class, plan.school, LOOKUP));
    }

    @Benchmark
    public void getAllFieldsHierarchical_cached(Caching inspector, ExecutionPlan plan, Blackhole bh) throws IllegalAccessException {
        bh.consume(inspector.introspector.seek(String.class, plan.school, LOOKUP));
    }

    @State(Scope.Thread)
    public static class ExecutionPlan {
        private static final SchoolGenerator schoolGenerator = new SchoolGenerator();
        School school;
        @Param({"100", "1000"})
        int nStudents;
        @Param({"100", "1000"})
        int nTeachers;

        @Setup(Level.Invocation)
        public void setupSchool() {
            school = schoolGenerator.generate();
            schoolGenerator.addStudents(school, nStudents);
            schoolGenerator.addTeachers(school, nTeachers);
        }
    }

    @State(Scope.Benchmark)
    public static class Caching {
        BeanIntrospector introspector;

        @Setup(Level.Iteration)
        public void initialize() {
            System.out.println("Initializing BeanIntrospector (Cached)");
            introspector = new BeanIntrospector(new CachingClassInspector());
        }
    }

    @State(Scope.Benchmark)
    public static class Basic {
        BeanIntrospector introspector;

        @Setup(Level.Iteration)
        public void initializeNotCaching() {
            System.out.println("Initializing BeanIntrospector");
            introspector = new BeanIntrospector(new ClassInspector());
        }
    }
}
