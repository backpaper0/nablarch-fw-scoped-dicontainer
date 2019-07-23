package nablarch.fw.dicontainer.component;

import nablarch.fw.dicontainer.component.impl.ContainerBuilderWrapper;
import nablarch.fw.dicontainer.component.impl.ErrorCollectorImpl;
import nablarch.fw.dicontainer.container.ContainerBuilder;
import nablarch.fw.dicontainer.exception.ContainerCreationException;
import nablarch.fw.dicontainer.exception.ContainerException;

/**
 * コンポーネント定義のバリデーションを行った結果、
 * 発生したバリデーションエラーを収集するインターフェース。
 *
 */
public interface ErrorCollector {

    /**
     * バリデーションエラーを追加する。
     * 
     * @param exception バリデーションエラー
     */
    void add(final ContainerException exception);

    /**
     * {@link #throwExceptionIfExistsError()}で無視をする例外クラスを設定する。
     * 
     * @param ignoreMe 無視される例外クラス
     */
    void ignore(final Class<? extends ContainerException> ignoreMe);

    /**
     * 追加されたバリデーションエラーがあれば、
     * それらを{@link ContainerCreationException}でラップしてスローする。
     * 
     * <p>バリデーションエラーがなければ何もしない。</p>
     * 
     */
    void throwExceptionIfExistsError();

    /**
     * 実装クラスのインスタンスを生成する。
     * 
     * @return 生成されたインスタンス
     */
    static ErrorCollector newInstance() {
        return new ErrorCollectorImpl();
    }

    /**
     * DIコンテナのビルダーをラップしたインスタンスを返す。
     * 
     * @param containerBuilder DIコンテナのビルダー
     * @return ラッパー
     */
    static ErrorCollector wrap(final ContainerBuilder<?> containerBuilder) {
        return new ContainerBuilderWrapper(containerBuilder);
    }
}
