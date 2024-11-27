package systems.helius.commons.types;

public class NumberWrapper {
    private Number value;

    public NumberWrapper(Number value) {
        this.value = value;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }
}
