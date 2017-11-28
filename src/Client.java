import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.concurrent.*;

public class Client extends Application {
	
	static String IP = "192.168.1.11"; // This should be your computers IP!!!!!!!!!!!
	static int port = 2224; // Port should match servers!!!!!!!!!!!
	private static final int PADDLE_MOVEMENT = 10;
	DatagramSocket socket;
	InetAddress IPAddress;
	static Client client;
	Rectangle Player1, Player2;

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
		client = new Client();
		client.createAndListenSocket(IP);
		launch(args); // invokes JavaFX start method
	}

	@Override 
  public void start(Stage stage) throws Exception {
	//final Circle Ball = new Circle();
	
	int p1y = 50;
	String a = "SetP1Y:" + 50;
	client.sendMessage(a); // networking shtuff

	Player1 = createRectangle(p1y,200);
	Player2 = createRectangle(550,200);
    final Group group = new Group(Player1, Player2);
    final Scene scene = new Scene(group, 600, 400, Color.BLACK);
    
    sendMovesOnKeyPress(scene, Player1, Player2); // key event sends server moves
   
    stage.setScene(scene);
    stage.show();

	  // not really sure about this
	  // purpose is to have a thread running in backround talking to server and updating gui
    // this is not working yet
    // trying to pull position info from server and update graphics
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						client.sendMessage("get:");
						byte[] incomingData = new byte[1024];
						DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
						socket.setSoTimeout(5000);
						socket.receive(incomingPacket);
						String response = new String(incomingPacket.getData());
						String[] check = response.split(":");
						final CountDownLatch latch = new CountDownLatch(1);
						Platform.runLater(new Runnable() { // need to have to make changes to gui inside thread i think
							@Override
							public void run() {
								try {
									Player1.setY(Double.parseDouble(check[0]));
									Player2.setY(Double.parseDouble(check[1]));
								} finally {
									latch.countDown();
								}
							}
						});
						latch.await();
						return null;
					}
				};
			}
		};
		service.start(); 
		
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
					client.sendMessage("p1plus:10");
					break;
				// rect.setY(rect.getY() - KEYBOARD_MOVEMENT_DELTA); break;
				case DOWN:
					client.sendMessage("p1minus:10");
					break;
				case ESCAPE:
					System.exit(0);
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
	
//	public void update() throws IOException, InterruptedException {
//	client.sendMessage("get:");
//	byte[] incomingData = new byte[1024];
//	DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
//	socket.setSoTimeout(5000);
//	socket.receive(incomingPacket);
//	String response = new String(incomingPacket.getData());
//	String[] check = response.split(":");
//	Platform.runLater(new Runnable() {
//		@Override
//		public void run() {
//			Player1.setY(Double.parseDouble(check[0]));
//			Player2.setY(Double.parseDouble(check[1]));
//		}
//	});
//}
	
}
