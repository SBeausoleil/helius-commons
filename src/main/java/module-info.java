/**
 * Helius-Commons is a utilities library that provides a set of common functionalities and utilities for Java applications.
 * It has a large focus on reflection and metaprogramming, providing tools to inspect and manipulate classes, methods, and fields at runtime.
 */
module systems.helius.commons {
    requires transitive jakarta.annotation;

    exports systems.helius.commons;
    exports systems.helius.commons.annotations;
    exports systems.helius.commons.collections;
    exports systems.helius.commons.exceptions;
    exports systems.helius.commons.lambda;
    exports systems.helius.commons.reflection;
    exports systems.helius.commons.reflection.accessors;
    // Note: systems.helius.commons.reflection.internal is intentionally NOT exported.
    exports systems.helius.commons.tests;
    exports systems.helius.commons.time;
    exports systems.helius.commons.types;
}