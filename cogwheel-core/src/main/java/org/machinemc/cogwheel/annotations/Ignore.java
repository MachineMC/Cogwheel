package org.machinemc.cogwheel.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to instruct the serialization process to ignore a field or record component,
 * preventing it from being included in the resulting configuration.
 * <p>
 * Fields marked with this annotation will not be serialized to the configuration,
 * allowing you to exclude specific fields from the output.
 * </p>
 * <p>For example:</p>
 * <pre><code>
 * public class AppConfig implements Configuration {
 *
 *     private String username = "user";
 *     private String password = "password";
 *
 *     &#064;Ignore
 *     private boolean debugMode = false;
 *
 * }</code></pre>
 * <p>In this example, the 'debugMode' field is marked with <code>&#064;Ignore</code> and will be ignored
 * when generating the configuration file, while 'username' and 'password' will be included in the output.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
public @interface Ignore {
}
