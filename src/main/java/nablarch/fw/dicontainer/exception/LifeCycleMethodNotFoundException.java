package nablarch.fw.dicontainer.exception;

/**
 * ファクトリーメソッドで定義されるコンポーネントが指定された破棄メソッドを持っていなかった場合にスローされる例外クラス。
 *
 */
public class LifeCycleMethodNotFoundException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public LifeCycleMethodNotFoundException(final String message) {
        super(message);
    }
}
