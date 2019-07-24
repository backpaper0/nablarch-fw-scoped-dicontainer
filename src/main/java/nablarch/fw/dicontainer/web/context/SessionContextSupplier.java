package nablarch.fw.dicontainer.web.context;

/**
 * セッションコンテキストを取得するクラス。
 *
 */
public interface SessionContextSupplier {

    /**
     * セッションコンテキストを取得する。
     * 
     * @return セッションコンテキスト
     */
    SessionContext getSessionContext();
}
