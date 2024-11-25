package systems.helius.commons.types;

public class Foo {
    private int a;
    private String b;

    public Foo(int a, String b) {
        this.a = a;
        this.b = b;
    }

    public int getA() {
        return a;
    }

    @Override
    public String toString() {
        return "Foo{" +
                "a=" + a +
                ", b='" + b + '\'' +
                '}';
    }
}
