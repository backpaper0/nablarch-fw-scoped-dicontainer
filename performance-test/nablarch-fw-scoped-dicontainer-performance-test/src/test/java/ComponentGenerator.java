import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.Scanner;

public class ComponentGenerator {

    public static void main(String[] args) throws IOException {
        final ComponentGenerator me = new ComponentGenerator();
        me.generateComponents();
    }

    private final String template;

    private final String dir;

    private final int numberOfComponents;

    ComponentGenerator(String dir, String template, int numberOfComponent) {
        this.template = template;
        this.dir = dir;
        this.numberOfComponents = numberOfComponent;
    }

    ComponentGenerator() throws IOException {
        this("src/main/java/" + "com/nablarch/framework/initialize/component",
                loadTemplate(),
                1000);
    }

    private static String loadTemplate() throws IOException {
        try (InputStream in = ComponentGenerator.class.getResourceAsStream("template.txt")) {
            Scanner s = new Scanner(in).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }

    void generateComponents() throws IOException {
        createDir(dir);
        for (int i = 0; i < numberOfComponents; i++) {
            generateComponentSourceFile(i);
        }
    }

    void createDir(String dir) throws IOException {
        try {
            Files.createDirectories(new File(dir).toPath());
        } catch (FileAlreadyExistsException ignored) {
            // nop
        }
    }

    void generateComponentSourceFile(int idx) throws IOException {
        String className = "Component" + idx;
        String fileName = className + ".java";
        File src = new File(new File(dir), fileName);

        try (final BufferedWriter w = Files.newBufferedWriter(src.toPath())) {
            String content = template.replace("$CLASS_NAME$", className);
            w.write(content);
        }
    }


}
