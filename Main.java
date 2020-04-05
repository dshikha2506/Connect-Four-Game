package game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
       FXMLLoader loader= new FXMLLoader(getClass().getResource("gamescreen.fxml"));
        GridPane rootgrid=loader.load();
        controller=loader.getController();
        controller.createPlayground();
        MenuBar menuBar=createMenu();
        Pane menupane=(Pane) rootgrid.getChildren().get(0);
        menupane.getChildren().add(menuBar);
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        Scene scene= new Scene(rootgrid);
        primaryStage.setScene(scene);
        primaryStage.setTitle("connect 4");
        primaryStage.setResizable(false);
        primaryStage.show();

    }
    private MenuBar createMenu()
    {
        Menu filemenu= new Menu("File");
        MenuItem newgame= new MenuItem("New Game");
        newgame.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    controller.resetgame();
                                }
                            }
        );
        MenuItem resetgame= new MenuItem("Reset Game");
        resetgame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.resetgame();
            }
        });
        SeparatorMenuItem separator=new SeparatorMenuItem();
        MenuItem exitgame= new MenuItem("Exit Game");
        exitgame.setOnAction(event -> exitgame());

        filemenu.getItems().addAll(newgame,resetgame,separator,exitgame);
        Menu helpmenu=new Menu("Help");
        MenuItem aboutgame= new MenuItem("About Game");
        aboutgame.setOnAction(event -> aboutc4game());

        SeparatorMenuItem separatorabt=new SeparatorMenuItem();
        MenuItem aboutme= new MenuItem("About Me");
        aboutme.setOnAction(event -> aboutme());

        helpmenu.getItems().addAll(aboutgame,separatorabt,aboutme);
        MenuBar menuBar=new MenuBar();
        menuBar.getMenus().addAll(filemenu,helpmenu);
        return menuBar;
    }

    private void aboutme() {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the Developer");
        alert.setHeaderText("Deepshikha");
        alert.setContentText("I think you came to the wrong section."+"You have got the game, so please play." +
                "And send me feedback,if you like it or not.You can mail me on : d.shikha2506@gmail.com." +
                "THANK YOU!!!");
        alert.show();
    }

    private void aboutc4game() {
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the 'Connect 4' Game");
        alert.setTitle("How to Play the Game?");
        alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored discs from the top into a seven-column, six-row vertically suspended grid. The pieces fall straight down, occupying the next available space within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game. The first player can always win by playing the right moves.In my language \"chaar go gola milaao.... aur jeet jaao\".");
        alert.show();


    }

    private void exitgame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetgame() {
        
    }


    public static void main(String[] args) {
        launch(args);
    }
}
