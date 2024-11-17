package systems.helius.commons.types;

import java.util.Objects;

public class ComplexStructure {
    public static class MiddleStrata {
        public static class IntHolder {
            int id;

            public IntHolder(int id) {
                this.id = id;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                IntHolder intHolder = (IntHolder) o;
                return id == intHolder.id;
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(id);
            }
        }

        private static class PrivateInnerClass {
            IntHolder hiddenIntHolder;

            public PrivateInnerClass(IntHolder hiddenIntHolder) {
                this.hiddenIntHolder = hiddenIntHolder;
            }
        }

        private PrivateInnerClass privateInnerClass;

        public MiddleStrata(IntHolder hiddenIntHolder) {
            this.privateInnerClass = new PrivateInnerClass(hiddenIntHolder);
        }
    }

    private MiddleStrata a;
    private MiddleStrata b;

    public ComplexStructure(MiddleStrata.IntHolder idOfA, MiddleStrata.IntHolder idOfB) {
        this.a = new MiddleStrata(idOfA);
        this.b = new MiddleStrata(idOfB);
    }
}
