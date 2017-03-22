package com.cx.sdk.infrastructure;

import com.checkmarx.v7.Credentials;
import com.checkmarx.v7.CxSDKWebService;
import com.checkmarx.v7.CxSDKWebServiceSoap;
import com.checkmarx.v7.CxWSResponseLoginData;
import com.cx.sdk.application.contracts.providers.SDKConfigurationProvider;
import com.cx.sdk.domain.exceptions.SdkException;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by ehuds on 2/28/2017.
 */
public class CxSoapClient {
    private final SDKConfigurationProvider sdkConfigurationProvider;

    public CxSoapClient(SDKConfigurationProvider sdkConfigurationProvider) {
        this.sdkConfigurationProvider = sdkConfigurationProvider;
    }

    public CxWSResponseLoginData login(String userName, String password) throws SdkException {
        URL wsdlUrl = getWsdlUrl(sdkConfigurationProvider.getCxServerUrl());
        CxSDKWebService cxSDKWebService = new CxSDKWebService(wsdlUrl);
        CxSDKWebServiceSoap cxSDKWebServiceSoap = cxSDKWebService.getCxSDKWebServiceSoap();
        Credentials credentials = new Credentials();
        credentials.setUser(userName);
        credentials.setPass(password);

        CxWSResponseLoginData responseLoginData = cxSDKWebServiceSoap.login(credentials, 1033);
        validateResponse(responseLoginData);
        return responseLoginData;
    }

    public CxWSResponseLoginData ssoLogin() throws SdkException {
        URL wsdlUrl = getWsdlUrl(sdkConfigurationProvider.getCxServerUrl());
        CxSDKWebService cxSDKWebService = new CxSDKWebService(wsdlUrl);
        CxSDKWebServiceSoap cxSDKWebServiceSoap = cxSDKWebService.getCxSDKWebServiceSoap();

        CxWSResponseLoginData responseLoginData = cxSDKWebServiceSoap.ssoLogin(new Credentials(), 1033);
        validateResponse(responseLoginData);
        return responseLoginData;
    }

    private URL getWsdlUrl(URL cxServerUrl) {
        if (cxServerUrl.toString().endsWith("wsdl")) {
            return cxServerUrl;
        }

        try {
            return new URL(cxServerUrl, "/cxwebinterface/sdk/cxsdkwebservice.asmx?wsdl");
        } catch (MalformedURLException e) {
            return cxServerUrl;
        }
    }

    private void validateResponse(CxWSResponseLoginData responseLoginData) throws SdkException {
        if (responseLoginData == null || !responseLoginData.isIsSuccesfull())
            throw new SdkException("Login Failed");
    }
}
