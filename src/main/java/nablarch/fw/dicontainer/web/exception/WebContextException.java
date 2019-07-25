package nablarch.fw.dicontainer.web.exception;

import nablarch.fw.dicontainer.exception.ContainerException;
import nablarch.fw.dicontainer.web.context.RequestContext;
import nablarch.fw.dicontainer.web.context.SessionContext;

/**
 * {@link RequestContext}や{@link SessionContext}の扱いが不正だった場合にスローされる例外クラス。
 *
 */
public class WebContextException extends ContainerException {

    /**
     * インスタンスを生成する。
     * 
     * @param message 例外メッセージ
     */
    public WebContextException(final String message) {
        super(message);
    }
}
