import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class GameServer {

	
	static int port = 1234; // needs to match port from Client!!!!!!!!!!
	static double OneY = 50;
	static double TwoY = 550;
	static double BallY;
	static double BallX;
	static DatagramSocket socket = null; // initialize
	static DatagramPacket replyPacket = null;
	
	public GameServer() {
	}
	
	public static void main(String args[]) throws IOException {
		GameServer server = new GameServer();
		server.createAndListenSocket();
	}
	
	public void createAndListenSocket() {
		try {
			socket = new DatagramSocket(port); // listen on port ____
			byte[] incomingData = new byte[1024]; // initialize incoming data
			DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length); // get incoming packet

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
		System.out.println(message);
		String[] check = message.split(":"); // splits string into method and value

		if (check[0].equals("get")) {
			String response = OneY + ":" + TwoY;
			byte[] data = response.getBytes();
			replyPacket = new DatagramPacket(data, data.length, IPAddress, port);
			socket.send(replyPacket);
		} else if (check[0].equals("p1plus")) {
			// calls p1plus method
			System.out.println(check[0]);
			System.out.println(check[1]);
			double num = Double.parseDouble(check[1]);
			p1plus(num);
			System.out.print(OneY);
		} else if (check[0].equals("p1minus")) {
			// calls p1minuus method
			System.out.println(check[0]);
			System.out.println(check[1]);
			double num = Double.parseDouble(check[1]);
			p1minus(num);
		} else if (check[0].equals("SetP1Y")) {
			// calls SetP1Y method
			System.out.println(check[0]);
			System.out.println(check[1]);
			double num = Double.parseDouble(check[1]);
			SetP1Y(num);
		}
//		} else if (check[0].equals("PlayerTwoYplus")) {
//			// calls SetPlayerTwoY
//			System.out.println(check[0]);
//			System.out.println(check[1]);
//		} else if (check[0].equals("PlayerTwoYminus")) {
//			// calls SetPlayerTwoY
//			System.out.println(check[0]);
//			System.out.println(check[1]);
//		} else if (check[0].equals("GetPlayerOneX")) { // check to make sure its DATA tag
//			// calls SetPlayerOneX
//			System.out.println(check[0]);
//			System.out.println(check[1]);
//		} else if (check[0].equals("GetPlayerOneY")) {
//			// calls SetPlayerOneY
//			System.out.println(check[0]);
//			System.out.println(check[1]);
//		} else if (check[0].equals("GetPlayerTwoX")) {
//			// calls SetPlayerTwoX
//			System.out.println(check[0]);
//			System.out.println(check[1]);
//		} else if (check[0].equals("GetPlayerTwoY")) {
//			// calls SetPlayerTwoY
//			System.out.println(check[0]);
//			System.out.println(check[1]);
//		}
	}
	
	public static double GetBallX() {
		return BallX;	
	}
	
	
	public static void SetBallX(double x) {
		BallX = x;
	}

	
	public static double GetBallY() {
		return BallY;
	}
	
	public static void SetBallY(double y) {
		BallY = y;	
	}
	
	public static void SetP1Y(double y) {
		OneY = y;
	}
	
	public static double GetP1Y() {
		return OneY;	
	}
	
	public static void p1plus(double y) {
		OneY = OneY + y;
	}
	
	public static void p1minus(double y) {
		OneY = OneY - y;
	}
	
	public static double GetPlayer2Y() {
		return TwoY;
	}
	
	public static void p2plus(double y) {
		TwoY = TwoY + y;
	}
	
	public static void p2minus(double y) {
		TwoY = TwoY - y;
	}
}

//InetAddress IPAddress = incomingPacket.getAddress(); //get address of sender
// int port = incomingPacket.getPort(); //get port of sender
// String reply = ("ACK " + seqNumber + " \n"); //create ACK string
// byte[] data = reply.getBytes(); //convert to byte data
// replyPacket = new DatagramPacket(data, data.length, IPAddress, port);
// //create reply packet
// //if(test%2 == 1) //test, make it fail every other time
// socket.send(replyPacket);
// test++; //testing

// else //if sequence number is wrong, resend the previous ACK
// socket.send(replyPacket);
// if(seqNumber == maxSeq) { //if max seq. number reached, end
// on = false;
// System.out.println("max seq number reached");
// }
