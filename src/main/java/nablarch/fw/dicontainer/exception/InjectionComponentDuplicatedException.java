package nablarch.fw.dicontainer.exception;

public class InjectionComponentDuplicatedException extends ContainerException {

    public InjectionComponentDuplicatedException(final String message) {
        super(message);
    }
}
