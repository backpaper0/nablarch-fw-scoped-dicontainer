package nablarch.fw.dicontainer.exception;

/**
 * コンポーネントのインスタンスを生成するコンストラクタが重複していた場合にスローされる例外クラス。
 *
 */
public class InjectableConstructorDuplicatedException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public InjectableConstructorDuplicatedException(final String message) {
        super(message);
    }
}
