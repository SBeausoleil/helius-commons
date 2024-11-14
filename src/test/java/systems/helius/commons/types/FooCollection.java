package systems.helius.commons.types;

import java.util.List;
import java.util.Set;

public class FooCollection<MaValeurGeneriqueDeClasse extends Foo> {
    private List<MaValeurGeneriqueDeClasse> foos;
    private Set<Foo> fooSet;
}
