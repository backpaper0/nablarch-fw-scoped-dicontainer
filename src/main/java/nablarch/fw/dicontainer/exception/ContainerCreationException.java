package nablarch.fw.dicontainer.exception;

import java.util.List;
import java.util.Objects;

public class ContainerCreationException extends ContainerException {

    private final List<ContainerException> exceptions;

    public ContainerCreationException(final List<ContainerException> exceptions) {
        this.exceptions = Objects.requireNonNull(exceptions);
    }

    public List<ContainerException> getExceptions() {
        return exceptions;
    }
}
