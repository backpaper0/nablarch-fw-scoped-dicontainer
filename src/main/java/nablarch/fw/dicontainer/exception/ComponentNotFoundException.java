package nablarch.fw.dicontainer.exception;

import java.text.MessageFormat;

import nablarch.fw.dicontainer.ComponentKey;

public class ComponentNotFoundException extends ContainerException {

    public ComponentNotFoundException(final ComponentKey<?> key) {
        super(MessageFormat.format("コンポーネント {0} が見つかりません", key));
        // TODO Auto-generated constructor stub
    }
}
