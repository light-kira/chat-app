package com.chatapp.server;

import com.chatapp.util.ClientResponseUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by manu.sharma on 6/3/20
 */

public class ChatService {
    private static ChatService instance;

    public static ChatService getInstance(){
        if (null != instance) return instance;
        synchronized (ChatService.class) {
            if (null != instance) return instance;

            instance = new ChatService();
        }
        return instance;
    }

    public static Map<Channel, String> channelToNameMapping = new HashMap<>();
    public static Map<String, Channel> nameToChannelMapping = new HashMap<>();
    private static Set<Channel> channels = new HashSet<>();

    public void onOpen(Channel sourceChannel, String userName){
        channels.add(sourceChannel);
        channelToNameMapping.put(sourceChannel, userName);
        nameToChannelMapping.put(userName, sourceChannel);
        broadcastAll(sourceChannel, userName,"", true, false);
    }

    public void onMessage(Channel sourceChannel, String message){
        String name = channelToNameMapping.get(sourceChannel);
        broadcastAll(sourceChannel, name, message, false, false);
    }

    public void onClose(Channel channel){
        String name = channelToNameMapping.get(channel);
        channels.remove(channel);
        channelToNameMapping.remove(channel);
        broadcastAll(channel, name, "", false, true);
    }

    private static void broadcastAll(Channel sourceChannel, String name, String message, boolean isNewClient, boolean isExit){
        for (Channel channel : channels){
            if(channel.equals(sourceChannel)) continue;
            System.out.println("flushing to channel "+channel.id());
            String response = null;
            if(isNewClient){
                response = ClientResponseUtil.getNewClientResponse(name, channels.size());
            }else if (isExit){
                response = ClientResponseUtil.getExitClientResponse(name, channels.size());
            }else {
                response = ClientResponseUtil.sendAllMessage(name, message, channels.size());
            }

            channel.writeAndFlush(new TextWebSocketFrame(response));
        }
    }
}
