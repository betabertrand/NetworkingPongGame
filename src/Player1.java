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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.concurrent.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Player1 extends Application {

	static String IP = "192.168.1.11"; // This should be your computers IP!!!!!!!!!!!
	static int port = 1200; // Port should match servers!!!!!!!!!!!
	DatagramSocket socket;
	InetAddress IPAddress;
	static Player1 client;
	Rectangle Player1, Player2;
	static double p1y, p2y, ballx;
	static boolean left = true;
	
	boolean on = true;

	public Player1() {
		
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
		p1y = 200;
		p2y = 200;
		Player1 = createRectangle(50, p1y);
		Player2 = createRectangle(550, p2y);
		
		Circle ball = new Circle(6);
		ballx= 300;
		ball.setCenterX(ballx);
		ball.setCenterY(200);
		ball.setFill(Color.WHITE);
		
		Text title = new Text("Networking Pong (Player 1)");
		title.setY(15);
		title.setX(250);
		title.setFill(Color.AQUA);
		
		Text t = new Text("Server: " + IP);
		t.setY(390);
		t.setX(250);
		t.setFill(Color.ALICEBLUE);
		
		Text win = new Text();
		win.setY(220);
		win.setX(100);
		win.setFill(Color.DARKORANGE);
		win.setFont(Font.font(50));
		
		final Group group = new Group(Player1, Player2, t, ball, title);
		final Scene scene = new Scene(group, 600, 400, Color.BLACK);

		sendMovesOnKeyPress(scene, Player1, Player2); // key event sends server moves

		stage.setScene(scene);
		
		client = new Player1();
		client.createAndListenSocket(IP);
		
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						while(on) {
						detectCollison();
						String[] check = client.get();
						System.out.println("From Server: " + "P1Y: " + check[0] + "P2Y: " + check[1] + "BallX:" + check[2] );
						p1y = Double.parseDouble(check[0]);
						p2y = Double.parseDouble(check[1]);
						ballx = Double.parseDouble(check[2]);
						}
						return null;
					}

					private void detectCollison() {
						if( ballx == 60 && ((p1y > 197 && p1y < 227) || (p1y > 173 && p1y < 203))) {
							left = false;
						} else if ( ballx == 550 && ((p2y > 197 && p2y < 227) || (p2y > 173 && p2y < 203))) {
							left = true;
						}
					}
				};
			}
		};
		service.start();	
		
		Service<Void> ballService = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						while(on) {
							Thread.sleep(5);
							ballUpdate();
						}
						return null;
					}

					private void ballUpdate() throws InterruptedException {
					if(left) {
						ballx = ballx - 1;
					} else {
						ballx = ballx + 1;
					}
					client.sendMessage("ball:" + ballx);
					}
				};
			}
		};
		
		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent event) { 
	            ballService.start();
	        }
	    });
		
		new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
					Player1.setY(p1y);
	        			Player2.setY(p2y);
	        			if(ballx == -10) {
	        				win.setText("Player Two Wins");
	        				group.getChildren().add(win);
	        			} else if (ballx == 610) {
	        				win.setText("Player One Wins");
	        				group.getChildren().add(win);
	        			} else {
	        			ball.setCenterX(ballx);
	        			}
            }
        }.start(); 
		
		stage.show();

	}
	
	String[] get() throws IOException {
		// TODO Auto-generated method stub
		socket.setSoTimeout(5000);
		byte[] data = "get:".getBytes(); // convert to bytes
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port); // convert to packet
		socket.send(sendPacket); // send the packet
		byte[] incomingData = new byte[1024];
		DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
		socket.receive(incomingPacket);
		String response = new String(incomingPacket.getData());
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
					if(!(p1y == 0)) client.sendMessage("p1plus:");
					break;
				case DOWN:
					if(!(p1y == 370)) client.sendMessage("p1minus:");
					break;
				case ESCAPE:  
					client.sendMessage("end:");
					System.exit(0);
					break;
				}
			}
		});
	}
}