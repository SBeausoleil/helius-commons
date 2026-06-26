package systems.helius.commons.reflection;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import systems.helius.commons.exceptions.IntrospectionException;
import systems.helius.commons.types.School;
import systems.helius.commons.types.SchoolGenerator;

import java.lang.invoke.MethodHandles;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks {@link BeanIntrospector#seek(Class, Object, java.lang.invoke.MethodHandles.Lookup)}
 * on one fixed-size school graph using a cached inspector.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 2)
@Warmup(time = 5, iterations = 5)
@Measurement(time = 5, iterations = 5)
public class BeanIntrospectorBenchmark {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final int FIXED_STUDENT_COUNT = 25;
    private static final int FIXED_TEACHER_COUNT = 10;

    /**
     * Measures one full object-graph traversal searching for all reachable strings.
     *
     * @param inspector cached introspector benchmark state
     * @param plan generated fixed-size input graph state
     * @param bh JMH sink to prevent dead-code elimination
     * @throws IntrospectionException if fatal introspection access errors happen
     */
    @Benchmark
    public void seekAllReachableStrings(Caching inspector, ExecutionPlan plan, Blackhole bh)
            throws IntrospectionException {
        bh.consume(inspector.introspector.seek(String.class, plan.school, LOOKUP));
    }

    /**
     * Provides one fixed school object graph for each benchmark trial.
     */
    @State(Scope.Benchmark)
    public static class ExecutionPlan {
        private final SchoolGenerator schoolGenerator = new SchoolGenerator();
        School school;

        /**
         * Builds and normalizes the graph so every multi-value field has deterministic size.
         */
        @Setup(Level.Trial)
        public void setupSchool() {
            school = schoolGenerator.generate();
            school.getStudents().clear();
            school.getTeachers().clear();

            schoolGenerator.addStudents(school, FIXED_STUDENT_COUNT);
            schoolGenerator.addTeachers(school, FIXED_TEACHER_COUNT);

            ensureExactStudentsCount();
            ensureExactTeachersCount();
        }

        /**
         * Ensures students map cardinality is exactly the fixed count.
         */
        private void ensureExactStudentsCount() {
            while (school.getStudents().size() < FIXED_STUDENT_COUNT) {
                schoolGenerator.addStudents(school, FIXED_STUDENT_COUNT - school.getStudents().size());
            }

            Iterator<Integer> iterator = school.getStudents().keySet().iterator();
            while (school.getStudents().size() > FIXED_STUDENT_COUNT && iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }

        /**
         * Ensures teachers set cardinality is exactly the fixed count.
         */
        private void ensureExactTeachersCount() {
            while (school.getTeachers().size() < FIXED_TEACHER_COUNT) {
                schoolGenerator.addTeachers(school, FIXED_TEACHER_COUNT - school.getTeachers().size());
            }

            Iterator<?> iterator = school.getTeachers().iterator();
            while (school.getTeachers().size() > FIXED_TEACHER_COUNT && iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
    }

    /**
     * Holds a cached {@link BeanIntrospector} instance for all benchmark invocations in a trial.
     */
    @State(Scope.Benchmark)
    public static class Caching {
        BeanIntrospector introspector;

        /**
         * Initializes the cached introspector once per trial.
         */
        @Setup(Level.Trial)
        public void initialize() {
            introspector = new BeanIntrospector(new CachingClassInspector());
        }
    }
}
