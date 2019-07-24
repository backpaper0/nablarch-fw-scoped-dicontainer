package nablarch.fw.dicontainer.exception;

/**
 * ファクトリメソッドのシグネチャが不正だった場合にスローされる例外クラス。
 *
 */
public class FactoryMethodSignatureException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public FactoryMethodSignatureException(final String message) {
        super(message);
    }
}
