import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Paddle extends Rectangle {

	public Paddle(int x, int y) {
		this.setX(x);
		this.setY(y);
		this.setOpacity(.7);
	}

	
}
