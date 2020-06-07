package com.chatapp.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by manu.sharma on 6/4/20
 */

public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private WebSocketServerHandshaker handshaker;
    private ChatService chatService = ChatService.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext context, Object message){
        if(message instanceof HttpRequest){
            HttpRequest httpRequest = (HttpRequest) message;
            System.out.println("Http Request Received");

            HttpHeaders headers = httpRequest.headers();
            System.out.println("Connection : "+headers.get("Connection"));
            System.out.println("Upgrade : "+headers.get("Upgrade"));
            System.out.println("URI : "+httpRequest.uri());

            if("Upgrade".equalsIgnoreCase(headers.get(HttpHeaderNames.CONNECTION)) && "Websocket".equalsIgnoreCase(headers.get(HttpHeaderNames.UPGRADE))){
                context.pipeline().replace(this, "websocketHandler", new WebSocketHandler());

                System.out.println("WebSocketHandler added to the pipeline");
                System.out.println("Opened Channel : "+context.channel());
                System.out.println("Handshaking !!!");
                handleHandshake(context, httpRequest);
                Map<String, String> queryParams = null;
                try {
                    queryParams = getQueryParams(httpRequest.uri());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String userName = queryParams.get("name");
                chatService.onOpen(context.channel(), userName);
                System.out.println("Handshake is done !!!");
            }
        }else {
            System.out.println("Incoming request is unknown");
        }
    }

    private void handleHandshake(ChannelHandlerContext context, HttpRequest request){
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketUrl(request), null, true);
        handshaker = wsFactory.newHandshaker(request);
        if(null == handshaker){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(context.channel());
        }else {
            handshaker.handshake(context.channel(), request);
        }
    }

    private String getWebSocketUrl(HttpRequest request){
        System.out.println("Request URI : "+request.uri());
        String url = "ws://"+request.headers().get("Host") + "/chatapplication";
        System.out.println("Constructed URL : "+url);
        return url;
    }

    public static Map<String, String> getQueryParams(String uri) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] queryArr = uri.split("\\?");
        if(queryArr.length <= 1){
            return query_pairs;
        }
        String query = queryArr[1];
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }
}
