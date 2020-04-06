package game;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
	public static final int Column = 7;
	public static final int row = 6;
	public static final int discrad = 79;
	public static final String disclr1 = "#D11F1F";
	public static final String disclr2 = "#000000";
	public static String player1 = "Player One";
	public static String player2 = "Player Two";
	private boolean isPlayer1turn = true;
	private Disc[][] insdiscarray = new Disc[row][Column];

	@FXML
	public GridPane rootgrid;
	@FXML
	public Pane discpane;
	@FXML
	public Label playername;
	@FXML
	public TextField player1name, player2name;
	@FXML
	public Button setname;
	//@FXML
	//public ComboBox colorplayer1, colorplayer2;
	private boolean isallowedtoinsert = true;

	public void createPlayground() {
		Shape rectholes = gameplay1();
		rectholes.setFill(Color.WHITE);
		rootgrid.add(rectholes, 0, 1);
		List<Rectangle> rectangleList = clickrect();
		for (Rectangle rectangle : rectangleList) {
			rootgrid.add(rectangle, 0, 1);
		}
		setname.setOnAction(event -> {
			names();
		});
	}

	public void names() {
		String name1 = player1name.getText();
		String name2 = player2name.getText();
		player1 = name1;
		player2 = name2;
		playername.setText(player1name.getText());

	}

	private Shape gameplay1() {
		Shape rectholes = new Rectangle((Column + 1) * discrad, (row * discrad) + 50);
		for (int ro = 0; ro < row; ro++) {
			for (int col = 0; col < Column; col++) {
				Circle circle = new Circle();
				circle.setRadius(discrad / 2);
				circle.setCenterX(discrad / 2);
				circle.setCenterY(discrad / 2);
				circle.setSmooth(true);
				circle.setTranslateX(col * (discrad + 5) + 20);
				circle.setCenterY(ro * (discrad + 5) + 50);
				rectholes = Shape.subtract(rectholes, circle);

			}
		}
		return rectholes;
	}

	private List<Rectangle> clickrect() {
		List<Rectangle> rectangleList = new ArrayList<>();
		for (int col = 0; col < Column; col++) {
			Rectangle rectangle = new Rectangle(discrad, (row * discrad) + 50);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (discrad + 5) + 20);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee70")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			final int column = col;
			rectangle.setOnMouseClicked(event -> {
				if (isallowedtoinsert) {
					isallowedtoinsert = false;

					insertdisc(new Disc(isPlayer1turn), column);
				}
			});
			rectangleList.add(rectangle);

		}


		return rectangleList;
	}

	private void insertdisc(Disc disc, int column) {
		int ro = row - 1;
		while (ro >= 0) {
			if (getdiscifpresent(ro, column) == null)
				break;
			ro--;
		}
		if (ro < 0)
			return;

		insdiscarray[ro][column] = disc;
		discpane.getChildren().add(disc);
		disc.setTranslateX(column * (discrad + 5) + 20);
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), disc);

		translateTransition.setToY(ro * (discrad + 5) + 11);
		int currentrow = ro;
		translateTransition.setOnFinished(event -> {
			isallowedtoinsert = true;
			if (gameended(currentrow, column)) {
				gameover();
				return;

			}

			isPlayer1turn = !isPlayer1turn;
			playername.setText(isPlayer1turn ? player1 : player2);

		});
		translateTransition.play();

	}

	private boolean gameended(int ro, int column) {
		List<Point2D> verticalpoints = IntStream.rangeClosed(ro - 3, ro + 3)
				.mapToObj(r -> new Point2D(r, column))
				.collect(Collectors.toList());
		List<Point2D> horizontalpoints = IntStream.rangeClosed(column - 3, column + 3)
				.mapToObj(col -> new Point2D(ro, col))
				.collect(Collectors.toList());
		Point2D startpoint1 = new Point2D(ro - 3, column + 3);
		List<Point2D> diag1points = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> startpoint1.add(i, -i))
				.collect(Collectors.toList());
		Point2D startpoint2 = new Point2D(ro - 3, column - 3);
		List<Point2D> diag2points = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> startpoint2.add(i, i))
				.collect(Collectors.toList());
		boolean isended = checkcombinations(verticalpoints) || checkcombinations(horizontalpoints)
				|| checkcombinations(diag1points) || checkcombinations(diag2points);
		return isended;
	}

	private boolean checkcombinations(List<Point2D> points) {
		int chain = 0;
		for (Point2D point : points) {

			int rowindexarray = (int) point.getX();
			int columnindexarray = (int) point.getY();
			Disc disc = getdiscifpresent(rowindexarray, columnindexarray);
			if (disc != null && disc.isplayer1move == isPlayer1turn) {
				chain++;
				if (chain == 4) {
					return true;
				}
			} else {
				chain = 0;
			}

		}
		return false;
	}

	private Disc getdiscifpresent(int ro, int column) {
		if (ro >= row || ro < 0 || column >= Column || column < 0)
			return null;
		else
			return insdiscarray[ro][column];
	}

	private void gameover() {
		String winner = isPlayer1turn ? player1 : player2;
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect 4");
		alert.setHeaderText("Well played " + winner + ", you are the winner!!!!");
		alert.setContentText("Want to Play again?");
		ButtonType yesbutton = new ButtonType("yes");
		ButtonType nobutton = new ButtonType("no,exit");
		alert.getButtonTypes().setAll(yesbutton, nobutton);
		Platform.runLater(() -> {
			Optional<ButtonType> btnclicked = alert.showAndWait();
			if (btnclicked.isPresent() && btnclicked.get() == yesbutton) {
				resetgame();
			} else {
				Platform.exit();
				System.exit(0);
			}

		});


	}

	public void resetgame() {
		discpane.getChildren().clear();
		for (int ro = 0; ro < insdiscarray.length; ro++) {
			for (int col = 0; col < insdiscarray[ro].length; col++) {
				insdiscarray[ro][col] = null;

			}

		}
		isPlayer1turn = true;
		playername.setText(player1);
		createPlayground();
	}

	private static class Disc extends Circle {
		private final boolean isplayer1move;

		public Disc(boolean isplayer1move) {
			this.isplayer1move = isplayer1move;
			setRadius(discrad / 2);
			setFill(!isplayer1move ? Color.valueOf(disclr2) : Color.valueOf(disclr1));
			setCenterX(discrad / 2);
			setCenterY(discrad / 2);
		}
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	private static String choosecolor() {
		String chosecolor[] = {"Black", "Green", "Red", "Yellow", "Blue", "Purple", "Olive", "Grey", "Indigo", "Magenta"};
		ComboBox colorplayer1 = new ComboBox();
		colorplayer1.setItems(FXCollections.observableArrayList(chosecolor));
		/*ComboBox colorplayer1 = new ComboBox(FXCollections.observableArrayList(chosecolor));
		ComboBox colorplayer2 = new ComboBox(FXCollections.observableArrayList(chosecolor));*/

		final String[] selected1 = {"choose color for " + player1};
		EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				String selected1 = (String) colorplayer1.getValue();
			}
		};
		return String.valueOf(selected1);
	}
}
