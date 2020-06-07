package com.chatapp.util;

import com.chatapp.model.ChannelMessage;
import com.google.gson.Gson;

/**
 * Created by manu.sharma on 6/1/20
 */

public class ClientResponseUtil {

    private static final String NEW = "new";
    private static final String EXIT = "exit";
    private static final String MESSAGE = "message";


    public static String getNewClientResponse(String name, int onlineCount){
        ChannelMessage channelMessage = new ChannelMessage();
        channelMessage.setFlag(NEW);
        String message = name + " joined conversation!";
        channelMessage.setMessage(message);
        channelMessage.setOnlineCount(onlineCount);
        channelMessage.setCreatedAt(System.currentTimeMillis());
        ChannelMessage.User user = new ChannelMessage.User();
        user.setUserName(name);
        channelMessage.setSender(user);
        return new Gson().toJson(channelMessage);
    }

    public static String getExitClientResponse(String name, int onlineCount){
        ChannelMessage channelMessage = new ChannelMessage();
        channelMessage.setFlag(EXIT);
        String message = name + " left conversation!";
        channelMessage.setMessage(message);
        channelMessage.setCreatedAt(System.currentTimeMillis());
        channelMessage.setOnlineCount(onlineCount);
        ChannelMessage.User user = new ChannelMessage.User();
        user.setUserName(name);
        channelMessage.setSender(user);
        return new Gson().toJson(channelMessage);
    }

    public static String sendAllMessage(String name, String message, int onlineCount){
        ChannelMessage channelMessage = new ChannelMessage();
        channelMessage.setFlag(MESSAGE);
        channelMessage.setMessage(message);
        channelMessage.setCreatedAt(System.currentTimeMillis());
        ChannelMessage.User user = new ChannelMessage.User();
        user.setUserName(name);
        channelMessage.setOnlineCount(onlineCount);
        channelMessage.setSender(user);
        return new Gson().toJson(channelMessage);
    }
}
