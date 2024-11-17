package systems.helius.commons.reflection;

public final class IntrospectionSettingsBuilder {
    private IntrospectionSettings introspectionSettings;

    public IntrospectionSettingsBuilder() {
        introspectionSettings = new IntrospectionSettings();
    }

    public IntrospectionSettingsBuilder(IntrospectionSettings other) {
        this.introspectionSettings = other;
    }

    public static IntrospectionSettingsBuilder anIntrospectionSettings() {
        return new IntrospectionSettingsBuilder();
    }

    public IntrospectionSettingsBuilder withSafeAccessCheck(boolean safeAccessCheck) {
        introspectionSettings.setSafeAccessCheck(safeAccessCheck);
        return this;
    }

    public IntrospectionSettingsBuilder withIgnoreIllegalAccessError(boolean ignoreIllegalAccessError) {
        introspectionSettings.setIgnoreIllegalAccessError(ignoreIllegalAccessError);
        return this;
    }

    public IntrospectionSettingsBuilder withDetailledIterableCheck(boolean detailledIterableCheck) {
        introspectionSettings.setDetailledIterableCheck(detailledIterableCheck);
        return this;
    }

    public IntrospectionSettingsBuilder withDetailledMapCheck(boolean detailledMapCheck) {
        introspectionSettings.setDetailledMapCheck(detailledMapCheck);
        return this;
    }

    public IntrospectionSettingsBuilder withEnterTargetType(boolean enterTargetType) {
        introspectionSettings.setEnterTargetType(enterTargetType);
        return this;
    }

    public IntrospectionSettingsBuilder withMaxDepth(int maxDepth) {
        introspectionSettings.setMaxDepth(maxDepth);
        return this;
    }

    public IntrospectionSettings build() {
        return introspectionSettings;
    }
}
