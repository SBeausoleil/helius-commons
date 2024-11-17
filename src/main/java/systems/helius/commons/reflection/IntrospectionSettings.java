package systems.helius.commons.reflection;

import systems.helius.commons.reflection.handlers.IterableLazyHandler;

import java.util.ArrayList;
import java.util.List;

// TODO create a settings overrides builder for the bean introspection
public class IntrospectionSettings {
    /**
     * If true (default), only fields and methods that may be accessed according to the rules will be made accessible.
     * If false, introspectors will throw an InaccessibleObjectException if faced with something it is not allowed to access.
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/IllegalAccessException.html">Java 17 API: IllegalAccessException</a>
     * @see <a href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/reflect/InaccessibleObjectException.html">Java 17 API: InaccessibleObjectException</a>
     *
     */
    protected boolean safeAccessCheck = true;
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

    @Deprecated
    protected List<ValueHandler> specialValueHandlers = new ArrayList<>(
            List.of(
                    new IterableLazyHandler()
            )
    );

    public boolean isSafeAccessCheck() {
        return safeAccessCheck;
    }

    public void setSafeAccessCheck(boolean safeAccessCheck) {
        this.safeAccessCheck = safeAccessCheck;
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

    public List<ValueHandler> getSpecialValueHandlers() {
        return specialValueHandlers;
    }

    public void setSpecialValueHandlers(List<ValueHandler> specialValueHandlers) {
        this.specialValueHandlers = specialValueHandlers;
    }
}
