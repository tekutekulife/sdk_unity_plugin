package com.zendesk.unity.providers;

import com.google.gson.internal.LinkedTreeMap;
import com.zendesk.unity.UnityComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import zendesk.core.User;
import zendesk.core.UserField;
import zendesk.core.Zendesk;

public class UserProvider extends UnityComponent {

    public static UserProvider _instance;
    public static Object instance(){
        _instance = new UserProvider();
        return _instance;
    }

    public void addTags(final String gameObjectName, String callbackId, String[] tags, int tagsLength){
        zendesk.core.UserProvider provider = Zendesk.INSTANCE.provider().userProvider();
        ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tags));

        provider.addTags(tagsList,
                new ZendeskUnityCallback<List<String>>(gameObjectName, callbackId, "didUserProviderAddTags"));
    }

    public void deleteTags(final String gameObjectName, String callbackId, String[] tags, int tagsLength){
        zendesk.core.UserProvider provider = Zendesk.INSTANCE.provider().userProvider();
        ArrayList<String> tagsList = new ArrayList<>(Arrays.asList(tags));

        provider.deleteTags(tagsList,
                new ZendeskUnityCallback<List<String>>(gameObjectName, callbackId, "didUserProviderDeleteTags"));
    }

    public void getUser(final String gameObjectName, String callbackId){
        zendesk.core.UserProvider provider = Zendesk.INSTANCE.provider().userProvider();

        provider.getUser(new ZendeskUnityCallback<User>(gameObjectName, callbackId, "didUserProviderGetUser"));
    }

    public void getUserFields(final String gameObjectName, String callbackId){
        zendesk.core.UserProvider provider = Zendesk.INSTANCE.provider().userProvider();

        provider.getUserFields(new ZendeskUnityCallback<List<UserField>>(gameObjectName, callbackId, "didUserProviderGetUserFields"));
    }

    @SuppressWarnings("unchecked")
    public void setUserFields(final String gameObjectName, String callbackId, String userFields){
        zendesk.core.UserProvider provider = Zendesk.INSTANCE.provider().userProvider();
        Map<String,String> userFieldMap = gson().fromJson(userFields, LinkedTreeMap.class);

        provider.setUserFields(userFieldMap,
                new ZendeskUnityCallback<Map<String,String>>(gameObjectName, callbackId, "didUserProviderSetUserFields"));
    }

}
