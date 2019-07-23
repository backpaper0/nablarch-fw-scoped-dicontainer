package nablarch.fw.dicontainer.exception;

/**
 * インジェクションするコンポーネントが重複していた場合にスローされる例外クラス。
 *
 */
public class InjectionComponentDuplicatedException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public InjectionComponentDuplicatedException(final String message) {
        super(message);
    }
}
