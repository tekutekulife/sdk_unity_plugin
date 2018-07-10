package com.zendesk.sdk.network.impl;

public class UserAgentHeaderUtil {

    private static final String UNITY_USER_AGENT_HEADER = "Unity";

    public static void addUnitySuffix() {
        // TODO how do we handle this for v2?
//        ZendeskConfig.INSTANCE.addUserAgentHeaderSuffix(new Pair<>(UNITY_USER_AGENT_HEADER, BuildConfig.VERSION_NAME));
    }

}
