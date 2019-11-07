package nablarch.fw.dicontainer.annotation.auto;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Stream;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.fw.dicontainer.exception.ClassTraversingException;

/**
 * ディレクトリトラバーサルを行って見つけたクラスに特定の処理を行うクラス。
 *
 */
public class ClassTraverser {

    /**
     * クラスファイルの拡張子
     */
    private static final String CLASSFILE_SUFFIX = ".class";
    /**
     * ロガー
     */
    private static final Logger LOGGER = LoggerManager.get(ClassTraverser.class);
    /**
     * 見つけたクラスをロードするためのクラスローダー
     */
    private final ClassLoader classLoader;
    /**
     * ディレクトリトラバーサルの起点となるクラス
     */
    private final Class<?> baseClass;
    /**
     * フィルター
     */
    private final ClassFilter classFilter;

    /**
     * インスタンスを生成する。
     * 
     * @param classLoader 見つけたクラスをロードするためのクラスローダー
     * @param baseClass ディレクトリトラバーサルの起点となるクラス
     * @param classFilter フィルター
     */
    public ClassTraverser(final ClassLoader classLoader, final Class<?> baseClass,
            final ClassFilter classFilter) {
        this.classLoader = Objects.requireNonNull(classLoader);
        this.baseClass = Objects.requireNonNull(baseClass);
        this.classFilter = Objects.requireNonNull(classFilter);
    }

    /**
     * ディレクトリトラバーサルを行う。
     * 
     * @param consumer 見つかったクラスに適用する処理
     */
    public void traverse(final Consumer<Class<?>> consumer) {
        final CodeSource codeSource = baseClass.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            LOGGER.logDebug("Can not traverse configured by [" + baseClass.getName() + "]");
            return;
        }

        try {
            traverse(consumer, codeSource);
        } catch (final URISyntaxException | IOException e) {
            throw new ClassTraversingException(e);
        }
    }

    /**
     * ディレクトリトラバーサルを行う。
     * @param consumer 見つかったクラスに適用する処理
     * @param codeSource {@link CodeSource}
     * @throws URISyntaxException {@link CodeSource}から{@link URI}への変換が失敗した場合
     * @throws IOException 予期しない入出力例外
     */
    void traverse(Consumer<Class<?>> consumer, CodeSource codeSource) throws URISyntaxException, IOException {
        final URI location = codeSource.getLocation().toURI();

        final Path fileOrDir = Paths.get(location);
        final String baseClassPackage = getBaseClassPackage();
        LOGGER.logInfo("base class package = [" + baseClassPackage + "]");
        if (Files.isDirectory(fileOrDir)) {
            traverseDirectory(consumer, fileOrDir, baseClassPackage);
        } else {
            traverseJarFile(consumer, fileOrDir, baseClassPackage);
        }
    }

    /**
     * ディレクトリトラバーサルを行う。
     *
     * @param consumer 見つかったクラスに適用する処理
     * @param directory 対象ディレクトリ
     */
    private void traverseDirectory(final Consumer<Class<?>> consumer, final Path directory, String baseClassPackage)
            throws IOException {

        Stream<Path> stream = Files.walk(directory);
        try {
            stream.filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().endsWith(CLASSFILE_SUFFIX))
                    .forEach(file -> {
                        loadClass(consumer, directory.relativize(file).toString(),
                                baseClassPackage);
                    });
        } finally {
            stream.close();
        }
    }

    /**
     * jarファイルのトラバースを行う。
     * @param consumer 見つかったクラスに適用する処理
     * @param jarFile 対象jarファイル
     * @throws IOException 予期しない入出力例外
     */
    private void traverseJarFile(final Consumer<Class<?>> consumer, final Path jarFile, String baseClassPackage)
            throws IOException {

        JarInputStream in = new JarInputStream(Files.newInputStream(jarFile));
        try {
            JarEntry entry = null;
            while (null != (entry = in.getNextJarEntry())) {
                if (entry.isDirectory() == false && entry.getName().endsWith(CLASSFILE_SUFFIX)) {
                    loadClass(consumer, entry.getName(), baseClassPackage);
                }
            }
        } finally {
            in.close();
        }
    }

    /**
     * 起点クラスのパッケージ名を取得する。
     * @return パッケージ名
     */
    private String getBaseClassPackage() {
        Package p = baseClass.getPackage();
        return p == null ? "" : p.getName();
    }

    /**
     * クラスをロードする。
     *
     * @param consumer 見つかったクラスに適用する処理
     * @param classFileName クラスファイル名
     * @param baseClassPackage 起点クラスのパッケージ
     */
    private void loadClass(final Consumer<Class<?>> consumer, final String classFileName,
            final String baseClassPackage) {

        final String className = classFileName.replace('/', '.').replace('\\', '.').substring(0,
                classFileName.length() - CLASSFILE_SUFFIX.length());
        if (className.startsWith(baseClassPackage) && classFilter.select(className)) {
            final Class<?> clazz = forName(className, classLoader);
            consumer.accept(clazz);
        }
    }

    /**
     * クラスの完全修飾名から{@link Class}を取得する。
     * @param className クラス名
     * @param classLoader クラスローダー
     * @return {@link Class}
     */
    static Class<?> forName(String className, ClassLoader classLoader) {
        try {
            return Class.forName(className, false, classLoader);
        } catch (final ClassNotFoundException e) {
            throw new ClassTraversingException(e);
        }
    }
}
