package org.mercurydb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface HgIndex {
    boolean ordered() default true;
}

