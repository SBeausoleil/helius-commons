package systems.helius.commons.fixtures;

public class ChildClassA extends Superclass {
    private String name;

    public ChildClassA() {}

    public ChildClassA(int superclassField, String name) {
        super(superclassField);
        this.name = name;
    }
}
