package nablarch.fw.dicontainer.exception;

/**
 * コンポーネントが見つからない場合にスローされる例外クラス。
 *
 */
public class ComponentNotFoundException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public ComponentNotFoundException(final String message) {
        super(message);
    }
}
