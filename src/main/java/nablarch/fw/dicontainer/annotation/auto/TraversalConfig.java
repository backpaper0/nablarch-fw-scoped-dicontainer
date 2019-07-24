package nablarch.fw.dicontainer.annotation.auto;

import java.util.Collections;
import java.util.Set;

/**
 * ディレクトリトラバーサルの設定。
 *
 */
public interface TraversalConfig {

    /**
     * 処理に含めるパターンを取得する。
     * パターンは正規表現で構成される。
     * パターンがなければ全て含める。
     * 
     * @return 処理に含めるパターン
     */
    default Set<String> includes() {
        return Collections.emptySet();
    }

    /**
     * 処理に含めないパターンを取得する。
     * パターンは正規表現で構成される。
     * パターンがなければ全て含める。
     * 
     * @return 処理に含めないパターン
     */
    default Set<String> excludes() {
        return Collections.emptySet();
    }
}
