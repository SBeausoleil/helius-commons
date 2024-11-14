package systems.helius.commons.reflection;

import org.junit.jupiter.api.Test;
import systems.helius.commons.types.*;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    public void GivenChildClass_WhenIntrospected_ThenCanSeeParentFields() throws IllegalAccessException {
        List<Accessor> accessors = ClassIntrospector.getAllFieldAccessors(ChildClassA.class, MethodHandles.lookup());
        List<Field> expected = List.of(Superclass.class.getDeclaredFields());
        assertTrue(expected.stream()
                .allMatch(field -> accessors.stream()
                        .anyMatch(acc -> acc.field().equals(field))));
    }

    @Test
    public void GivenRecord_WhenIntrospected_ThenCanSeeFields() throws IllegalAccessException {
        List<Accessor> accessors = ClassIntrospector.getAllFieldAccessors(RecordType.class, MethodHandles.lookup());
        assertTrue(!accessors.isEmpty());
        var instance = new RecordType(1, 5);
        for (Accessor handle : accessors) {
            assertEquals(int.class, handle.getDeclaredType());
            assertTrue(instance.isContained((int) handle.get(instance)));
        }
    }

    @Test
    public void GivenCollectionField_WhenIntrospectedByTypes_ThenCanMatchAccessorToComponents() throws IllegalAccessException {
        List<Accessor> accessors = ClassIntrospector.getAllFieldAccessors(FooCollection.class, MethodHandles.lookup());
        Accessor getter = accessors.get(0);
        assertEquals(List.class, getter.getDeclaredType());
        System.out.println(getter.getDeclaredType().arrayType());
    }
}
