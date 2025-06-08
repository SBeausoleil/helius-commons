package systems.helius.commons.reflection;

import systems.helius.commons.annotations.Internal;
import systems.helius.commons.exceptions.LoookupAcquisitionException;

import java.lang.invoke.MethodHandles;

public class LookupManager {
    /**
     * Attempts to get a privileged (private-level access) lookup on a target class.
     *
     * @param target        the class on which a privileged lookup is desired.
     * @param rootContext   the original context of the request. Will be used as a backup if the parent may not grant privileged-access.
     * @param parent        the lookup on the class that owns the field where the target is the type.
     * @param forSuperclass indicates that the lookup is being made for the superclass of the parent.
     * @return a privileged lookup.
     */
    @Internal // TODO refactor and restrict access to module
    public MethodHandles.Lookup getPrivilegedLookup(Class<?> target, MethodHandles.Lookup rootContext, MethodHandles.Lookup parent, boolean forSuperclass) throws LoookupAcquisitionException {
        MethodHandles.Lookup acquiredAccess;
        try { // Check if the direct parent has access
            acquiredAccess = MethodHandles.privateLookupIn(target, parent);
        } catch (IllegalAccessException | SecurityException parentException) {
            try { // Fallback on the root context: perhaps the parent is part of a library who is not allowed such privileges
                acquiredAccess = MethodHandles.privateLookupIn(target, rootContext);
            } catch (IllegalAccessException | SecurityException rootContextException) {
                try { // Last resort: maybe this library is afforded the privilege by the type's module.
                    acquiredAccess = MethodHandles.privateLookupIn(target, MethodHandles.lookup());
                } catch (IllegalAccessException | SecurityException libraryLookupException) {
                    throw new LoookupAcquisitionException("Couldn't get privileged lookup access into: " + target.getCanonicalName()
                            + (forSuperclass ? "\n Accessing superclass of: " + parent.lookupClass()
                            : ".\n Parent class: " + parentException.getMessage())
                            + ",\n root context: " + rootContextException.getMessage()
                            + ",\n library context: " + libraryLookupException.getMessage());
                }
            }
        }
        return acquiredAccess;
    }
}
