package nablarch.fw.dicontainer.nablarch;

import java.util.Collections;

import nablarch.core.repository.SystemRepository;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.container.ContainerImplementer;

/**
 * {@link ContainerImplementer}の管理クラス。
 * {@link SystemRepository}を使用して、{@link ContainerImplementer}の取得や登録、削除を行う。
 */
public final class ContainerImplementers {

    /** プライベートコンストラクタ。 */
    private ContainerImplementers() {
    }

    /**
     * {@link ContainerImplementer}を取得する。
     * @return {@link SystemRepository}に格納された{@link ContainerImplementer}
     */
    public static ContainerImplementer get() {
        return SystemRepository.get(name());
    }

    /**
     * {@link ContainerImplementer}を設定する。
     * @param container {@link SystemRepository}に格納する{@link ContainerImplementer}インスタンス
     */
    public static void set(final ContainerImplementer container) {
        SystemRepository.load(() -> Collections.singletonMap(name(), container));
    }

    /**
     * {@link ContainerImplementer}を{@link SystemRepository}から削除する。
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
