package org.machinemc.cogwheel.annotations;

import org.machinemc.cogwheel.serialization.Serializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface SerializeWith {

    Class<? extends Serializer<?>> value();

}
