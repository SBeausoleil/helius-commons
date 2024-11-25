package systems.helius.commons.reflection;

import org.junit.jupiter.api.Test;
import systems.helius.commons.types.Foo;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ClassInspectorTest {

    @Test
    void getAllFieldsHandles() throws IllegalAccessException, NoSuchFieldException {
        Foo foo = new Foo(5, "Hello");
        Map<Field, VarHandle> handles = ClassInspector.getAllFieldsHandles(foo.getClass(), MethodHandles.lookup());
        assertEquals(Foo.class.getDeclaredFields().length, handles.size());

        final int A_NEW_VALUE = 1;
        handles.get(Foo.class.getDeclaredField("a")).set(foo, A_NEW_VALUE);
        assertEquals(A_NEW_VALUE, foo.getA());

        final String B_NEW_VALUE = "World";
        VarHandle bHandle = handles.get(Foo.class.getDeclaredField("b"));
        bHandle.set(foo, B_NEW_VALUE);
        assertEquals(B_NEW_VALUE, bHandle.get(foo));
        assertTrue(foo.toString().contains(B_NEW_VALUE));
    }
}