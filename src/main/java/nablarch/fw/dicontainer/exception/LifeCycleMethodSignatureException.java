package nablarch.fw.dicontainer.exception;

/**
 * 初期化メソッドや破棄メソッドのシグネチャが不正だった場合にスローされる例外クラス。
 *
 */
public class LifeCycleMethodSignatureException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public LifeCycleMethodSignatureException(final String message) {
        super(message);
    }
}
