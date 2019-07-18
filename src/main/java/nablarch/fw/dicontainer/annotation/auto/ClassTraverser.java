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

public final class ClassTraverser {

    private final ClassLoader classLoader;
    private final Class<?> base;
    private final ClassFilter classFilter;

    public ClassTraverser(final ClassLoader classLoader, final Class<?> base,
            final ClassFilter classFilter) {
        this.classLoader = Objects.requireNonNull(classLoader);
        this.base = Objects.requireNonNull(base);
        this.classFilter = Objects.requireNonNull(classFilter);
    }

    public void traverse(final Consumer<Class<?>> consumer) {
        final CodeSource codeSource = base.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            //TODO ログ
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
            //TODO error
            throw new RuntimeException(e);
        }
    }

    private void traverseDirectory(final Consumer<Class<?>> consumer, final Path directory)
            throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
            stream.filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().endsWith(".class"))
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
                if (entry.isDirectory() == false && entry.getName().endsWith(".class")) {
                    loadClass(consumer, entry.getName());
                }
            }
        }
    }

    private void loadClass(final Consumer<Class<?>> consumer, final String classFileName) {
        try {
            final String className = classFileName.replace('/', '.').substring(0,
                    classFileName.length() - ".class".length());
            if (classFilter.select(className)) {
                final Class<?> clazz = Class.forName(className, false, classLoader);
                consumer.accept(clazz);
            }
        } catch (final ClassNotFoundException e) {
            //TODO error
            throw new RuntimeException(e);
        }
    }
}
