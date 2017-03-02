package com.cx.sdk.api;

import java.net.URL;

/**
 * Created by victork on 28/02/2017.
 */
public class SdkConfiguration {
    private final URL cxServerUrl;
    private final String cxOrigin;

    public SdkConfiguration(URL cxServerUrl, String cxOrigin)
    {
        this.cxServerUrl = cxServerUrl;
        this.cxOrigin = cxOrigin;
    }

    public String getOriginName() { return cxOrigin; }
    public URL getCxServerUrl() {
        return cxServerUrl;
    }
}
