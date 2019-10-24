package nablarch.fw.dicontainer.nablarch;

import java.util.Collections;

import nablarch.core.repository.SystemRepository;
import nablarch.fw.dicontainer.Container;

/**
 * {@link ContainerImplementer}の管理クラス。
 * {@link SystemRepository}を使用して、{@link ContainerImplementer}の取得や登録、削除を行う。
 */
public final class ContainerImplementers {

    /** プライベートコンストラクタ。 */
    private ContainerImplementers() {
    }

    /**
     * {@link Container}を取得する。
     * @return {@link SystemRepository}に格納された{@link Container}
     */
    public static Container get() {
        return SystemRepository.get(name());
    }

    /**
     * {@link Container}を設定する。
     * @param container {@link SystemRepository}に格納する{@link Container}インスタンス
     */
    public static void set(final Container container) {
        SystemRepository.load(() -> Collections.singletonMap(name(), container));
    }

    /**
     * {@link Container}を{@link SystemRepository}から削除する。
     */
    public static void clear() {
        SystemRepository.load(() -> Collections.singletonMap(name(), null));
    }

    /**
     * 名前を取得する。
     * {@link SystemRepository}のキーとして使用する。
     * @return 名前
     */
    private static String name() {
        return Container.class.getName();
    }
}
