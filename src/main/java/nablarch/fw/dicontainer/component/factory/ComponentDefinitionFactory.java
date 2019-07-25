package nablarch.fw.dicontainer.component.factory;

import java.util.Optional;

import nablarch.fw.dicontainer.component.ComponentDefinition;
import nablarch.fw.dicontainer.component.ErrorCollector;

/**
 * コンポーネント定義のファクトリ。
 *
 */
public interface ComponentDefinitionFactory {

    /**
     * コンポーネントのクラスをもとにコンポーネント定義を生成する。
     * 
     * @param <T> コンポーネントの型
     * @param componentType コンポーネントのクラス
     * @param errorCollector バリデーションエラーを収集するクラス
     * @return コンポーネント定義
     */
    <T> Optional<ComponentDefinition<T>> fromComponentClass(Class<T> componentType,
            ErrorCollector errorCollector);
}