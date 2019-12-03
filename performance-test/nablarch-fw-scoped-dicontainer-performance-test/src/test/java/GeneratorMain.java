import java.io.IOException;

/**
 * コンポーネントにインジェクションを行わない設定で、ソースコードを出力するメインクラス。
 *
 * @see ComponentGenerator
 */
public class GeneratorMain {

    public static void main(String[] args) throws IOException {
        String dir = "src/main/java/" + "com/nablarch/framework/initialize/component";
        String templateName = "template.txt";
        int numberOfComponent = 1000;
        final ComponentGenerator me = new ComponentGenerator(dir, templateName, numberOfComponent);
        me.generateComponents();
    }


}
