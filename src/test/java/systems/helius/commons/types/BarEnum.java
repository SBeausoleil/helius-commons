package systems.helius.commons.types;

public enum BarEnum {
    A, B, C, D, E, F;

    final int id;

    BarEnum() {
        this.id = this.ordinal() * 37;
    }

    public int getId() {
        return id;
    }
}
