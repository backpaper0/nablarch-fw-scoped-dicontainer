package com.nablarch.framework;

import nablarch.core.log.basic.LogContext;
import nablarch.core.log.basic.LogWriter;
import nablarch.core.log.basic.ObjectSettings;

public class NopLogWriter implements LogWriter {
    @Override
    public void initialize(ObjectSettings objectSettings) {

    }

    @Override
    public void terminate() {

    }

    @Override
    public void write(LogContext logContext) {

    }
}
