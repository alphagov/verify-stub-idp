package uk.gov.ida.stub.idp.csrf;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@BindingAnnotation
@Target({TYPE, METHOD})
@Retention(RUNTIME)

public @interface CSRFCheckProtection {}
