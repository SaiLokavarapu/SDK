package com.cx.sdk.infrastructure;

import com.checkmarx.v7.*;
import com.cx.sdk.application.contracts.providers.SDKConfigurationProvider;
import com.cx.sdk.application.contracts.exceptions.NotAuthorizedException;
import com.cx.sdk.domain.Session;
import com.cx.sdk.domain.exceptions.SdkException;
import com.cx.sdk.infrastructure.authentication.kerberos.DynamicAuthSupplier;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by ehuds on 2/28/2017.
 */
public class CxSoapClient {
    private final SDKConfigurationProvider sdkConfigurationProvider;

    public CxSoapClient(SDKConfigurationProvider sdkConfigurationProvider) {
        this.sdkConfigurationProvider = sdkConfigurationProvider;
        DynamicAuthSupplier.setKerberosActive(sdkConfigurationProvider.useKerberosAuthentication());
    }

    public CxWSResponseLoginData login(String userName, String password) throws Exception {
        URL wsdlUrl = getWsdlUrl(sdkConfigurationProvider.getCxServerUrl());
        CxSDKWebService cxSDKWebService = new CxSDKWebService(wsdlUrl);
        CxSDKWebServiceSoap cxSDKWebServiceSoap = cxSDKWebService.getCxSDKWebServiceSoap();
        Credentials credentials = new Credentials();
        credentials.setUser(userName);
        credentials.setPass(password);

        CxWSResponseLoginData responseLoginData = cxSDKWebServiceSoap.login(credentials, 1033);
        validateLoginResponse(responseLoginData);
        return responseLoginData;
    }

    public CxWSResponseLoginData ssoLogin() throws Exception {
        CxSDKWebServiceSoap cxSDKWebServiceSoap = createProxy();

        CxWSResponseLoginData responseLoginData = cxSDKWebServiceSoap.ssoLogin(new Credentials(), 1033);
        validateLoginResponse(responseLoginData);
        return responseLoginData;
    }

    public CxWSResponsePresetList getPresets(Session session) throws Exception {
        CxSDKWebServiceSoap cxSDKWebServiceSoap = createProxy();
        CxWSResponsePresetList response = cxSDKWebServiceSoap.getPresetList(session.getSessionId());
        validateResponse(response);
        return response;
    }

    public CxWSResponseGroupList getTeams(Session session) throws Exception {
        CxSDKWebServiceSoap cxSDKWebServiceSoap = createProxy();
        CxWSResponseGroupList response = cxSDKWebServiceSoap.getAssociatedGroupsList(session.getSessionId());
        validateResponse(response);
        return response;
    }

    public CxWSResponseConfigSetList getConfigurations(Session session) throws Exception {
        CxSDKWebServiceSoap cxSDKWebServiceSoap = createProxy();
        CxWSResponseConfigSetList response = cxSDKWebServiceSoap.getConfigurationSetList(session.getSessionId());
        validateResponse(response);
        return response;
    }

    public Boolean isProjectNameValid(Session session, String projectName, String groupId) throws Exception {
        CxSDKWebServiceSoap cxSDKWebServiceSoap = createProxy();
        CxWSBasicRepsonse response = cxSDKWebServiceSoap.isValidProjectName(session.getSessionId(), projectName, groupId);
        validateResponse(response);
        Boolean isValid = response.isIsSuccesfull();
        return isValid;
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

    private void validateResponse(CxWSBasicRepsonse response) throws Exception {
        if (response.isIsSuccesfull())
            return;

        if ("ReConnect".equals(response.getErrorMessage()))
            throw new NotAuthorizedException();

        throw new Exception(response.getErrorMessage());
    }

    private void validateLoginResponse(CxWSBasicRepsonse response) throws SdkException {
        if (response == null || !response.isIsSuccesfull())
            throw new SdkException("Login failed");
    }

    private CxSDKWebServiceSoap createProxy() {
        URL wsdlUrl = getWsdlUrl(sdkConfigurationProvider.getCxServerUrl());
        CxSDKWebService cxSDKWebService = new CxSDKWebService(wsdlUrl);
        CxSDKWebServiceSoap cxSDKWebServiceSoap = cxSDKWebService.getCxSDKWebServiceSoap();
        return cxSDKWebServiceSoap;
    }
}
