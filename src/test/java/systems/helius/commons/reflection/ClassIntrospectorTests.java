package systems.helius.commons.reflection;

import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClassIntrospectorTests {

    @Test
    public void GivenClassWithOnlyPrivateFieldAndNoGetters_WhenGettingAllFieldHandles_ThenProvideVirtualGetters() throws Throwable {
        List<Accessor> accessors = ClassIntrospector.getAllFieldAccessors(Foo.class, MethodHandles.lookup());
        var instance = new Foo(1, "Hello World");
        for (Accessor handle : accessors) {
            assertNotNull(handle.get(instance));
        }
    }

    @Test
    public void GivenClass_WhenGivenHandles_ThenCanMatchHandlesToTheirUnderlyingType() throws IllegalAccessException {
        Accessor getter = ClassIntrospector.getAllFieldAccessors(FooWrapper.class, MethodHandles.lookup()).get(0);
        assertEquals(Foo.class, getter.getDeclaredType());
    }

    @Test
    public void GivenCollectionField_WhenIntrospected_ThenCanMatchAccessorToComponents() throws IllegalAccessException {
        
    }
}
