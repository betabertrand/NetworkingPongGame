import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.concurrent.*;

public class Client extends Application {

	static String IP = "10.200.53.133"; // This should be your computers IP!!!!!!!!!!!
	static int port = 1114; // Port should match servers!!!!!!!!!!!
	private static final int PADDLE_MOVEMENT = 10;
	DatagramSocket socket;
	InetAddress IPAddress;
	static Client client;
	Rectangle Player1, Player2;
	static double p1y = 50, p2y = 50;
	boolean on = true;

	public Client() {
		
	}

	public void createAndListenSocket(String address) {
		try {
			socket = new DatagramSocket(); // create new socket
			IPAddress = InetAddress.getByName(address); // set IP address
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args); // invokes JavaFX start method
	}

	@Override
	public void start(Stage stage) throws Exception {
		// final Circle Ball = new Circle();

		Player1 = createRectangle(50, 200);
		Player2 = createRectangle(550, 200);
		final Group group = new Group(Player1, Player2);
		final Scene scene = new Scene(group, 600, 400, Color.BLACK);

		sendMovesOnKeyPress(scene, Player1, Player2); // key event sends server moves

		stage.setScene(scene);
		
		client = new Client();
		client.createAndListenSocket(IP);
		
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						while(on) {
						Thread.sleep(5000);
						String[] check = client.get();
						p1y = Double.parseDouble(check[0]);
						p2y = Double.parseDouble(check[1]);
						}
						return null;
					}
				};
			}
		};
		service.start();	
		
		new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
					Player1.setY(p1y);
	        			Player2.setY(p2y);
            }
        }.start(); 
		
		stage.show();

	}
	
	private String[] get() throws IOException {
		// TODO Auto-generated method stub
		socket.setSoTimeout(5000);
		byte[] data = "get:".getBytes(); // convert to bytes
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port); // convert to packet
		socket.send(sendPacket); // send the packet
		byte[] incomingData = new byte[1024];
		DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
		socket.receive(incomingPacket);
		String response = new String(incomingPacket.getData());
		System.out.println("From Server:" + response);
		String[] check = response.split(":");
		return check;
	}

	private Rectangle createRectangle(int x, int y) {
		final Rectangle rect = new Rectangle(10, 30, Color.DARKMAGENTA);
		rect.setX(x);
		rect.setY(y);
		rect.setOpacity(0.7);
		return rect;
	}

	public void sendMessage(String message) { // send the String message
		try {
			socket.setSoTimeout(5000);
			byte[] data = message.getBytes(); // convert to bytes
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port); // convert to packet
			socket.send(sendPacket); // send the packet
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendMovesOnKeyPress(Scene scene, final Rectangle rect, Rectangle rect1) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case UP:
					client.sendMessage("p1plus");
					break;
				// rect.setY(rect.getY() - KEYBOARD_MOVEMENT_DELTA); break;
				case DOWN:
					client.sendMessage("p1minus");
					break;
				case ESCAPE: 
					System.exit(0);
					break;
					// rect.setY(rect.getY() + KEYBOARD_MOVEMENT_DELTA); break;
				case U:
					rect1.setY(rect1.getY() - PADDLE_MOVEMENT);
					break;
				case J:
					rect1.setY(rect1.getY() + PADDLE_MOVEMENT);
					break;
				default:
					break;
				}
			}
		});
	}
}


