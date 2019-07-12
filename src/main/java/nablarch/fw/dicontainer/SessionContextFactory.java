package nablarch.fw.dicontainer;

public interface SessionContextFactory {

    SessionContext create(Object request);
}
