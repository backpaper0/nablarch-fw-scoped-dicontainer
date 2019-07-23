package nablarch.fw.dicontainer.exception;

/**
 * コンポーネントが重複していた場合にスローされる例外クラス。
 *
 */
public class ComponentDuplicatedException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public ComponentDuplicatedException(final String message) {
        super(message);
    }
}
