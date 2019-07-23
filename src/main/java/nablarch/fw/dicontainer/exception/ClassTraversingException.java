package nablarch.fw.dicontainer.exception;

/**
 * コンポーネントを自動登録するために行うディレクトリトラバーサルで例外が発生した場合にラップしてスローされる例外クラス。
 *
 */
public class ClassTraversingException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param t ディレクトリトラバーサル中に発生した例外
     */
    public ClassTraversingException(final Throwable t) {
        super(t);
    }
}
