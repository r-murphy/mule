/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.internal.dsl.model;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.mule.runtime.internal.dsl.DslConstants.NAME_ATTRIBUTE_NAME;

import org.mule.runtime.api.component.location.ComponentLocation;
import org.mule.runtime.ast.api.ComponentAst;
import org.mule.runtime.ast.api.ParameterAst;
import org.mule.runtime.ast.internal.ReferenceParameterValueAst;
import org.mule.runtime.ast.internal.SimpleParameterValueAst;
import org.mule.runtime.config.internal.model.ComponentModel;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;

/**
 * Specialization of {@link ComponentModel} that keeps references to a Spring bean specification.
 *
 * @since 4.0
 */
public class SpringComponentModel extends ComponentModel implements ComponentAst {

  // TODO MULE-9688 Remove this attributes since should not be part of this class. This class should be immutable.
  private BeanReference beanReference;
  private BeanDefinition beanDefinition;

  /**
   * @param beanDefinition the {@code BeanDefinition} created based on the {@code ComponentModel} values.
   */
  public void setBeanDefinition(BeanDefinition beanDefinition) {
    this.beanDefinition = beanDefinition;
  }

  /**
   * @return the {@code BeanDefinition} created based on the {@code ComponentModel} values.
   */
  public BeanDefinition getBeanDefinition() {
    return beanDefinition;
  }

  /**
   * @param beanReference the {@code BeanReference} that represents this object.
   */
  public void setBeanReference(BeanReference beanReference) {
    this.beanReference = beanReference;
  }

  /**
   * @return the {@code BeanReference} that represents this object.
   */
  public BeanReference getBeanReference() {
    return beanReference;
  }

  @Override
  public ComponentLocation getLocation() {
    return getComponentLocation();
  }

  @Override
  public Optional<String> getName() {
    return ofNullable(getParameters().get(NAME_ATTRIBUTE_NAME));
  }

  @Override
  public Optional<String> getRawParameterValue(String paramName) {
    return ofNullable(getParameters().get(paramName));
  }

  @Override
  public <T, V> Optional<T> mapParameter(String paramName, Function<V, T> mapper) {
    final String value = getParameters().get(paramName);

    if (value == null) {
      return empty();
      // } else if ("resource".equals(paramName)) {
      // return of(new ResourceParameterValueAst(value).map((Function<URI, T>) mapper));
    } else if ("config-ref".equals(paramName)) {
      return of(new ReferenceParameterValueAst(null).map((Function<ComponentAst, T>) mapper));
    } else {
      return of(new SimpleParameterValueAst(value).map((Function<String, T>) mapper));
    }
  }

  @Override
  public <T> Optional<T> mapSimpleParameter(String paramName, Function<String, T> mapper) {
    final String value = getParameters().get(paramName);

    if (value == null) {
      return empty();
      // } else if ("resource".equals(paramName)) {
      // return of(new ResourceParameterValueAst(value).map((Function<URI, T>) mapper));
    } else {
      return of(new SimpleParameterValueAst(value).map(mapper));
    }
  }

  @Override
  public Set<ParameterAst> getParameters2() {
    return Collections.emptySet();
  }

}
