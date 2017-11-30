import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class GameServer {

	static int port = 1200; // needs to match port from Client!!!!!!!!!!
	static double OneY, TwoY, BallX;
	static DatagramSocket socket = null; // initialize
	static DatagramPacket replyPacket = null;

	public GameServer() {
	}

	public static void main(String args[]) throws IOException, InterruptedException {
		OneY = 200;
		TwoY = 200;
		BallX = 300;
		GameServer server = new GameServer();
		server.createAndListenSocket();
	}

	public void createAndListenSocket() throws InterruptedException {
		try {
			socket = new DatagramSocket(port); // listen on port ____
			byte[] incomingData = new byte[1024]; // initialize incoming data
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length); // get incoming
			
			while (true) { // this loop is infinite for now
				socket.receive(incomingPacket);
				handlePacket(incomingPacket);
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public static void handlePacket(DatagramPacket recieved) throws IOException {
		String message = new String(recieved.getData()); // get string from packet
		InetAddress IPAddress = recieved.getAddress();
		int port = recieved.getPort();
		System.out.println("From Client:" + message);
		String[] check = message.split(":"); // splits string into method and value

		if (check[0].equals("get")) {
			String response = OneY + ":" + TwoY + ":" + BallX;
			byte[] data = response.getBytes();
			replyPacket = new DatagramPacket(data, data.length, IPAddress, port);
			socket.send(replyPacket);
		} else if (check[0].equals("p1plus")) {
			p1plus();
		} else if (check[0].equals("p1minus")) {
			p1minus();
		} else if (check[0].equals("p2plus")) {
			p2plus();
		} else if (check[0].equals("p2minus")) {
			p2minus();
		} else if (check[0].equals("end")) {
			socket.close();
			System.exit(0);
		} else if (check[0].equals("ball")) {
			setBall(Double.parseDouble(check[1]));
		} 
	}

	public static void setBall(double pos) {
		BallX = pos;
	}
	
	public static void p1minus() {
		OneY = OneY + 10;
	}

	public static void p1plus() {
		OneY = OneY - 10;
	}

	public static void p2plus() {
		TwoY = TwoY - 10;
	}

	public static void p2minus() {
		TwoY = TwoY + 10;
	}
}