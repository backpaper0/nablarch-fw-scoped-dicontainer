package nablarch.fw.dicontainer.component.factory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import nablarch.fw.dicontainer.component.ComponentId;
import nablarch.fw.dicontainer.component.DestroyMethod;
import nablarch.fw.dicontainer.component.ErrorCollector;
import nablarch.fw.dicontainer.component.FactoryMethod;
import nablarch.fw.dicontainer.component.InitMethod;
import nablarch.fw.dicontainer.component.InjectableMember;
import nablarch.fw.dicontainer.component.ObservesMethod;

/**
 * コンポーネント定義の構成要素を生成するファクトリ。
 *
 */
public interface MemberFactory {

    /**
     * コンストラクタでコンポーネントを生成する要素を作成する。
     * 
     * @param componentType コンポーネントのクラス
     * @param errorCollector バリデーションエラーを収集するクラス
     * @return コンポーネントを生成する要素
     */
    Optional<InjectableMember> createConstructor(Class<?> componentType,
            ErrorCollector errorCollector);

    /**
     * ファクトリメソッドでコンポーネントを生成する要素を作成する。
     * 
     * @param factoryId ファクトリーのID
     * @param factoryMethod ファクトリメソッド
     * @param errorCollector バリデーションエラーを収集するクラス
     * @return コンポーネントを生成する要素
     */
    InjectableMember createFactoryMethod(ComponentId factoryId,
            Method factoryMethod,
            ErrorCollector errorCollector);

    /**
     * インジェクション対象のフィールドとメソッドからなる要素を作成する。
     * 
     * @param componentType コンポーネントのクラス
     * @param errorCollector バリデーションエラーを収集するクラス
     * @return インジェクション対象のフィールドとメソッドからなる要素
     */
    List<InjectableMember> createFieldsAndMethods(Class<?> componentType,
            ErrorCollector errorCollector);

    /**
     * イベントをハンドリングするメソッドからなる要素を作成する。
     * 
     * @param componentType コンポーネントのクラス
     * @param errorCollector バリデーションエラーを収集するクラス
     * @return イベントをハンドリングするメソッドからなる要素
     */
    List<ObservesMethod> createObservesMethod(Class<?> componentType,
            ErrorCollector errorCollector);

    /**
     * 初期化メソッドからなる要素を作成する。
     * 
     * @param componentType コンポーネントのクラス
     * @param errorCollector バリデーションエラーを収集するクラス
     * @return 初期化メソッドからなる要素
     */
    Optional<InitMethod> createInitMethod(Class<?> componentType,
            ErrorCollector errorCollector);

    /**
     * 破棄メソッドからなる要素を作成する。
     * 
     * @param componentType コンポーネントのクラス
     * @param errorCollector バリデーションエラーを収集するクラス
     * @return 破棄メソッドからなる要素
     */
    Optional<DestroyMethod> createDestroyMethod(Class<?> componentType,
            ErrorCollector errorCollector);

    /**
     * ファクトリメソッドで定義されるコンポーネントの破棄メソッドからなる要素を作成する。
     * 
     * @param factoryMethod ファクトリメソッド
     * @param errorCollector バリデーションエラーを収集するクラス
     * @return ファクトリメソッドで定義されるコンポーネントの破棄メソッドからなる要素
     */
    Optional<DestroyMethod> createFactoryDestroyMethod(Method factoryMethod,
            ErrorCollector errorCollector);

    /**
     * ファクトリメソッドからなる要素を作成する。
     * 
     * @param id ID
     * @param componentType コンポーネントのクラス
     * @param componentDefinitionFactory コンポーネント定義のファクトリ
     * @param errorCollector バリデーションエラーを収集するクラス
     * @return ファクトリメソッドからなる要素
     */
    List<FactoryMethod> createFactoryMethods(ComponentId id,
            Class<?> componentType,
            ComponentDefinitionFactory componentDefinitionFactory,
            ErrorCollector errorCollector);

}