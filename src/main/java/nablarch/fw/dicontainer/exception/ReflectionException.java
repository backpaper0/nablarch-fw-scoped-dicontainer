package nablarch.fw.dicontainer.exception;

/**
 * リフレクションで例外が発生した場合にラップしてスローされる例外クラス。
 *
 */
public class ReflectionException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param t リフレクション中に発生した例外
     */
    public ReflectionException(final Throwable t) {
        super(t);
    }
}
