package nablarch.fw.dicontainer;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Objects;

public interface Qualifier extends Serializable {

    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    static Qualifier fromAnnotation(final Annotation annotation) {
        if (annotation.annotationType()
                .isAnnotationPresent(javax.inject.Qualifier.class) == false) {
            //TODO error
            throw new RuntimeException();
        }
        return new AnnotationQualifier(annotation);
    }

    final class AnnotationQualifier implements Qualifier {

        private final Annotation annotation;

        public AnnotationQualifier(final Annotation annotation) {
            this.annotation = Objects.requireNonNull(annotation);
        }

        @Override
        public int hashCode() {
            return annotation.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            } else if (obj == null) {
                return false;
            } else if (obj.getClass() != getClass()) {
                return false;
            }
            final AnnotationQualifier other = (AnnotationQualifier) obj;
            return annotation.equals(other.annotation);
        }

        @Override
        public String toString() {
            return annotation.toString();
        }
    }
}
