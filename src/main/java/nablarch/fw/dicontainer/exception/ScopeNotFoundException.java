package nablarch.fw.dicontainer.exception;

/**
 * スコープが見つからなかった場合にスローされる例外クラス。
 *
 */
public class ScopeNotFoundException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public ScopeNotFoundException(final String message) {
        super(message);
    }
}
