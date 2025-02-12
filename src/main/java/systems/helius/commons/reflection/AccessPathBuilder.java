package systems.helius.commons.reflection;

import systems.helius.commons.types.Pair;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.LinkedList;

public class AccessPathBuilder<T> {
    Class<T> startingAt;
    LinkedList<Pair<Field, VarHandle>> path;

    public AccessPathBuilder(Class<T> startingAt) {
        this.startingAt = startingAt;
        this.path = new LinkedList<>();
    }

    public void goDeeper(Field field, VarHandle handle) {
        path.add(new Pair<>(field, handle));
    }

    public Pair<Field, VarHandle> goBack() {
        return path.removeLast();
    }

    public AccessPath<T> build(AccessPath.End end) {
        return new AccessPath<>(startingAt, (LinkedList<Pair<Field, VarHandle>>) path.clone(), end);
    }
}
