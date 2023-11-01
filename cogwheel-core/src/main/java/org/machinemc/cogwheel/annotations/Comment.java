package org.machinemc.cogwheel.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to comment the lines above the config node.
 * <p><b>Note: This annotation will be ignored if the data format used doesn't support comments</b></p>
 * <p>For example:</p>
 * <pre><code>
 * &#064;Comment({"The username used to log in", "This key is required"})
 * private String username = "user";
 *
 * &#064;Comment({"The password used to log in", "This key is required"})
 * private String password = "password";</code></pre>
 *
 * <p>Would look something like:</p>
 * <pre>
 * # The username used to log in
 * # This key is required
 * username: "user"
 *
 * # The password used to log in
 * # This key is required
 * password: "password"
 * </pre>
 *
 * @see Inline
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface Comment {

    String[] value();

    /**
     * An annotation used to include inline comments within a config node.
     * <p><b>Note: This annotation will be ignored if the data format used doesn't support inline comments</b></p>
     * <p>For example:</p>
     * <pre><code>
     * &#064;Comment.Inline("This is an inline comment")
     * private String field;</code></pre>
     *
     * <p>Would look something like:</p>
     * <pre>
     * field: "value" # This is an inline comment
     * </pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
    @interface Inline {

        String value();

    }

}
