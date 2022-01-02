package com.luna.synthesis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Comment("Didn't totally steal this from Oringo Client, move along.")
public @interface Comment {
    String value();
}
