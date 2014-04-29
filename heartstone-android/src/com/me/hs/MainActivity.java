package com.me.hs;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;


public class MainActivity extends AndroidApplication {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
        
        Client client;
	
			client = new Client();
			Listener listener=null;
			 initialize(new HStone(client,listener), cfg);
	
        
        
        
       
    }
}