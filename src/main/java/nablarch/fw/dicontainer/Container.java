package nablarch.fw.dicontainer;

import java.lang.annotation.Annotation;

public interface Container {

    <T> T getComponent(ComponentId id);

    <T> T getComponent(ComponentKey<T> key);

    <T> T getComponent(Class<T> key);

    <T> T getComponent(Class<T> key, Annotation... qualifiers);

    void fire(Object event);

    void destroy();
}
