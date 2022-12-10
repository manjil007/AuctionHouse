package Bank;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application{
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        BankManager manager = new BankManager();
        Thread managerThread = new Thread(manager);
        managerThread.start();

        Label ipAddress = new Label("IP: " + manager.getIpAddress());
        ipAddress.setFont(new Font("Arial", 20));
        Label portNumber = new Label("Port no: " + manager.getPortNo());
        portNumber.setFont(new Font("Arial", 20));

        AnchorPane box = new AnchorPane();
        AnchorPane.setBottomAnchor(ipAddress, 250.0);
        AnchorPane.setLeftAnchor(ipAddress, 100.0);
        AnchorPane.setLeftAnchor(portNumber, 100.0);
        AnchorPane.setBottomAnchor(portNumber, 220.0);
        ObservableList list = box.getChildren();
        list.addAll(ipAddress, portNumber);

        primaryStage.setScene(new Scene(box, 400, 400));
        primaryStage.setTitle("Bank Network Information");


        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
    }
}
