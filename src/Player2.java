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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.concurrent.*;

public class Player2 extends Application {

	static String IP = "10.200.60.48"; // This should be your computers IP!!!!!!!!!!!
	static int port = 1200; // Port should match servers!!!!!!!!!!!
	DatagramSocket socket;
	InetAddress IPAddress;
	static Player2 client;
	Rectangle Player1, Player2;
	static double p1y, p2y, ballx;
	boolean on = true;

	public Player2() {
		
	}

	public void createAndListenSocket(String address) throws IOException {
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
		p1y = 200;
		p2y = 200;
		Player1 = createRectangle(50, p1y);
		Player2 = createRectangle(550, p2y);
		
		Circle ball = new Circle(6);
		ballx = 300;
		ball.setLayoutX(ballx);
		ball.setLayoutY(200);
		ball.setFill(Color.WHITE);
		
		Text title = new Text("Networking Pong");
		title.setY(15);
		title.setX(250);
		title.setFill(Color.AQUA);
		
		Text t = new Text("Server: " + IP);
		t.setY(390);
		t.setX(250);
		t.setFill(Color.ALICEBLUE);
		
	
		final Group group = new Group(Player1, Player2, t, ball, title);
		final Scene scene = new Scene(group, 600, 400, Color.BLACK);

		sendMovesOnKeyPress(scene, Player1, Player2); // key event sends server moves

		stage.setScene(scene);
		
		client = new Player2();
		client.createAndListenSocket(IP);
		
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						while(on) {
						String[] check = client.get();
						p1y = Double.parseDouble(check[0]);
						p2y = Double.parseDouble(check[1]);
						ballx = Double.parseDouble(check[2]);
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
	        			ball.setLayoutX(ballx);
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

	private Rectangle createRectangle(double x, double y) {
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
			e.printStackTrace();
		}
	}

	private void sendMovesOnKeyPress(Scene scene, final Rectangle rect, Rectangle rect1) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case UP:
					if( !(p2y == 0)) client.sendMessage("p2plus:");
					break;
				case DOWN:
					if(!(p2y == 370)) client.sendMessage("p2minus:");
					break;
				case ESCAPE: 
					System.exit(0);
					break;
				}
			}
		});
	}
}


