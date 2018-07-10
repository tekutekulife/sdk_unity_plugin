package com.zendesk.unity.push;

import com.zendesk.unity.UnityComponent;

import zendesk.core.Zendesk;


public class ZDKPush extends UnityComponent {

    public static ZDKPush _instance = new ZDKPush();
    public static Object instance(){
        return _instance;
    }

    public void enablePushWithIdentifier(final String gameObjectName, String callbackId, String identifier) {
        ZendeskUnityCallback<String> callback = new ZendeskUnityCallback<>(gameObjectName, callbackId, "didEnablePushWithIdentifier");
        Zendesk.INSTANCE.provider().pushRegistrationProvider().registerWithDeviceIdentifier(identifier, callback);
    }

    public void enablePushWithUAChannelId(final String gameObjectName, String identifier, String callbackId) {
        ZendeskUnityCallback<String> callback = new ZendeskUnityCallback<>(gameObjectName, callbackId, "didEnablePushWithUAChannelId");
        Zendesk.INSTANCE.provider().pushRegistrationProvider().registerWithUAChannelId(identifier, callback);
    }

    public void disablePush(final String gameObjectName, String callbackId) {
        ZendeskUnityCallback<Void> callback = new ZendeskUnityCallback<>(gameObjectName, callbackId, "didPushDisable");
        Zendesk.INSTANCE.provider().pushRegistrationProvider().unregisterDevice(callback);
    }
}
