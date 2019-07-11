package nablarch.fw.dicontainer;

import javax.inject.Provider;

public interface Scope {

    <T> T getComponent(ComponentKey<T> key, Provider<T> provider);
}
