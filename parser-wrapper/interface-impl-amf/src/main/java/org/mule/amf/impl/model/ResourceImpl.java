/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.amf.impl.model;

import amf.client.model.domain.EndPoint;
import amf.client.model.domain.Operation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.mule.raml.interfaces.model.IAction;
import org.mule.raml.interfaces.model.IActionType;
import org.mule.raml.interfaces.model.IResource;
import org.mule.raml.interfaces.model.parameter.IParameter;

import static java.util.stream.Collectors.toMap;
import static org.mule.raml.interfaces.ParserUtils.resolveVersion;

public class ResourceImpl implements IResource {

  private EndPoint resource;
  private Map<IActionType, IAction> actions;
  private Map<String, IParameter> resolvedUriParameters;

  public ResourceImpl(final EndPoint resource) {
    this.resource = resource;
  }

  @Override
  public String getRelativeUri() {
    return resource.path().value();
  }

  @Override
  public String getUri() {
    return resource.path().value();
  }

  @Override
  public String getResolvedUri(String version) {
    return resolveVersion(getUri(), version);
  }


  @Override
  public String getParentUri() {
    return getUri().substring(0, getUri().length() - getRelativeUri().length());
  }

  @Override
  public IAction getAction(String name) {
    return getActions().get(getActionKey(name));
  }

  @Override
  public Map<IActionType, IAction> getActions() {
    if (actions == null) {
      actions = loadActions(resource);
    }

    return actions;
  }

  private static Map<IActionType, IAction> loadActions(final EndPoint endPoint) {
    Map<IActionType, IAction> map = new LinkedHashMap<>();
    for (Operation operation : endPoint.operations()) {
      map.put(getActionKey(operation.method().value()), new ActionImpl(endPoint, operation));
    }
    return map;
  }

  private static IActionType getActionKey(String method) {
    return IActionType.valueOf(method.toUpperCase());
  }

  @Override
  public Map<String, IResource> getResources() {
    return Collections.emptyMap();
  }

  @Override
  public String getDisplayName() {
    return resource.path().value();
  }

  @Override
  public Map<String, IParameter> getResolvedUriParameters() {
    if (resolvedUriParameters == null) {
      resolvedUriParameters = loadResolvedUriParameters(resource);
    }

    return resolvedUriParameters;
  }

  private static Map<String, IParameter> loadResolvedUriParameters(final EndPoint resource) {
    return resource.parameters().stream().collect(toMap(p -> p.name().value(), ParameterImpl::new));
  }

  @Override
  public void setParentUri(String parentUri) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, List<IParameter>> getBaseUriParameters() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void cleanBaseUriParameters() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return getUri();
  }
}
