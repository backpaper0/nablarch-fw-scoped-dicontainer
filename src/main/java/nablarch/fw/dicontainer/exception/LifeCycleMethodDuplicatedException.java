package nablarch.fw.dicontainer.exception;

/**
 * 初期化メソッドや破棄メソッドが重複している場合にスローされる例外クラス。
 *
 */
public class LifeCycleMethodDuplicatedException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public LifeCycleMethodDuplicatedException(final String message) {
        super(message);
    }
}
