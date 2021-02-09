package com.dustinredmond.fxtrayicon.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate public API for FXTrayIcon
 * This annotation is not retained at runtime, and
 * is only to ease developmental determination of
 * publicly available API.
 * <p>Some IDEs allow disabling warnings for unused
 * methods with a certain annotation. This is the primary
 * purpose for this annotation.</p>
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
public @interface API {
    /**
     * Optionally, describes the version in which the API was added.
     * @return The version in which the API was added or an empty String
     *         if one is not provided.
     */
    String version() default "";
}
