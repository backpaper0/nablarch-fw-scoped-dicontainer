package nablarch.fw.dicontainer.component.factory;

import java.util.List;
import java.util.Optional;

import nablarch.fw.dicontainer.component.DestroyMethod;
import nablarch.fw.dicontainer.component.ErrorCollector;
import nablarch.fw.dicontainer.component.InitMethod;
import nablarch.fw.dicontainer.component.InjectableConstructor;
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
    Optional<InjectableConstructor> createConstructor(Class<?> componentType,
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
}