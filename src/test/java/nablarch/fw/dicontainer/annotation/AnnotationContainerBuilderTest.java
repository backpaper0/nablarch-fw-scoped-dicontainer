package nablarch.fw.dicontainer.annotation;

import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder.Builder;
import nablarch.fw.dicontainer.component.ErrorCollector;
import nablarch.fw.dicontainer.component.factory.ComponentDefinitionFactory;
import nablarch.fw.dicontainer.component.factory.ComponentKeyFactory;
import nablarch.fw.dicontainer.component.factory.MemberFactory;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.scope.Scope;
import nablarch.fw.dicontainer.scope.ScopeDecider;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;

public class AnnotationContainerBuilderTest {

    private ComponentKeyFactory componentKeyFactory = new AnnotationComponentKeyFactory();
    private ScopeDecider scopeDecider = AnnotationScopeDecider.builder().build();
    private MemberFactory memberFactory = new AnnotationMemberFactory(new DefaultInjectionComponentResolverFactory());
    private ComponentDefinitionFactory componentDefinitionFactory = new AnnotationComponentDefinitionFactory(memberFactory, scopeDecider);

    @Test
    public void testInternalBuilder() {
        Builder sut = AnnotationContainerBuilder.builder();
        sut.componentKeyFactory(componentKeyFactory);
        sut.scopeDecider(scopeDecider);
        sut.componentDefinitionFactory(componentDefinitionFactory);
        sut.memberFactory(memberFactory);
        sut.eagerLoad(false);
        AnnotationContainerBuilder builder = sut.build();
        assertNotNull(builder);
    }

    /**
     * {@link ScopeDecider}の実体が{@link AnnotationScopeDecider}以外のとき、
     * eagarLoadを設定しても、何もおこらないこと。
     * （ログ出力のみ）
     */
    @Test
    public void testEagerLoadNop() {
        Builder sut = AnnotationContainerBuilder.builder();
        sut.scopeDecider(new MockScopeDecider());
        sut.eagerLoad(true);
    }

    private static class MockScopeDecider implements ScopeDecider {

        @Override
        public Optional<Scope> fromComponentClass(Class<?> componentType, ErrorCollector errorCollector) {
            return Optional.empty();
        }

        @Override
        public void registerScopes(ContainerBuilder<?> builder, MemberFactory memberFactory) {
            // NOP
        }
    }
}