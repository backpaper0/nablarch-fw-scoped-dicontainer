package nablarch.fw.dicontainer.exception;

import java.util.List;
import java.util.Objects;

/**
 * IDコンテナを構築する際に発生した例外を集めた例外クラス。
 *
 */
public class ContainerCreationException extends ContainerException {

    /**
     * IDコンテナを構築する際に発生した例外
     */
    private final List<ContainerException> exceptions;

    /**
     * インスタンスを生成する。
     * 
     * @param exceptions IDコンテナを構築する際に発生した例外
     */
    public ContainerCreationException(final List<ContainerException> exceptions) {
        this.exceptions = Objects.requireNonNull(exceptions);
    }

    /**
     * IDコンテナを構築する際に発生した例外を返す。
     * 
     * @return IDコンテナを構築する際に発生した例外
     */
    public List<ContainerException> getExceptions() {
        return exceptions;
    }
}
