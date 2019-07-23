package nablarch.fw.dicontainer.component;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * コンポーネント定義に付与されるID。
 *
 */
public final class ComponentId implements Serializable {

    /**
     * IDの値
     */
    private final UUID value;

    /**
     * インスタンスを生成する。
     * 
     * @param value IDの値
     */
    private ComponentId(final UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * IDを生成する。
     * 
     * @return 生成されたID
     */
    public static ComponentId generate() {
        return new ComponentId(UUID.randomUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
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
