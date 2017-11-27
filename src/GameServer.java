import java.io.*;
import java.net.*;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;

public class GameServer {

	static double OneX;
	static double OneY;
	static double TwoX;
	static double TwoY;
	static double BallY;
	static double BallX;
	static Rectangle One;
	static Rectangle Two;
	static Circle Ball;
	
	
	public static void main(String args[]) throws IOException {
		ServerSocket s = new ServerSocket(56798);
		Socket s1=s.accept(); 
		Socket s2=s.accept();
		DataInputStream oneInput = new DataInputStream(s1.getInputStream());
		DataInputStream twoInput = new DataInputStream(s2.getInputStream());
		DataOutputStream oneOutput = new DataOutputStream(s1.getOutputStream());
		DataOutputStream twoOutput = new DataOutputStream(s2.getOutputStream());
		
		while (true) {
			
		}
	}
	
	public GameServer(Rectangle one, Rectangle two, Circle ball) {
		BallX = ball.getCenterX();
		BallY = ball.getCenterY();
		OneX = one.getX();
		OneY = one.getY();
		TwoX = two.getX();
		TwoY = two.getY();
	}
	
	public static double GetBallX() {
		return BallX;	
	}
	
	public static double SetBallX(double x) {
		return BallX = x;
	}
	
	public static double GetBallY() {
		return BallY;
	}
	
	public static double SetBallY(double y) {
		return BallY = y;	
	}
	
	public static double GetPlayer1X() {
		return OneX;
	}
	
	public static double SetPlayer1X(double x) {
		return OneX = x;
	}
	
	public static double GetPlayer1Y() {
		return OneY;	
	}
	
	public static double SetPlayer1Y(double y) {
		return OneY = y;
	}
	
	public static double GetPlayer2X() {
		return TwoX;
	}
	
	public static double SetPlayer2X(double x) {
		return TwoX = x;
	}
	
	public static double GetPlayer2Y() {
		return TwoY;
	}
	
	public static double SetPlayer2Y(double y) {
		return TwoY = y;
	}
}
