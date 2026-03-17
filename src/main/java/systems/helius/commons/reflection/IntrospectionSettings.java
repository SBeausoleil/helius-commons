package systems.helius.commons.reflection;

import systems.helius.commons.annotations.Unstable;
import systems.helius.commons.reflection.accessors.AccessorsChain;
import systems.helius.commons.reflection.accessors.ContentAccessor;

@Unstable
public class IntrospectionSettings {
    /**
     * If true (default), only fields and methods that may be accessed according to the rules will be made accessible.
     * If false, introspectors will throw an IllegalAccessException if faced with something it is not allowed to access.
     *
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/IllegalAccessException.html">Java 17 API: IllegalAccessException</a>
     */
    protected final boolean safeAccessCheck;

    /**
     * Controls what happens when an IllegalAccessException occurs whilst reading a field's value using a privileged lookup.
     * That is a very unlikely scenario that should only occur if the binary definition of the class that contains the
     * field being read changes between the lookup being acquired and the attempt to read.
     * If true, the exception will be ignored.
     * If false (default), the introspector will fail-fast and an IllegalAccessError will be thrown.
     *
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/IllegalAccessException.html">Java 17 API: IllegalAccessException</a>
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/MethodHandles.html#privateLookupIn(java.lang.Class,java.lang.invoke.MethodHandles.Lookup)">Java 17 API: MethodHandles.privateLookupIn(Class, Lookup)</a>
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html#unreflectVarHandle(java.lang.reflect.Field)">Java 17 API: Lookup.unreflectVarHandle</a>
     */
    protected final boolean ignoreIllegalAccessError;

    /**
     * If true (default), fields that cause any exception when accessed will be skipped.
     * If false, introspectors will throw an {@link systems.helius.commons.exceptions.IntrospectionException}
     * if faced with an exception when accessing a field.
     *
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/IllegalAccessException.html">Java 17 API: IllegalAccessException</a>
     */
    protected final boolean skipOnException;

    /**
     * If true, Iterable classes will have their internals inspected as if they were a regular class.
     * If false (default), Iterable classes will only have their iterable elements inspected.
     *
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Iterable.html">Java 17 API: Iterable</a>
     */
    protected final boolean detailedIterableCheck;
    /**
     * If true, Map classes will have their internals inspected as if they were a regular class.
     * If false (default), Map classes will only have their iterable entrySet inspected.
     *
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Map.html#entrySet()">Java 17 API: Map.entrySet()</a>
     */
    protected final boolean detailedMapCheck;

    /**
     * If true (default), instances of the target type will also be introspected for more instances.
     */
    protected final boolean enterTargetType;

    /**
     * How deep in the root object to search for matches.
     */
    protected final int maxDepth;

    protected final ContentAccessor contentAccessor;

    /**
     * Default introspection settings.
     * @deprecated use the builder.
     */
    @Deprecated(since = "0.7.0")
    public IntrospectionSettings() {
        this.safeAccessCheck = true;
        this.ignoreIllegalAccessError = false;
        this.skipOnException = true;
        this.detailedIterableCheck = false;
        this.detailedMapCheck = false;
        this.enterTargetType = true;
        this.maxDepth = Integer.MAX_VALUE;
        contentAccessor = new AccessorsChain.Builder().build();
    }

    private IntrospectionSettings(Builder b) {
        this.safeAccessCheck = b.safeAccessCheck;
        this.ignoreIllegalAccessError = b.ignoreIllegalAccessError;
        this.skipOnException = b.skipOnException;
        this.detailedIterableCheck = b.detailedIterableCheck;
        this.detailedMapCheck = b.detailedMapCheck;
        this.enterTargetType = b.enterTargetType;
        this.maxDepth = b.maxDepth;
        this.contentAccessor = b.contentAccessor;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .withSafeAccessCheck(safeAccessCheck)
                .withIgnoreIllegalAccessError(ignoreIllegalAccessError)
                .withSkipOnException(skipOnException)
                .withDetailedIterableCheck(detailedIterableCheck)
                .withDetailedMapCheck(detailedMapCheck)
                .withEnterTargetType(enterTargetType)
                .withMaxDepth(maxDepth);
    }

    public boolean useSafeAccessCheck() {
        return safeAccessCheck;
    }

    /**
     * @link ignoreIllegalAccessError
     */
    public boolean isIgnoreIllegalAccessError() {
        return ignoreIllegalAccessError;
    }

    public boolean isDetailedIterableCheck() {
        return detailedIterableCheck;
    }

    public boolean isDetailedMapCheck() {
        return detailedMapCheck;
    }

    public boolean isEnterTargetType() {
        return enterTargetType;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public static final class Builder {
        private boolean safeAccessCheck = true;
        private boolean ignoreIllegalAccessError = false;
        private boolean skipOnException = true;
        private boolean detailedIterableCheck = false;
        private boolean detailedMapCheck = false;
        private boolean enterTargetType = true;
        private int maxDepth = Integer.MAX_VALUE;
        private ContentAccessor contentAccessor;

        /**
         * If true (default), only fields and methods that may be accessed according to the rules will be made accessible.
         * If false, introspectors will throw an IllegalAccessException if faced with something it is not allowed to access.
         *
         * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/IllegalAccessException.html">Java 17 API: IllegalAccessException</a>
         */
        public Builder withSafeAccessCheck(boolean v) {
            this.safeAccessCheck = v;
            return this;
        }

        /**
         * Controls what happens when an IllegalAccessException occurs whilst reading a field's value using a privileged lookup.
         * That is a very unlikely scenario that should only occur if the binary definition of the class that contains the
         * field being read changes between the lookup being acquired and the attempt to read.
         * If true, the exception will be ignored.
         * If false (default), the introspector will fail-fast and an IllegalAccessError will be thrown.
         *
         * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/IllegalAccessException.html">Java 17 API: IllegalAccessException</a>
         * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/MethodHandles.html#privateLookupIn(java.lang.Class,java.lang.invoke.MethodHandles.Lookup)">Java 17 API: MethodHandles.privateLookupIn(Class, Lookup)</a>
         * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html#unreflectVarHandle(java.lang.reflect.Field)">Java 17 API: Lookup.unreflectVarHandle</a>
         */
        public Builder withIgnoreIllegalAccessError(boolean v) {
            this.ignoreIllegalAccessError = v;
            return this;
        }

        /**
         * If true (default), fields that cause any exception when accessed will be skipped.
         * If false, introspectors will throw an {@link systems.helius.commons.exceptions.IntrospectionException}
         * if faced with an exception when accessing a field.
         *
         * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/IllegalAccessException.html">Java 17 API: IllegalAccessException</a>
         */
        public Builder withSkipOnException(boolean v) {
            this.skipOnException = v;
            return this;
        }

        /**
         * If true, Iterable classes will have their internals inspected as if they were a regular class.
         * If false (default), Iterable classes will only have their iterable elements inspected.
         *
         * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Iterable.html">Java 17 API: Iterable</a>
         */
        public Builder withDetailedIterableCheck(boolean v) {
            this.detailedIterableCheck = v;
            return this;
        }

        /**
         * If true, Map classes will have their internals inspected as if they were a regular class.
         * If false (default), Map classes will only have their iterable entrySet inspected.
         *
         * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Map.html#entrySet()">Java 17 API: Map.entrySet()</a>
         */
        public Builder withDetailedMapCheck(boolean v) {
            this.detailedMapCheck = v;
            return this;
        }

        /**
         * If true (default), instances of the target type will also be introspected for more instances.
         */
        public Builder withEnterTargetType(boolean v) {
            this.enterTargetType = v;
            return this;
        }

        /**
         * How deep in the root object to search for matches.
         */
        public Builder withMaxDepth(int v) {
            this.maxDepth = v;
            return this;
        }

        public Builder withContentAccessor(ContentAccessor v) {
            this.contentAccessor = v;
            return this;
        }

        public IntrospectionSettings build() {
            if (maxDepth < 0) throw new IllegalArgumentException("maxDepth must be >= 0");
            return new IntrospectionSettings(this);
        }
    }
}
