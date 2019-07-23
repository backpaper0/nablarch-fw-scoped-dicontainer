package nablarch.fw.dicontainer.exception;

/**
 * 依存関係が循環していた場合にスローされる例外クラス。
 *
 */
public class CycleInjectionException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public CycleInjectionException(final String message) {
        super(message);
    }
}
