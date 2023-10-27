package org.machinemc.cogwheel.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to indicate that a field or record component in a configuration class or record is optional.
 * Fields or record components marked with this annotation are not required to have a corresponding value
 * in the configuration. When deserializing the configuration, the absence of a value for an optional field or
 * record component will not result in an error.
 * <p>
 * To use this annotation, apply it to fields or record components within your configuration class or record.
 * The presence of this annotation indicates that the field or record component is optional.
 * </p>
 * <p>For example:</p>
 * <pre><code>
 * &#064;Optional
 * private String optionalField;
 * </code></pre>
 * <p>In this example, the 'optionalField' is annotated with {@code @Optional}, signifying that it's an optional field.
 * The absence of a value for 'optionalField' in the configuration will not raise an error during deserialization.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface Optional {
}
