package systems.helius.commons.reflection;

import jakarta.annotation.Nullable;
import systems.helius.commons.annotations.Internal;
import systems.helius.commons.exceptions.LoookupAcquisitionException;

import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

public class LookupManager {
    /**
     * Attempts to get a privileged (private-level access) lookup on a target class.
     *
     * @param target        the class on which a privileged lookup is desired.
     * @param caller        the lookup of the caller or, ideally, of the target class itself.
     * @param fallbacks     (optional) fallback lookups that may be tried, such as the original context of the request.
     *                      Will be used as a backup if the caller may not grant privileged-access.
     * @return a privileged lookup.
     */
    @Internal // TODO restrict access to module
    public MethodHandles.Lookup getPrivilegedLookup(Class<?> target, MethodHandles.Lookup caller, MethodHandles.Lookup... fallbacks) throws LoookupAcquisitionException {
        List<String> errorMessages;
        IllegalAccessException originalException;
        try {
            return MethodHandles.privateLookupIn(target, caller);
        } catch (IllegalAccessException e) {
            errorMessages = new LinkedList<>();
            errorMessages.add(e.getMessage());
            originalException = e;
        }

        for (var fallback : fallbacks) {
            try {
                return MethodHandles.privateLookupIn(target, fallback);
            } catch (IllegalAccessException e) {
                errorMessages.add(e.getMessage());
            }
        }

        String message = "All access has been denied: " + String.join(", ", errorMessages);
        throw new LoookupAcquisitionException(message, originalException);
    }
}
