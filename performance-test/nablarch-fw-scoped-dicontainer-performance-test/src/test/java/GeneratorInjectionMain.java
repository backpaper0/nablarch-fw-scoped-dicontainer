import java.io.IOException;

/**
 * コンポーネントにインジェクションを行う設定で、ソースコードを出力するメインクラス。
 *
 * @see ComponentGenerator
 */
public class GeneratorInjectionMain {

    public static void main(String[] args) throws IOException {
        String dir = "src/main/java/" + "com/nablarch/framework/initialize/component";
        String templateName = "template-injection.txt";
        int numberOfComponent = 10000;
        final ComponentGenerator me = new ComponentGenerator(dir, templateName, numberOfComponent);
        me.generateComponents();
    }


}
