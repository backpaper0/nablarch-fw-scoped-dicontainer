import java.io.IOException;

public class GeneratorMain {

    public static void main(String[] args) throws IOException {
        String dir = "src/main/java/" + "com/nablarch/framework/initialize/component";
        String templateName = "template.txt";
        int numberOfComponent = 1000;
        final ComponentGenerator me = new ComponentGenerator(dir, templateName, numberOfComponent);
        me.generateComponents();
    }


}
