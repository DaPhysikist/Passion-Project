/*
 * Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package authapi;

import java.util.*;



@com.amazonaws.mobileconnectors.apigateway.annotation.Service(endpoint = "https://byk4bk3szf.execute-api.us-west-1.amazonaws.com/prod")
public interface AuthapiClient {


    /**
     * A generic invoker to invoke any API Gateway endpoint.
     * @param request
     * @return ApiResponse
     */
    com.amazonaws.mobileconnectors.apigateway.ApiResponse execute(com.amazonaws.mobileconnectors.apigateway.ApiRequest request);
    
    /**
     * 
     * 
     * @return void
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/access", method = "OPTIONS")
    void accessOptions();
    
    /**
     * 
     * 
     * @param proxy 
     * @return void
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/access/{proxy+}", method = "OPTIONS")
    void accessProxyOptions(
            @com.amazonaws.mobileconnectors.apigateway.annotation.Parameter(name = "proxy", location = "path")
            String proxy);
    
    /**
     * 
     * 
     * @return void
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/auth", method = "OPTIONS")
    void authOptions();
    
    /**
     * 
     * 
     * @param proxy 
     * @return void
     */
    @com.amazonaws.mobileconnectors.apigateway.annotation.Operation(path = "/auth/{proxy+}", method = "OPTIONS")
    void authProxyOptions(
            @com.amazonaws.mobileconnectors.apigateway.annotation.Parameter(name = "proxy", location = "path")
            String proxy);
    
}

