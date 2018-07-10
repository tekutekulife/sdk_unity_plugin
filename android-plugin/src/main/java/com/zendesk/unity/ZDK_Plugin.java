package com.zendesk.unity;

import android.app.Activity;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unity3d.player.UnityPlayer;
import com.zendesk.logger.Logger;
import com.zendesk.sdk.network.impl.UserAgentHeaderUtil;
import com.zendesk.util.CollectionUtils;
import com.zendesk.util.StringUtils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.JwtIdentity;
import zendesk.core.Zendesk;
import zendesk.support.CustomField;
import zendesk.support.Support;
import zendesk.support.guide.ArticleUiConfig;
import zendesk.support.guide.HelpCenterActivity;
import zendesk.support.guide.HelpCenterUiConfig;
import zendesk.support.guide.ViewArticleActivity;
import zendesk.support.request.RequestActivity;
import zendesk.support.request.RequestUiConfig;
import zendesk.support.requestlist.RequestListActivity;

/**
 * Zendesk Plugin
 */
public class ZDK_Plugin extends UnityComponent {

    private static final String LOG_TAG = "ZDK_Plugin";

    private static final int CONTACT_US_BUTTON_VISIBILITY_ARTICLE_LIST_ONLY = 1;
    private static final int CONTACT_US_BUTTON_VISIBILITY_ARTICLE_LIST_ARTICLE_VIEW = 2;
    
    public static ZDK_Plugin _instance;
    public static Object instance(){
        if (_instance == null) {
            _instance = new ZDK_Plugin();
        }
        return _instance;
    }

    // this field is only useful when doing direct tests outside of Unity
    public Activity _activity;

    // Fetches the current Activity that the Unity player is using
    @Override
    protected Activity getActivity() {
        if (_activity != null)
            return _activity;

        return super.getActivity();
    }


    // ##### ##### ##### ##### ##### ##### ##### #####
    // ZDKConfig
    // ##### ##### ##### ##### ##### ##### ##### #####


    public void initialize(String zendeskUrl, String applicationId, String oauthClientId) {

        Activity activity = UnityPlayer.currentActivity;
        if (activity == null) {
            Log.e(LOG_TAG, "initialize: Unity activity is null!!");
            return;
        }

        Zendesk.INSTANCE.init(activity.getApplication(), zendeskUrl, applicationId, oauthClientId);
        Support.INSTANCE.init(Zendesk.INSTANCE);
        UserAgentHeaderUtil.addUnitySuffix();
    }

    //authenticate anonymous identity with details
    public void authenticateAnonymousIdentity(String name, String email) {
        Identity anonymousIdentity = new AnonymousIdentity.Builder().withEmailIdentifier(email)
                .withNameIdentifier(name)
                .build();
        Zendesk.INSTANCE.setIdentity(anonymousIdentity);
    }

    public void authenticateJwtUserIdentity(String jwtUserIdentity){
        Identity jwtIdentity = new JwtIdentity(jwtUserIdentity);
        Zendesk.INSTANCE.setIdentity(jwtIdentity);
    }

    public void setUserLocale(String locale) {
        try {
            Support.INSTANCE.setHelpCenterLocaleOverride(locale != null ? new Locale(locale) : null);
        } catch (Exception ex) {
            Log.e("Zendesk", "failed setting user locale", ex);
        }
    }

    // ##### ##### ##### ##### ##### ##### ##### #####
    // ZDKLogger
    // ##### ##### ##### ##### ##### ##### ##### #####

    public void enableLogger(boolean boolString){
            Logger.setLoggable(boolString);
    }


    // ##### ##### ##### ##### ##### ##### ##### #####
    // ZDKHelpCenter
    // ##### ##### ##### ##### ##### ##### ##### #####

    public void showHelpCenter() {
        if (!checkInitialized()) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                HelpCenterActivity.builder()
                        .show(getActivity());
            }
        });
    }

    public void showHelpCenter(boolean collapseCategories, final boolean showContactUsButton, String[] labelNames, long[] sectionIds, long[] categoryIds, final String[] tags, final String additionalInfo, final String requestSubject) {
        
            showHelpCenter(collapseCategories, CONTACT_US_BUTTON_VISIBILITY_ARTICLE_LIST_ARTICLE_VIEW, labelNames, sectionIds, categoryIds, tags, requestSubject);
        }
    
    public void showHelpCenter(boolean collapseCategories, final int contactUsButtonVisibility,
                                    String[] labelNames, long[] sectionIds, long[] categoryIds,
                                    final String[] tags, final String requestSubject) {

        if(!checkInitialized()) {
            return;
        }

        final HelpCenterUiConfig.Builder helpCenterUiBuilder = HelpCenterActivity.builder()
                .withLabelNames(labelNames)
                .withCategoriesCollapsed(collapseCategories);
        final ArticleUiConfig.Builder articleUiBuilder = ViewArticleActivity.builder();

        if (contactUsButtonVisibility != CONTACT_US_BUTTON_VISIBILITY_ARTICLE_LIST_ARTICLE_VIEW) {
            articleUiBuilder.withContactUsButtonVisible(false);
        }
        if (contactUsButtonVisibility == CONTACT_US_BUTTON_VISIBILITY_ARTICLE_LIST_ONLY) {
            helpCenterUiBuilder.withContactUsButtonVisible(false);
        }

        if (sectionIds != null && sectionIds.length > 0) {
            helpCenterUiBuilder.withArticlesForSectionIds(longArrayToObjectList(sectionIds));
        }

        if (categoryIds != null && categoryIds.length > 0) {
            helpCenterUiBuilder.withArticlesForCategoryIds(longArrayToObjectList(categoryIds));
        }

        final RequestUiConfig.Builder requestUiBuilder = RequestActivity.builder();
        if (StringUtils.hasLength(requestSubject)) {
            requestUiBuilder.withRequestSubject(requestSubject);
        }
        if (CollectionUtils.isNotEmpty(tags)) {
            requestUiBuilder.withTags(tags);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                helpCenterUiBuilder.show(getActivity(),
                        articleUiBuilder.config(),
                        requestUiBuilder.config());
            }
        });

    }

    private List<Long> longArrayToObjectList(long[] longs) {
        List<Long> list = new ArrayList<>(longs.length);
        for (long aLong: longs) {
            list.add(aLong);
        }
        return list;
    }

    private boolean checkInitialized() {
        if (Support.INSTANCE.isInitialized()) {
            return true;
        }
        Log.e("Zendesk Unity", "Zendesk SDK must be initialized before doing anything else! Did you call ZendeskSDK.ZDKConfig.Initialize(...)?");
        return false;
    }

    public void viewArticle(final String id){
        ViewArticleActivity.builder(Long.valueOf(id))
                .show(getActivity());
    }


    // ##### ##### ##### ##### ##### ##### ##### #####
    // ZDKRequests
    // ##### ##### ##### ##### ##### ##### ##### #####

    public void showRequestCreation(){
        if(!checkInitialized())
            return;

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                RequestActivity.builder()
                        .show(getActivity());
            }
        });
    }

    /**
     *  Form id for ticket creation.
     *
     *  The ticket form id will be ignored if your Zendesk doesn't support it.  Currently
     *  Enterprise and higher plans support this.
     *
     *  @param ticketFormId the form id for ticket creation
     *
     *  @see <a href="https://developer.zendesk.com/embeddables/docs/ios/providers#using-custom-fields-and-custom-forms">Custom fields and forms documentation</a>
     *  @since 1.0.0.1
     */
    public void showRequestCreationWithConfig(final String requestSubject, final String[] tags,
                                              final String jsonFields, final String ticketFormId) {
        if(!checkInitialized())
            return;

        final RequestUiConfig.Builder builder = RequestActivity.builder()
                .withTags(tags)
                .withRequestSubject(requestSubject);

        Map<String, String> fields = getGson().fromJson(jsonFields, Map.class);

        List<CustomField> customFields = new ArrayList<>(fields.entrySet().size());
        for (Map.Entry<String, String> field: fields.entrySet()) {
            customFields.add(new CustomField(Long.valueOf(field.getKey()), field.getValue()));
        }

        if (CollectionUtils.isNotEmpty(customFields)) {
            Long formId = null;

            try {
                formId = Long.valueOf(ticketFormId);
            } catch (NumberFormatException e) {
                Logger.e(LOG_TAG, "The supplied ticketFormId was not a number", e);
            }

            if (formId != null) {
                builder.withTicketForm(formId, customFields);
            }
        }

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                builder.show(getActivity());
            }
        });
    }

    public void showRequest(final String requestId) {
        if(!checkInitialized())
            return;

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                RequestActivity.builder()
                        .withRequestId(requestId)
                        .show(getActivity());
            }
        });
    }

    public void showRequestList() {
        if(!checkInitialized())
            return;

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                RequestListActivity.builder()
                        .show(getActivity());
            }
        });
    }

    private Gson getGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .create();
    }
}
