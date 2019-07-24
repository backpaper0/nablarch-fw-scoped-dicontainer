package nablarch.fw.dicontainer.annotation;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentDefinition.Builder;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.DestroyMethod;
import nablarch.fw.dicontainer.component.ErrorCollector;
import nablarch.fw.dicontainer.component.FactoryMethod;
import nablarch.fw.dicontainer.component.InitMethod;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.component.ObservesMethod;
import nablarch.fw.dicontainer.scope.Scope;

/**
 * アノテーションをもとにコンポーネント定義を生成するファクトリ。
 *
 */
public final class AnnotationComponentDefinitionFactory {

    /**
     * コンポーネント定義の構成要素のファクトリ
     */
    private final AnnotationMemberFactory memberFactory;
    /**
     * スコープを決定するクラス
     */
    private final AnnotationScopeDecider scopeDecider;

    /**
     * インスタンスを生成する。
     * 
     * @param memberFactory コンポーネント定義の構成要素のファクトリ
     * @param scopeDecider スコープを決定するクラス
     */
    public AnnotationComponentDefinitionFactory(final AnnotationMemberFactory memberFactory,
            final AnnotationScopeDecider scopeDecider) {
        this.memberFactory = Objects.requireNonNull(memberFactory);
        this.scopeDecider = Objects.requireNonNull(scopeDecider);
    }

    /**
     * コンポーネントのクラスをもとにコンポーネント定義を生成する。
     * 
     * @param <T> コンポーネントの型
     * @param componentType コンポーネントのクラス
     * @param errorCollector バリデーションエラーを収集するクラス
     * @return コンポーネント定義
     */
    public <T> Optional<ComponentDefinition<T>> fromComponentClass(final Class<T> componentType,
            final ErrorCollector errorCollector) {
        final Builder<T> builder = ComponentDefinition.builder(componentType);

        final Optional<InjectableMember> injectableConstructor = memberFactory
                .createConstructor(componentType, errorCollector);
        final List<InjectableMember> injectableMembers = memberFactory
                .createFieldsAndMethods(componentType, errorCollector);
        final List<ObservesMethod> observesMethods = memberFactory
                .createObservesMethod(componentType, errorCollector);
        final Optional<InitMethod> initMethod = memberFactory.createInitMethod(componentType,
                errorCollector);
        final Optional<DestroyMethod> destroyMethod = memberFactory
                .createDestroyMethod(componentType, errorCollector);
        final List<FactoryMethod> factoryMethods = memberFactory.createFactoryMethods(builder.id(),
                componentType, this, errorCollector);
        final Optional<Scope> scope = scopeDecider.fromComponentClass(componentType,
                errorCollector);

        injectableConstructor.ifPresent(builder::injectableConstructor);
        initMethod.ifPresent(builder::initMethod);
        destroyMethod.ifPresent(builder::destroyMethod);
        scope.ifPresent(builder::scope);

        return builder
                .injectableMembers(injectableMembers)
                .observesMethods(observesMethods)
                .factoryMethods(factoryMethods)
                .build();
    }

    /**
     * ファクトリメソッドをもとにコンポーネント定義を生成する。
     * 
     * @param <T> コンポーネントの型
     * @param factoryId ファクトリのID
     * @param factoryMethod ファクトリメソッド
     * @param errorCollector バリデーションエラーを収集するクラス
     * @return コンポーネント定義
     */
    public <T> Optional<ComponentDefinition<T>> fromFactoryMethod(final ComponentId factoryId,
            final Method factoryMethod, final ErrorCollector errorCollector) {
        final Class<T> componentType = (Class<T>) factoryMethod.getReturnType();
        final Builder<T> builder = ComponentDefinition.builder(componentType);

        final InjectableMember injectableConstructor = memberFactory.createFactoryMethod(factoryId,
                factoryMethod, errorCollector);
        final Optional<DestroyMethod> destroyMethod = memberFactory.createFactoryDestroyMethod(
                factoryMethod,
                errorCollector);
        final Optional<Scope> scope = scopeDecider.fromFactoryMethod(factoryMethod, errorCollector);

        destroyMethod.ifPresent(builder::destroyMethod);
        scope.ifPresent(builder::scope);

        return builder
                .injectableConstructor(injectableConstructor)
                .build();
    }
}
