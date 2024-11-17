package systems.helius.commons.types;

public class Foo {
    private int a;
    private String b;

    public Foo(int a, String b) {
        this.a = a;
        this.b = b;
    }

    public int getA() {
        System.out.println("Getting A through it's real getter!");
        return a;
    }
}
