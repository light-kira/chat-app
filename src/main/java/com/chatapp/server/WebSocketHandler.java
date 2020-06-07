package com.chatapp.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Created by manu.sharma on 6/4/20
 */

public class WebSocketHandler extends ChannelInboundHandlerAdapter {

    private ChatService chatService = ChatService.getInstance();
    @Override
    public void channelRead(ChannelHandlerContext context, Object message){
        System.out.println("This is websocket Frame");
        System.out.println("Client Channel : "+context.channel());

        if(message instanceof TextWebSocketFrame){
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) message;
            //context.channel().writeAndFlush(new TextWebSocketFrame("Message recieved : " + ((TextWebSocketFrame) message).text()));
            chatService.onMessage(context.channel(), textWebSocketFrame.text());
        }else if (message instanceof CloseWebSocketFrame){
            System.out.println("CloseWebSocketFrame Received : ");
            System.out.println("ReasonText :" + ((CloseWebSocketFrame) message).reasonText());
            System.out.println("StatusCode : " + ((CloseWebSocketFrame) message).statusCode());
            chatService.onClose(context.channel());
        }
    }
}
