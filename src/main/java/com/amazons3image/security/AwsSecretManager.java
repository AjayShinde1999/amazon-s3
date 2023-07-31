package com.amazons3image.security;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.amazonaws.services.simplesystemsmanagement.model.ParameterNotFoundException;

import static com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder.defaultClient;

import com.amazons3image.exception.InvalidKeyException;
import org.apache.commons.lang3.StringUtils;

public class AwsSecretManager {
    private static final AWSSimpleSystemsManagement awsSimpleSystemsManagement = defaultClient();

    public static String getParameterValueFromStore(String key) {
        if (StringUtils.isNotEmpty(key)) {
            try {
                final GetParameterResult parameter = awsSimpleSystemsManagement
                        .getParameter(new GetParameterRequest().withName(key).withWithDecryption(true));
                return parameter.getParameter().getValue();
            } catch (ParameterNotFoundException pnfe) {
                throw new InvalidKeyException(String.format("No parameter found for key : %s ", key));
            }
        } else {
            throw new InvalidKeyException("Key can not be null for parameter fetching.");
        }
    }
}
