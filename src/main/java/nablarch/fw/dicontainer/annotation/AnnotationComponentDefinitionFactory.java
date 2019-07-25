package nablarch.fw.dicontainer.annotation;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentDefinition.Builder;
import nablarch.fw.dicontainer.component.DestroyMethod;
import nablarch.fw.dicontainer.component.ErrorCollector;
import nablarch.fw.dicontainer.component.InitMethod;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.component.ObservesMethod;
import nablarch.fw.dicontainer.component.factory.ComponentDefinitionFactory;
import nablarch.fw.dicontainer.component.factory.MemberFactory;
import nablarch.fw.dicontainer.scope.Scope;
import nablarch.fw.dicontainer.scope.ScopeDecider;

/**
 * アノテーションをもとにコンポーネント定義を生成するファクトリ。
 *
 */
public final class AnnotationComponentDefinitionFactory implements ComponentDefinitionFactory {

    /**
     * コンポーネント定義の構成要素のファクトリ
     */
    private final MemberFactory memberFactory;
    /**
     * スコープを決定するクラス
     */
    private final ScopeDecider scopeDecider;

    /**
     * インスタンスを生成する。
     * 
     * @param memberFactory コンポーネント定義の構成要素のファクトリ
     * @param scopeDecider スコープを決定するクラス
     */
    public AnnotationComponentDefinitionFactory(final MemberFactory memberFactory,
            final ScopeDecider scopeDecider) {
        this.memberFactory = Objects.requireNonNull(memberFactory);
        this.scopeDecider = Objects.requireNonNull(scopeDecider);
    }

    @Override
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
        final Optional<Scope> scope = scopeDecider.fromComponentClass(componentType,
                errorCollector);

        injectableConstructor.ifPresent(builder::injectableConstructor);
        initMethod.ifPresent(builder::initMethod);
        destroyMethod.ifPresent(builder::destroyMethod);
        scope.ifPresent(builder::scope);

        return builder
                .injectableMembers(injectableMembers)
                .observesMethods(observesMethods)
                .build();
    }
}
