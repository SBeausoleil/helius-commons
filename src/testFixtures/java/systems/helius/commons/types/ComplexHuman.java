package systems.helius.commons.types;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ComplexHuman extends ComplexStructure {
    protected String firstName;
    protected String middleName;
    protected String lastName;
    private int age;

    private Sex birthSex;
    private Sex currentSex;
}
