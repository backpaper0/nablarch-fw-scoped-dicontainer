package com.nablarch.framework.initialize;

import nablarch.fw.dicontainer.Container;
import nablarch.fw.dicontainer.annotation.AnnotationContainerBuilder;
import nablarch.fw.dicontainer.annotation.auto.AnnotationAutoContainerFactory;
import nablarch.fw.dicontainer.annotation.auto.DefaultComponentPredicate;
import nablarch.fw.dicontainer.annotation.auto.TraversalConfig;

import java.util.Collections;

public class InitializationTester {

    public static void main(String[] args) {
        new InitializationTester().createContainer();
    }

    public void createContainer() {
        Iterable<TraversalConfig> traversalConfigs = Collections.singleton(new InitializationTraverseConfig());
        AnnotationContainerBuilder containerBuilder = AnnotationContainerBuilder.createDefault();
        AnnotationAutoContainerFactory factory = new AnnotationAutoContainerFactory(
                containerBuilder, traversalConfigs, new DefaultComponentPredicate()
        );
        Container container = factory.create();
    }

}
