package cz.cuni.amis.pogamut.base.utils.guice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.ScopeAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ScopeAnnotation
public @interface AgentTeamScoped {
}
