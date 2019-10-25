package nablarch.fw.dicontainer.container;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.component.AliasMapping;
import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ComponentDefinitionRepository;
import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.ComponentKey;
import nablarch.fw.dicontainer.event.ContainerDestroy;
import nablarch.fw.dicontainer.event.EventTrigger;
import nablarch.fw.dicontainer.exception.ComponentDuplicatedException;
import nablarch.fw.dicontainer.exception.ComponentNotFoundException;

/**
 * {@link Container}のデフォルト実装クラス。
 */
public final class DefaultContainer implements Container, EventTrigger {
    /** ロガー */
    private static final Logger logger = LoggerManager.get(DefaultContainer.class);

    /** コンポーネント定義のリポジトリ */
    private final ComponentDefinitionRepository definitions;

    /** エイリアスキーと検索キーのマッピング */
    private final AliasMapping aliasMapping;

    /**
     * コンストラクタ。
     * @param definitionsMap コンポーネント定義のリポジトリ
     * @param aliasesMap エイリアスキーと検索キーのマッピング
     */
    public DefaultContainer(final ComponentDefinitionRepository definitionsMap,
            final AliasMapping aliasesMap) {
        this.definitions = Objects.requireNonNull(definitionsMap);
        this.aliasMapping = Objects.requireNonNull(aliasesMap);
    }



    @Override
    public <T> T getComponent(final ComponentKey<T> key) {
        ComponentDefinition<T> definition = definitions.find(key);
        if (definition != null) {
            return definition.getComponent(this);
        }
        final Set<ComponentKey<?>> alterKeys = aliasMapping.find(key.asAliasKey());
        if (alterKeys.isEmpty()) {
            throw new ComponentNotFoundException("key=" + key);
        } else if (alterKeys.size() > 1) {
            final String message = alterKeys.stream().map(Objects::toString)
                    .collect(Collectors.joining(", ", "keys=", ""));
            throw new ComponentDuplicatedException(message);
        }
        final ComponentKey<T> alterKey = (ComponentKey<T>) alterKeys.iterator().next();
        definition = definitions.find(alterKey);
        return definition.getComponent(this);
    }

    @Override
    public <T> T getComponent(final Class<T> key) {
        return getComponent(new ComponentKey<>(key));
    }

    @Override
    public <T> T getComponent(final Class<T> key, final Annotation... qualifiers) {
        return getComponent(new ComponentKey<>(key, qualifiers));
    }

    @Override
    public <T> Set<T> getComponents(final Class<T> key) {
        final ComponentKey<T> exactKey = new ComponentKey<>(key);
        final Set<ComponentKey<?>> alterKeys = aliasMapping.find(exactKey.asAliasKey());
        final Set<ComponentKey<?>> keys = new HashSet<>(alterKeys.size() + 1);
        keys.add(exactKey);
        keys.addAll(alterKeys);
        final Set<?> components = keys.stream().map(definitions::find)
                .filter(Objects::nonNull)
                .map(a -> a.getComponent(this))
                .collect(Collectors.toSet());
        return (Set<T>) components;
    }

    @Override
    public void fire(final Object event) {
        logger.logDebug("Fire event [" + event + "]");
        definitions.fire(this, event);
    }

    @Override
    public void destroy() {
        fire(new ContainerDestroy());
    }
}
