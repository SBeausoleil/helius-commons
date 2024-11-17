
package systems.helius.commons.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Elements annotated with Internal are only intended for usage within the module
 * of the declared element.
 * Compare to @Unstable, an @Internal is by definition also an @Unstable and should even less be used.
 * @see Unstable
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Internal {
}
