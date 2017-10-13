package cz.cuni.amis.pogamut.base.component.controller;

import java.util.HashSet;
import java.util.Set;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.token.IToken;

/**
 * @author Jimmy
 */
public class ComponentDependencies {
	
	Set<IToken> tokenDependencies = new HashSet<IToken>();

	private ComponentDependencyType type;
	
	public ComponentDependencies() {
		this(ComponentDependencyType.STARTS_WITH);
	}
	
	public ComponentDependencies(ComponentDependencyType dependencyType) {
		this.type = dependencyType;
		NullCheck.check(this.type, "dependencyType");
	}
	
	/**
	 * @param dependencyType
	 * @param dependencies might be {@link IToken} implementations or {@link IComponent} implementations
	 */
	public ComponentDependencies(ComponentDependencyType dependencyType, Object... dependencies) {
		this.type = dependencyType;
		NullCheck.check(this.type, "dependencyType");
		NullCheck.check(dependencies, "dependencies");
		for (int i = 0; i < dependencies.length; ++i) {
			NullCheck.check(dependencies[i], "dependencies[" + i + "]");
			if (dependencies[i] instanceof IToken) {
				add((IToken)dependencies[i]);
			} else
			if (dependencies[i] instanceof IComponent) {
				if (((IComponent)dependencies[i]).getComponentId() == null) {
					throw new IllegalArgumentException("dependencies[" + i + "].getComponentId() is null");
				}
				add((IComponent)dependencies[i]);
			} else {
				throw new IllegalArgumentException("dependencies[" + i + "] is not IToken nor IComponent");
			}
		}
	}
	
	public ComponentDependencies(ComponentDependencies dependencies) {
		NullCheck.check(dependencies, "dependencies");
		this.type = dependencies.type;
		for (IToken token : dependencies.tokenDependencies) {
			tokenDependencies.add(token);
		}
	}
		
	public ComponentDependencies add(IToken token) {
		NullCheck.check(token, "token");
		tokenDependencies.add(token);
		return this;
	}
	
	public ComponentDependencies add(IComponent component) {
		NullCheck.check(component, "component");
		NullCheck.check(component.getComponentId(), "'component's id'");
		tokenDependencies.add(component.getComponentId());
		return this;
	}
	
	public IToken[] getDependencies() {
		return tokenDependencies.toArray(new IToken[0]);
	}
	
	public boolean isDependency(IToken token) {
		return tokenDependencies.contains(token);
	}
	
	public boolean isDependency(IComponent component) {
		return isDependency(component.getComponentId());
	}
	
	public ComponentDependencyType getType() {
		return type;
	}
	
	public int getCount() {
		return tokenDependencies.size();
	}
	
	public String toString() {
		return "ComponentDependencies";
	}

}
