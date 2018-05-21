/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.validation.body.form;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mule.module.apikit.api.exception.BadRequestException;
import org.mule.module.apikit.api.exception.InvalidFormParameterException;
import org.mule.module.apikit.validation.body.form.transformation.DataWeaveTransformer;
import org.mule.raml.interfaces.model.IMimeType;
import org.mule.raml.interfaces.model.parameter.IParameter;
import org.mule.raml.interfaces.parser.rule.IValidationResult;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.api.util.MultiMap;
import org.mule.runtime.core.api.el.ExpressionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class UrlencodedFormV2Validator implements FormValidatorStrategy<TypedValue> {

  protected static final Logger logger = LoggerFactory.getLogger(UrlencodedFormV2Validator.class);
  Map<String, List<IParameter>> formParameters;
  IMimeType actionMimeType;
  DataWeaveTransformer dataWeaveTransformer;

  public UrlencodedFormV2Validator(IMimeType actionMimeType, ExpressionManager expressionManager) {
    this.formParameters = actionMimeType.getFormParameters();
    this.actionMimeType = actionMimeType;
    this.dataWeaveTransformer = new DataWeaveTransformer(expressionManager);
  }

  @Override
  public TypedValue validate(TypedValue originalPayload) throws BadRequestException {

    String jsonText;
    MultiMap<String, String> requestMap = dataWeaveTransformer.getMultiMapFromPayload(originalPayload);

    try {
      jsonText = new ObjectMapper().disableDefaultTyping().writeValueAsString(requestMap);
    } catch (Exception e) {
      logger.warn("Cannot validate url-encoded form", e);
      return originalPayload;
    }

    final List<IValidationResult> validationResult;
    try {
      validationResult = actionMimeType.validate(jsonText);
    } catch (UnsupportedOperationException e) {
      return originalPayload;
    }

    if (validationResult.size() > 0) {
      String resultString = "";
      for (IValidationResult result : validationResult) {
        resultString += result.getMessage() + "\n";
      }
      throw new InvalidFormParameterException(resultString);
    }

    return dataWeaveTransformer.getXFormUrlEncodedStream(requestMap, originalPayload.getDataType());
  }
}
