package nablarch.fw.dicontainer.exception;

/**
 * スコープが重複している場合にスローされる例外クラス。
 *
 */
public class ScopeDuplicatedException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public ScopeDuplicatedException(final String message) {
        super(message);
    }
}
