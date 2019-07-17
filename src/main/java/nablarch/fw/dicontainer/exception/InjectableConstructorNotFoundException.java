package nablarch.fw.dicontainer.exception;

import java.text.MessageFormat;

public class InjectableConstructorNotFoundException extends ContainerException {

    public InjectableConstructorNotFoundException(final Class<?> componentType) {
        super(MessageFormat.format("コンポーネント {0} にコンストラクタが見つかりません。", componentType.getName()));
        // TODO Auto-generated constructor stub
    }
}
