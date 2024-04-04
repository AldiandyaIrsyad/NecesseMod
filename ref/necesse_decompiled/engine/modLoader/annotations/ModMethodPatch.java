package necesse.engine.modLoader.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ModMethodPatch {
   Class<?> target();

   String name();

   Class<?>[] arguments();

   int priority() default 0;
}
