package systems.helius.commons.types;

import com.sb.factorium.BaseGenerator;

import java.util.concurrent.ThreadLocalRandom;

public class NumberWrapperGenerator extends BaseGenerator<NumberWrapper> {

    @Override
    protected NumberWrapper make() {
        return new NumberWrapper(ThreadLocalRandom.current().nextLong());
    }
}
