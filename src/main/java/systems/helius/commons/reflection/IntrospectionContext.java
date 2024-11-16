package systems.helius.commons.reflection;

import jakarta.annotation.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

public class IntrospectionContext {
    private final Class<?> rootClass;
    private Class<?> currentClass;
    private Field currentField;
    private MethodHandles.Lookup lookup;
    private IntrospectionSettings settings;

    public IntrospectionContext(Class<?> rootClass, Class<?> currentClass, Field currentField, MethodHandles.Lookup lookup, IntrospectionSettings settings) {
        this.rootClass = rootClass;
        this.currentClass = currentClass;
        this.currentField = currentField;
        this.lookup = lookup;
        this.settings = settings;
    }

    public Class<?> getRootClass() {
        return rootClass;
    }

    public Class<?> getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(Class<?> currentClass) {
        this.currentClass = currentClass;
    }

    public Field getCurrentField() {
        return currentField;
    }

    public void setCurrentField(Field currentField) {
        this.currentField = currentField;
    }

    public MethodHandles.Lookup getLookup() {
        return lookup;
    }

    public void setLookup(MethodHandles.Lookup lookup) {
        this.lookup = lookup;
    }

    public IntrospectionSettings getSettings() {
        return settings;
    }

    public void setSettings(IntrospectionSettings settings) {
        this.settings = settings;
    }
}
