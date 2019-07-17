package nablarch.fw.dicontainer.exception;

import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Set;

public class InjectableConstructorDuplicatedException extends ContainerException {

    public InjectableConstructorDuplicatedException(final Class<?> componentType,
            final Set<Constructor<?>> constructors) {
        super(MessageFormat.format("コンポーネント {0} のコンストラクタが重複しています。{1}", componentType.getName(),
                constructors));
        // TODO Auto-generated constructor stub
    }
}
