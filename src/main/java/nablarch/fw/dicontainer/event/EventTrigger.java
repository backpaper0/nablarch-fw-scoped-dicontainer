package nablarch.fw.dicontainer.event;

/**
 * イベントを発火させる手段を提供するインターフェース。
 *
 */
public interface EventTrigger {

    /**
     * イベントを発火させる。
     * 
     * @param event イベント
     */
    void fire(Object event);
}
