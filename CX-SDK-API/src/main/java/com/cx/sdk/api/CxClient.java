package com.cx.sdk.api;

import com.cx.sdk.api.DTOs.SessionDTO;
import com.cx.sdk.domain.exceptions.SdkException;

/**
 * Created by ehuds on 2/22/2017.
 */
public interface CxClient {
    SessionDTO login(String userName, String password) throws SdkException;
}