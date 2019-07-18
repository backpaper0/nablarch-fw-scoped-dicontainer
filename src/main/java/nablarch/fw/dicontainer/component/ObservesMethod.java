package nablarch.fw.dicontainer.component;

public interface ObservesMethod {

    boolean isTarget(final Object event);

    void invoke(final Object component, final Object event);
}
