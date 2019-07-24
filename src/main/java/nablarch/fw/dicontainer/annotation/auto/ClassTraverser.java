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
public final class ClassTraverser {

    /**
     * クラスファイルの拡張子
     */
    private static final String CLASSFILE_SUFFIX = ".class";
    /**
     * ロガー
     */
    private static final Logger logger = LoggerManager.get(ClassTraverser.class);
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
            if (logger.isDebugEnabled()) {
                logger.logDebug("Can not traverse configured by [" + baseClass.getName() + "]");
            }
            return;
        }

        try {
            final URI location = codeSource.getLocation().toURI();

            final Path fileOrDir = Paths.get(location);

            if (Files.isDirectory(fileOrDir)) {
                traverseDirectory(consumer, fileOrDir);
            } else {
                traverseJarFile(consumer, fileOrDir);
            }

        } catch (final URISyntaxException | IOException e) {
            throw new ClassTraversingException(e);
        }
    }

    private void traverseDirectory(final Consumer<Class<?>> consumer, final Path directory)
            throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
            stream.filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().endsWith(CLASSFILE_SUFFIX))
                    .forEach(file -> {
                        loadClass(consumer, directory.relativize(file).toString());
                    });
        }
    }

    private void traverseJarFile(final Consumer<Class<?>> consumer, final Path jarFile)
            throws IOException {
        try (JarInputStream in = new JarInputStream(Files.newInputStream(jarFile))) {
            JarEntry entry = null;
            while (null != (entry = in.getNextJarEntry())) {
                if (entry.isDirectory() == false && entry.getName().endsWith(CLASSFILE_SUFFIX)) {
                    loadClass(consumer, entry.getName());
                }
            }
        }
    }

    private void loadClass(final Consumer<Class<?>> consumer, final String classFileName) {
        try {
            final String className = classFileName.replace('/', '.').substring(0,
                    classFileName.length() - CLASSFILE_SUFFIX.length());
            if (classFilter.select(className)) {
                final Class<?> clazz = Class.forName(className, false, classLoader);
                consumer.accept(clazz);
            }
        } catch (final ClassNotFoundException e) {
            throw new ClassTraversingException(e);
        }
    }
}
