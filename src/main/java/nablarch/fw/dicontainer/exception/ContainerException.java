package nablarch.fw.dicontainer.exception;

/**
 * 例外の基底クラス。
 *
 */
public class ContainerException extends RuntimeException {

    /**
     * インスタンスを生成する。
     * 
     */
    public ContainerException() {
    }

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public ContainerException(final String message) {
        super(message);
    }

    /**
     * インスタンスを生成する。
     * 
     * @param t ラップする例外
     */
    public ContainerException(final Throwable t) {
        super(t);
    }
}
