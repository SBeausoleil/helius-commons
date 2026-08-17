package systems.helius.commons.fixtures;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComplexHuman {
    protected String firstName;
    protected String middleName;
    protected String lastName;
    private int age;

    private Sex birthSex;
    private Sex currentSex;
}
