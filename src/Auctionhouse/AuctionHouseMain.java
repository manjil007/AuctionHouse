package Auctionhouse;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class AuctionHouseMain extends Application {
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

        args = getParameters().getRaw().toArray(new String[2]);
        ip = args[0]; //bank ip
        port = Integer.parseInt(args[1]);  //bank port no
        inventory = args[2]; //.txt file of inventory

        AuctionHouse auctionHouse = new AuctionHouse(ip, port, inventory);
        (new Thread(auctionHouse)).start();
        Label idLabel = new Label("Bank ID#: " + auctionHouse.getBankId());
        pane.getChildren().addAll(idLabel);
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });

    }
}
