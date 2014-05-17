package util;

import gameObjects.Player;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * This class is a convenient place to keep things common to both the client and server.
 * @author Enrique Martín Arenal
 *
 */
public class Network {

	static public final int port = 54555;

	/**
	 *  This registers objects that are going to be sent over the network.
	 * @param endPoint 
	 */
	static public void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(RegisterName.class);
		kryo.register(ActionMessage.class);
		kryo.register(Player.class);
		kryo.register(util.Stats.class);

	}
	/**
	 * Class used to register the connections on the server
	 * @author Enrique Martín Arenal
	 *
	 */
	static public class RegisterName {

		public String name;
	}
	/**
	 * Class used to send which action has been done  
	 * @author Enrique Martín Arenal
	 *
	 */
	static public class ActionMessage {

		public static final String START = "a1";
		public static final String PASS_TURN = "a8";
        public static final String DISCONNECT="a3";
		public String action;
	}
}
