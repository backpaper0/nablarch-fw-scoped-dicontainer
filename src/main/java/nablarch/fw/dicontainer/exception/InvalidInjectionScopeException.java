package nablarch.fw.dicontainer.exception;

public class InvalidInjectionScopeException extends ContainerException {

    public InvalidInjectionScopeException() {
        //TODO {componentClass}の{methodName}：{key}に関連付けられたコンポーネントのスコープがDI対象のコンポーネントのスコープよりも狭いです。
    }
}
