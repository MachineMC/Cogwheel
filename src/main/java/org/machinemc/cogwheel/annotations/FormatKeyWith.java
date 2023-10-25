package org.machinemc.cogwheel.annotations;

import org.machinemc.cogwheel.keyformatter.KeyFormatter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to specify a custom key formatter for fields or record components when serializing
 * and deserializing configurations. The key formatter determines how field or record component names are
 * formatted when generating or reading configuration keys.
 * <p>
 * To use this annotation, apply it to fields or record components within your configuration class or record.
 * The annotation takes a single argument, a reference to a class that implements the {@link KeyFormatter} interface.
 * This class will be used to format the keys associated with the annotated field or record component.
 * </p>
 * <p>For example:</p>
 * <pre><code>
 * &#064;FormatKeyWith(CustomKeyFormatter.class)
 * private String myCustomField;
 * </code></pre>
 * <p>In this example, the 'myCustomField' is annotated with {@code @FormatKeyWith} and specifies the
 * {@code CustomKeyFormatter} class to format the key for this field.</p>
 *
 * @see KeyFormatter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface FormatKeyWith {

    Class<? extends KeyFormatter> value();

}
