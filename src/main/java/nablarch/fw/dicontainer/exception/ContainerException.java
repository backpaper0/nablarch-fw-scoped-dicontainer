package nablarch.fw.dicontainer.exception;

public class ContainerException extends RuntimeException {

    public ContainerException() {
    }

    public ContainerException(final String message) {
        super(message);
    }

    public ContainerException(final Throwable t) {
        super(t);
    }
}
