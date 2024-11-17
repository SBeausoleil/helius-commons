package systems.helius.commons.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The referenced element is subject to breaking changes without a change in major version of the library.
 * You may see them as Preview elements that may be stabilized, removed, or modified at any time.
 * Usually indicate that the annotated element is not quite to the quality standard of the library's authors.
 * Use at your own risk.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Unstable {
}
