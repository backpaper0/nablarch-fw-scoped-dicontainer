package nablarch.fw.dicontainer.annotation.auto.custom;

import java.util.Objects;

public class Custom1 {

    private final Custom2 custom2;

    public Custom1(final Custom2 custom2) {
        this.custom2 = Objects.requireNonNull(custom2);
    }

    public Custom2 getCustom2() {
        return custom2;
    }
}
