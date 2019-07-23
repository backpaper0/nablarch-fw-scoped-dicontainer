package nablarch.fw.dicontainer.exception;

/**
 * staticメソッドやstaticフィールドへインジェクションしようとしている場合にスローされる例外クラス。
 *
 */
public class StaticInjectionException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public StaticInjectionException(final String message) {
        super(message);
    }
}
