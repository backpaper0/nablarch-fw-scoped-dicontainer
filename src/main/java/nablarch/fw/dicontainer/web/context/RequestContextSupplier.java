package nablarch.fw.dicontainer.web.context;

/**
 * リクエストコンテキストを取得するクラス。
 *
 */
public interface RequestContextSupplier {

    /**
     * リクエストコンテキストを取得する。
     * 
     * @return リクエストコンテキスト
     */
    RequestContext getRequestContext();
}
