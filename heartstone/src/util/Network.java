package util;

import gameObjects.Player;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

//This class is a convenient place to keep things common to both the client and server.
public class Network {

	static public final int port = 147;

	// This registers objects that are going to be sent over the network.
	static public void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(RegisterName.class);
		kryo.register(ActionMessage.class);
		kryo.register(Player.class);
		kryo.register(util.Stats.class);

	}

	static public class RegisterName {

		public String name;
	}

	static public class ActionMessage {

		public static final String START = "a1";
		public static final String PASS_TURN = "a8";
                public static final String INIT="a3";
		public String action;
	}
}
