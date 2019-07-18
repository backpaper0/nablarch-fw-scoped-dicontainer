package nablarch.fw.dicontainer.component;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public final class ComponentId implements Serializable {

    private final UUID value;

    private ComponentId(final UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static ComponentId generate() {
        return new ComponentId(UUID.randomUUID());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj.getClass() != getClass()) {
            return false;
        }
        final ComponentId other = (ComponentId) obj;
        return value.equals(other.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
