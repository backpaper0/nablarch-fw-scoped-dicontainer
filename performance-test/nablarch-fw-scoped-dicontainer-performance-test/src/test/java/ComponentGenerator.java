import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * コンポーネントのソースコードを出力するクラス。
 *
 * 本クラスでコンポーネントを生成し、その状態でDIコンテナの初期化時間を計測する。
 * 必要な設定については、{@link #ComponentGenerator(String, String, int)}を参照。
 */
class ComponentGenerator {

    private final String template;

    private final String dir;

    private final int numberOfComponents;

    /**
     * コンストラクタ。
     * @param dir ファイル出力先ディレクトリ
     * @param templateName テンプレート名（クラスパスからロードする）
     * @param numberOfComponent 出力するコンポーネント数
     * @throws IOException 予期しない入出力例外
     */
    ComponentGenerator(String dir, String templateName, int numberOfComponent) throws IOException {
        this.template = loadTemplate(templateName);
        this.dir = dir;
        this.numberOfComponents = numberOfComponent;
    }

    private static String loadTemplate(String templateName) throws IOException {
        try (InputStream in = ComponentGenerator.class.getResourceAsStream(templateName)) {
            if (in == null) {
                throw new IllegalArgumentException(templateName);
            }
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
