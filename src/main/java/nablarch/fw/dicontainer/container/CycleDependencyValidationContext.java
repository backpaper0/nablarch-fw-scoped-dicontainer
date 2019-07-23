package nablarch.fw.dicontainer.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.exception.CycleInjectionException;

/**
 * 循環依存バリデーションのコンテキスト。
 *
 */
public final class CycleDependencyValidationContext {

    /**
     * DIコンテナのビルダー
     */
    private final ContainerBuilder<?> containerBuilder;
    /**
     * バリデーション対象となるコンポーネントの定義
     */
    private final ComponentDefinition<?> target;
    /**
     * 対象に依存されているコンポーネントの定義
     */
    private final List<ComponentDefinition<?>> dependencies;

    /**
     * インスタンスを生成する。
     * 
     * @param containerBuilder DIコンテナのビルダー
     * @param target バリデーション対象となるコンポーネントの定義
     * @param dependencies 対象に依存されているコンポーネントの定義
     */
    private CycleDependencyValidationContext(final ContainerBuilder<?> containerBuilder,
            final ComponentDefinition<?> target,
            final List<ComponentDefinition<?>> dependencies) {
        this.containerBuilder = Objects.requireNonNull(containerBuilder);
        this.target = Objects.requireNonNull(target);
        this.dependencies = Objects.requireNonNull(dependencies);
    }

    /**
     * 新しいコンテキストを生成する。
     * 
     * @param containerBuilder DIコンテナのビルダー
     * @param target バリデーション対象となるコンポーネントの定義
     * @return コンテキスト
     */
    static CycleDependencyValidationContext newContext(
            final ContainerBuilder<?> containerBuilder, final ComponentDefinition<?> target) {
        return new CycleDependencyValidationContext(containerBuilder, target,
                new ArrayList<>());
    }

    /**
     * 現在の状態をコピーして新しいコンテキストを生成する。
     * 
     * @return コピーして生成されたコンテキスト
     */
    public CycleDependencyValidationContext createSubContext() {
        return new CycleDependencyValidationContext(containerBuilder, target,
                new ArrayList<>(dependencies));
    }

    /**
     * 依存関係の循環を検出するためのバリデーションを行う。
     * 
     * @param key 検索キー
     */
    public void validateCycleDependency(final ComponentKey<?> key) {
        final Set<ComponentDefinition<?>> cds = containerBuilder.findComponentDefinitions(key);
        if (cds.size() != 1) {
            return;
        }
        final ComponentDefinition<?> dependency = cds.iterator().next();
        dependencies.add(dependency);
        if (dependencies.contains(target)) {
            final ComponentDefinition<?> cd = dependencies.stream().filter(target::equals)
                    .findAny()
                    .get();
            containerBuilder.addError(
                    new CycleInjectionException("Dependency between [" + target + "] and ["
                            + cd + "] is cycled."));
            return;
        }
        dependency.validateCycleDependency(this);
    }
}