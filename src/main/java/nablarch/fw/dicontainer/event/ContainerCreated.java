package nablarch.fw.dicontainer.event;

/**
 * DIコンテナが構築されたときに発火されるイベント。
 *
 */
public final class ContainerCreated {

    @Override
    public String toString() {
        return getClass().getName();
    }
}
