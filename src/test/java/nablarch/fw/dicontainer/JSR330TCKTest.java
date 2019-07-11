package nablarch.fw.dicontainer;

import javax.inject.Named;

import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.Convertible;
import org.atinject.tck.auto.Drivers;
import org.atinject.tck.auto.DriversSeat;
import org.atinject.tck.auto.FuelTank;
import org.atinject.tck.auto.Seat;
import org.atinject.tck.auto.Tire;
import org.atinject.tck.auto.V8Engine;
import org.atinject.tck.auto.accessories.Cupholder;
import org.atinject.tck.auto.accessories.SpareTire;

import junit.framework.Test;

public class JSR330TCKTest {

    @Drivers
    Object drivers;
    @Named("spare")
    Object spare;

    public static Test suite() throws Exception {
        final Container container = new AnnotationContainerBuilder()
                //org.atinject.tck.auto.Car is implemented by Convertible.
                .register(Convertible.class)
                //@Drivers Seat is implemented by DriversSeat.
                .register(DriversSeat.class, drivers())
                //Seat is implemented by Seat itself, and Tire by Tire itself (not subclasses).
                .register(Seat.class).register(Tire.class)
                //Engine is implemented by V8Engine.
                .register(V8Engine.class)
                //@Named("spare") Tire is implemented by SpareTire.
                .register(SpareTire.class, spare())
                //The following classes may also be injected directly: Cupholder, SpareTire, and FuelTank.
                .register(Cupholder.class).register(SpareTire.class).register(FuelTank.class)
                .build();

        final Car car = container.getComponent(Car.class);

        return Tck.testsFor(car, false, true);
    }

    private static Drivers drivers() throws Exception {
        return JSR330TCKTest.class.getDeclaredField("drivers").getAnnotation(Drivers.class);
    }

    private static Named spare() throws Exception {
        return JSR330TCKTest.class.getDeclaredField("spare").getAnnotation(Named.class);
    }
}
