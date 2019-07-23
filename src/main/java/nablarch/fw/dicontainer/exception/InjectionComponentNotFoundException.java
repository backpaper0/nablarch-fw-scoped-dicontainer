package nablarch.fw.dicontainer.exception;

/**
 * インジェクションするコンポーネントが見つからない場合にスローされる例外クラス。
 *
 */
public class InjectionComponentNotFoundException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public InjectionComponentNotFoundException(final String message) {
        super(message);
    }
}
