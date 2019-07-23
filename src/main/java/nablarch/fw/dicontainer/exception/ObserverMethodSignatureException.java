package nablarch.fw.dicontainer.exception;

/**
 * イベントハンドラメソッドのシグネチャが不正だった場合にスローされる例外クラス。
 *
 */
public class ObserverMethodSignatureException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public ObserverMethodSignatureException(final String message) {
        super(message);
    }
}
