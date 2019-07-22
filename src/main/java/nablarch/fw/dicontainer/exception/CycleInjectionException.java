package nablarch.fw.dicontainer.exception;

public class CycleInjectionException extends ContainerException {

    public CycleInjectionException(final String message) {
        super(message);
    }
}
