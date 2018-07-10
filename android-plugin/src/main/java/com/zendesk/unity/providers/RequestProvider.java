package com.zendesk.unity.providers;

import com.zendesk.unity.UnityComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import zendesk.support.Comment;
import zendesk.support.CommentsResponse;
import zendesk.support.CreateRequest;
import zendesk.support.EndUserComment;
import zendesk.support.Request;
import zendesk.support.RequestUpdates;
import zendesk.support.Support;
import zendesk.support.TicketForm;

public class RequestProvider extends UnityComponent {

    public static RequestProvider _instance;
    public static Object instance(){
        _instance = new RequestProvider();
        return _instance;
    }

    public void createRequest(final String gameObjectName, String callbackId, String subject,
                                                                   String description, String[] tags,
                                                                   int tagsLength, String[] attachments, int attachmentsLength){
        ArrayList<String> attachmentsList = attachments != null ? new ArrayList<>(Arrays.asList(attachments)) : null;
        ArrayList<String> tagsList = tags != null ? new ArrayList<>(Arrays.asList(tags)) : null;

        CreateRequest createRequest = new CreateRequest();
        createRequest.setSubject(subject);
        createRequest.setDescription(description);
        createRequest.setTags(tagsList);
        createRequest.setAttachments(attachmentsList);

        zendesk.support.RequestProvider provider = Support.INSTANCE.provider().requestProvider();
        provider.createRequest(createRequest,
                new ZendeskUnityCallback<Request>(gameObjectName, callbackId, "didRequestProviderCreateRequest"));
    }

    public void getAllRequests(final String gameObjectName, String callbackId){
        zendesk.support.RequestProvider provider = Support.INSTANCE.provider().requestProvider();
        provider.getAllRequests(
                new ZendeskUnityCallback<List<Request>>(gameObjectName, callbackId, "didRequestProviderGetAllRequests"));
    }

    public void getRequestsByStatus(final String gameObjectName, String callbackId, String status){
        zendesk.support.RequestProvider provider = Support.INSTANCE.provider().requestProvider();
        //Status is a comma separated list of status to filter the results by status
        provider.getRequests(status,
                new ZendeskUnityCallback<List<Request>>(gameObjectName, callbackId, "didRequestProviderGetAllRequestsByStatus"));
    }

    public void getCommentsWithRequestId(final String gameObjectName, String callbackId, String requestId){
        zendesk.support.RequestProvider provider = Support.INSTANCE.provider().requestProvider();
        provider.getComments(requestId,
                new ZendeskUnityCallback<CommentsResponse>(gameObjectName, callbackId, "didRequestProviderGetCommentsWithRequestId"));
    }

    public void getRequestWithId(final String gameObjectName, String callbackId, String requestId){
        zendesk.support.RequestProvider provider = Support.INSTANCE.provider().requestProvider();
        
        provider.getRequest(requestId,
                new ZendeskUnityCallback<Request>(gameObjectName, callbackId, "didRequestProviderGetRequestWithId"));
    }

    public void addComment(final String gameObjectName, String callbackId, String comment, String requestId){
        EndUserComment endUserComment = new EndUserComment();
        endUserComment.setValue(comment);

        zendesk.support.RequestProvider provider = Support.INSTANCE.provider().requestProvider();
        provider.addComment(requestId, endUserComment,
                new ZendeskUnityCallback<Comment>(gameObjectName, callbackId, "didRequestProviderAddComment"));
    }

    public void addCommentWithAttachments(final String gameObjectName, String callbackId, String comment,
                                                                String requestId, String[] attachments, int attachmentsLength){
        ArrayList<String> attachmentsList = new ArrayList<>(Arrays.asList(attachments));
        EndUserComment endUserComment = new EndUserComment();
        endUserComment.setAttachments(attachmentsList);
        endUserComment.setValue(comment);

        zendesk.support.RequestProvider provider = Support.INSTANCE.provider().requestProvider();
        provider.addComment(requestId, endUserComment,
                new ZendeskUnityCallback<Comment>(gameObjectName, callbackId, "didRequestProviderAddCommentWithAttachments"));
    }

    public void getTicketFormWithIds(final String gameObjectName, String callbackId, long[] ticketFormsIds, int formsCount){
        
        Long[] result = new Long[formsCount];
        for (int i = 0; i < formsCount; i++) {
            result[i] = Long.valueOf(ticketFormsIds[i]);
        }

        ArrayList<Long> ids = new ArrayList<>(Arrays.asList(result));

        zendesk.support.RequestProvider provider = Support.INSTANCE.provider().requestProvider();
        provider.getTicketFormsById(ids,
                        new ZendeskUnityCallback<List<TicketForm>>(gameObjectName, callbackId, "didRequestProviderGetTicketFormWithIds"));
    }
    
    public void getUpdatesForDevice(String gameObjectName, String callbackId) {
        Support.INSTANCE.provider().requestProvider().getUpdatesForDevice(new ZendeskUnityCallback<RequestUpdates>(gameObjectName, callbackId, "didRequestProviderGetUpdatesForDevice"));
    }
    
    public void markRequestAsRead(String requestId, int readCommentCount) {
        Support.INSTANCE.provider().requestProvider().markRequestAsRead(requestId, readCommentCount);
    }

}
