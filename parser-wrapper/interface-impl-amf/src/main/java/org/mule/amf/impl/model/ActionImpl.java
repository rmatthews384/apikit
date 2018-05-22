/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.amf.impl.model;

import amf.client.model.domain.EndPoint;
import amf.client.model.domain.Operation;
import amf.client.model.domain.Request;
import amf.client.model.domain.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.mule.raml.interfaces.model.IAction;
import org.mule.raml.interfaces.model.IActionType;
import org.mule.raml.interfaces.model.IMimeType;
import org.mule.raml.interfaces.model.IResource;
import org.mule.raml.interfaces.model.IResponse;
import org.mule.raml.interfaces.model.ISecurityReference;
import org.mule.raml.interfaces.model.parameter.IParameter;

import static java.util.Collections.emptyMap;

public class ActionImpl implements IAction {

  private final EndPoint endPoint;
  private final Operation operation;
  private Map<String, IMimeType> bodies;
  private Map<String, IResponse> responses;
  private Map<String, IParameter> queryParameters;
  private Map<String, IParameter> headers;

  public ActionImpl(final EndPoint endPoint, final Operation operation) {
    this.endPoint = endPoint;
    this.operation = operation;
  }

  @Override
  public IActionType getType() {
    return IActionType.valueOf(operation.method().value().toUpperCase());
  }

  @Override
  public boolean hasBody() {
    return !getBody().isEmpty();
  }

  @Override
  public Map<String, IResponse> getResponses() {
    if (responses == null) {
      responses = loadResponses(operation);
    }
    return responses;
  }

  private static Map<String, IResponse> loadResponses(final Operation operation) {
    Map<String, IResponse> result = new LinkedHashMap<>();
    for (Response response : operation.responses()) {
      result.put(response.statusCode().value(), new ResponseImpl(response));
    }
    return result;
  }

  @Override
  public IResource getResource() {
    return new ResourceImpl(endPoint);
  }

  @Override
  public Map<String, IMimeType> getBody() {
    if (bodies == null) {
      bodies = loadBodies(operation);
    }

    return bodies;
  }

  private static Map<String, IMimeType> loadBodies(final Operation operation) {
    final Request request = operation.request();
    if (request == null)
      return emptyMap();

    final Map<String, IMimeType> result = new LinkedHashMap<>();

    request.payloads().forEach(payload -> {
      result.put(payload.schema().name().value(), new MimeTypeImpl(payload));
    });

    return result;
  }

  @Override
  public Map<String, IParameter> getQueryParameters() {
    if (queryParameters == null) {
      queryParameters = loadQueryParameters(operation);
    }
    return queryParameters;
  }

  private static Map<String, IParameter> loadQueryParameters(final Operation operation) {
    final Request request = operation.request();
    if (request == null)
      return emptyMap();

    final Map<String, IParameter> result = new HashMap<>();
    request.queryParameters().forEach(parameter -> {
      result.put(parameter.name().value(), new ParameterImpl(parameter));
    });
    return result;
  }

  @Override
  public Map<String, List<IParameter>> getBaseUriParameters() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, IParameter> getHeaders() {
    if (headers == null) {
      headers = loadHeaders(operation);
    }
    return headers;
  }

  private Map<String, IParameter> loadHeaders(final Operation operation) {
    final Request request = operation.request();
    if (request == null)
      return emptyMap();

    final Map<String, IParameter> result = new HashMap<>();
    request.headers().forEach(parameter -> {
      result.put(parameter.name().value(), new ParameterImpl(parameter));
    });
    return result;
  }

  @Override
  public List<ISecurityReference> getSecuredBy() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<String> getIs() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void cleanBaseUriParameters() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setHeaders(Map<String, IParameter> headers) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setQueryParameters(Map<String, IParameter> queryParameters) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setBody(Map<String, IMimeType> body) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addResponse(String key, IResponse response) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addSecurityReference(String securityReferenceName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addIs(String is) {
    throw new UnsupportedOperationException();
  }
}
