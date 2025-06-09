package systems.helius.commons.reflection;

public class IntrospectionSettings {
    /**
     * If true (default), only fields and methods that may be accessed according to the rules will be made accessible.
     * If false, introspectors will throw an IllegalAccessException if faced with something it is not allowed to access.
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/IllegalAccessException.html">Java 17 API: IllegalAccessException</a>
     */
    // TODO consider making this a per-introspector setting
    protected boolean safeAccessCheck = true;
    /**
     * Controls what happens when an IllegalAccessException occurs whilst reading a field's value using a privileged lookup.
     * That is a very unlikely scenario that should only occur if the binary definition of the class that contains the
     * field being read changes between the lookup being acquired and the attempt to read.
     * If true, the exception will be ignored.
     * If false (default), the introspector will fail-fast and an IllegalAccessError will be thrown.
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/IllegalAccessException.html">Java 17 API: IllegalAccessException</a>
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/MethodHandles.html#privateLookupIn(java.lang.Class,java.lang.invoke.MethodHandles.Lookup)">Java 17 API: MethodHandles.privateLookupIn(Class, Lookup)</a>
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html#unreflectVarHandle(java.lang.reflect.Field)">Java 17 API: Lookup.unreflectVarHandle</a>
     */
    protected boolean ignoreIllegalAccessError = false;

    /**
     * If true (default), fields that cause any exception when accessed will be skipped.
     * If false, introspectors will throw an {@link systems.helius.commons.exceptions.IntrospectionException}
     * if faced with an exception when accessing a field.
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/IllegalAccessException.html">Java 17 API: IllegalAccessException</a>
     */
    // TODO consider making this a per-introspector setting
    protected boolean skipOnException = true;

    /**
     * If true, Iterable classes will have their internals inspected as if they were a regular class.
     * If false (default), Iterable classes will only have their iterable elements inspected.
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Iterable.html">Java 17 API: Iterable</a>
     */
    protected boolean detailledIterableCheck = false;
    /**
     * If true, Map classes will have their internals inspected as if they were a regular class.
     * If false (default), Map classes will only have their iterable entrySet inspected.
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Map.html#entrySet()">Java 17 API: Map.entrySet()</a>
     */
    protected boolean detailledMapCheck = false;

    /**
     * If true (default), instances of the target type will also be introspected for more instances.
     */
    protected boolean enterTargetType = true;

    protected int maxDepth = Integer.MAX_VALUE;

    public boolean useSafeAccessCheck() {
        return safeAccessCheck;
    }

    public void setSafeAccessCheck(boolean safeAccessCheck) {
        this.safeAccessCheck = safeAccessCheck;
    }

    public boolean isIgnoreIllegalAccessError() {
        return ignoreIllegalAccessError;
    }

    public void setIgnoreIllegalAccessError(boolean ignoreIllegalAccessError) {
        this.ignoreIllegalAccessError = ignoreIllegalAccessError;
    }

    public boolean isDetailledIterableCheck() {
        return detailledIterableCheck;
    }

    public void setDetailledIterableCheck(boolean detailledIterableCheck) {
        this.detailledIterableCheck = detailledIterableCheck;
    }

    public boolean isDetailledMapCheck() {
        return detailledMapCheck;
    }

    public void setDetailledMapCheck(boolean detailledMapCheck) {
        this.detailledMapCheck = detailledMapCheck;
    }

    public boolean isEnterTargetType() {
        return enterTargetType;
    }

    public void setEnterTargetType(boolean enterTargetType) {
        this.enterTargetType = enterTargetType;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}
