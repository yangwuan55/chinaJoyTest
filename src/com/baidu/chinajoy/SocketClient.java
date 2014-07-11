package com.baidu.chinajoy;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by yangmengrong on 14-7-11.
 */
public class SocketClient extends WebSocketClient{

    private MessageListener messageListener;

    public static interface MessageListener{
        void onReceive(String message);
    }

    public SocketClient( URI serverUri , Draft draft ) {
        super(serverUri, draft);
    }

    public SocketClient( URI serverURI ) {
        super(serverURI);
    }

    @Override
    public void onOpen( ServerHandshake handshakedata ) {
        System.out.println( "yangmengrong opened connection" );
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage( String message ) {
        System.out.println( "yangmengrong received: " + message );
        if (messageListener != null) {
            messageListener.onReceive(message);
        }
    }

    @Override
    public void onFragment( Framedata fragment ) {
        System.out.println( "yangmengrong received fragment: " + new String( fragment.getPayloadData().array() ) );
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println( "yangmengrong Connection closed by " + ( remote ? "remote peer" : "us" ) );
    }

    @Override
    public void onError( Exception ex ) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
        System.out.println( "yangmengrong Exception: " + ex.toString() );
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }
}
