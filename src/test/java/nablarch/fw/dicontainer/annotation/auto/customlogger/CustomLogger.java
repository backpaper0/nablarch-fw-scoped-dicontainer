package nablarch.fw.dicontainer.annotation.auto.customlogger;

import javax.inject.Singleton;

import nablarch.core.log.Logger;

@Singleton
public class CustomLogger {

    private Logger logger;

    public Logger getLogger() {
        return logger;
    }
}
