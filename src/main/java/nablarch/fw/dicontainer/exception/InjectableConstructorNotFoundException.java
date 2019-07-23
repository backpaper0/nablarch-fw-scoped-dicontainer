package nablarch.fw.dicontainer.exception;

/**
 * コンポーネントのインスタンスを生成するコンストラクタが見つからない場合にスローされる例外クラス。
 *
 */
public class InjectableConstructorNotFoundException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public InjectableConstructorNotFoundException(final String message) {
        super(message);
    }
}
