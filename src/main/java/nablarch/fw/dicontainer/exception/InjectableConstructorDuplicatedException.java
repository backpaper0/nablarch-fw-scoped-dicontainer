package nablarch.fw.dicontainer.exception;

public class InjectableConstructorDuplicatedException extends ContainerException {

    public InjectableConstructorDuplicatedException(final String message) {
        super(message);
    }
}
