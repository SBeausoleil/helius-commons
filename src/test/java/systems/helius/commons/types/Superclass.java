package systems.helius.commons.types;

public class Superclass {
    private int superclassField;

    public Superclass() {}

    public Superclass(int superclassField) {
        this.superclassField = superclassField;
    }

    public int getSuperclassField() {
        System.out.println("In getSuperclassField");
        return superclassField;
    }
}
