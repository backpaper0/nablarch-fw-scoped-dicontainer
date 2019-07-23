package nablarch.fw.dicontainer.exception;

/**
 * インジェクションするコンポーネントとインジェクションされるコンポーネント間でスコープの広さが不正だった場合にスローされる例外クラス。
 *
 */
public class InvalidInjectionScopeException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public InvalidInjectionScopeException(final String message) {
        super(message);
    }
}
