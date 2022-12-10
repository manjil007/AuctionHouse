package Auctionhouse;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class Main extends Application {
    private String ip;
    private int port;
    private String inventory;
    private String[] args;

    public static void main(String[] args){
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        FlowPane pane = new FlowPane();
        primaryStage.setScene(new Scene(pane, 300, 300));
        primaryStage.setTitle("Auction House");
        primaryStage.show();

        ip = args[0];
        port = Integer.parseInt(args[1]);
        inventory = args[2];

        AuctionHouse auctionHouse = new AuctionHouse(ip, port, inventory);
        (new Thread(auctionHouse)).start();

        primaryStage.setTitle(getItemTypes());

        Label idLabel = new Label("Bank ID#: " + auctionHouse.getBankId());
        pane.getChildren().addAll(idLabel);
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });

    }

    private String getItemTypes(){
        return null;
    }
}
