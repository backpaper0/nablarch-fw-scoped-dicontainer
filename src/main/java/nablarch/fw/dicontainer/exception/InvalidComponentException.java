package nablarch.fw.dicontainer.exception;

/**
 * 不正なコンポーネントを登録しようとした場合にスローされる例外クラス。
 *
 */
public class InvalidComponentException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public InvalidComponentException(final String message) {
        super(message);
    }
}
