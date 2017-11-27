import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Client extends Application {
  private static final int      KEYBOARD_MOVEMENT_DELTA = 10;

  public static void main(String[] args) { launch(args); }
  @Override public void start(Stage stage) throws Exception {
	//final Circle Ball = new Circle();
	final Rectangle Player1 = createRectangle(50,200);
	final Rectangle Player2 = createRectangle(550,200);
	//GameServer gm = new GameServer(Player1, Player2);
    final Group group = new Group(Player1, Player2);
    
    final Scene scene = new Scene(group, 600, 400, Color.BLACK);
    movePawnsOnKeyPress(scene, Player1, Player2); // to move left pawn use R and F, right use UP and DOWN
    
    stage.setScene(scene);
    stage.show();
  }

  private Rectangle createRectangle(int x, int y) {
	final Rectangle rect = new Rectangle(10, 30, Color.DARKMAGENTA);
	rect.setX(x);
	rect.setY(y);
    rect.setOpacity(0.7);
    return rect;
  }

  private void movePawnsOnKeyPress(Scene scene, final Rectangle rect, Rectangle rect1) {
    scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override public void handle(KeyEvent event) {
        switch (event.getCode()) {
          case UP: rect.setY(rect.getY() - KEYBOARD_MOVEMENT_DELTA); break;
          case DOWN:  rect.setY(rect.getY() + KEYBOARD_MOVEMENT_DELTA); break;
          case U: rect1.setY(rect1.getY() - KEYBOARD_MOVEMENT_DELTA); break;
          case J:  rect1.setY(rect1.getY() + KEYBOARD_MOVEMENT_DELTA); break;
        }
      }
    });
  }
}