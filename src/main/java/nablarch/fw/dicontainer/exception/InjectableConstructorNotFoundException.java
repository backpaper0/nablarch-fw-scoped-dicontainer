package nablarch.fw.dicontainer.exception;

public class InjectableConstructorNotFoundException extends ContainerException {

    public InjectableConstructorNotFoundException(final String message) {
        super(message);
    }
}
