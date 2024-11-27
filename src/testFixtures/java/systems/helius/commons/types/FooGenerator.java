package systems.helius.commons.types;

import com.sb.factorium.FakerGenerator;

public class FooGenerator extends FakerGenerator<Foo> {
    @Override
    protected Foo make() {
        return new Foo(
                faker.random().nextInt(Integer.MAX_VALUE),
                faker.lorem().word()
        );
    }
}
