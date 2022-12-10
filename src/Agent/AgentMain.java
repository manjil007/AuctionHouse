package Agent;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;


public class AgentMain extends Application {

    private static Agent agent;
    private static MainStage mainStage;
    private static Stage stage;
    private static String bankIP;
    private static int bankPort;
    private Slider bidSlider;
    private final AmountPane amountPane = new AmountPane();
    private InteractionHandler interactionHandler;
    private Button placeBid, back, next;
    private final HashMap<Integer, AuctionGUI> auctionPanes = new HashMap<>();

    public static void main(String[] args) {
        if (args.length == 2) {
            bankIP = args[0];
            bankPort = Integer.parseInt(args[1]);
        }
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        agent = new Agent(bankIP,bankPort);
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();

        AgentMain.stage = stage;
        initInteractables();
        interactionHandler = new InteractionHandler(bidSlider,placeBid,back,next);
        mainStage = new MainStage(amountPane,interactionHandler);
        StartStage loginStage = new StartStage(agent);

        stage.setTitle("Agent");
        stage.setScene(new Scene(loginStage,340,250));
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> {
            agent.closeSocket();
            System.exit(0);
        });

        stage.show();
    }

    // updates GUI elements
    private void update() {

        if (agent.hasBankId) {
            amountPane.setBankIDLabel(agent.getAccountId());
            agent.clearAccountId();
        }

        bidSlider.setMax(agent.getAvailableBalance());

        amountPane.updateAmount(agent.getTotalBalance(),
                agent.getAvailableBalance());

        if (agent.checkActiveBids()) {
            stage.setOnCloseRequest(event -> event.consume());
        } else {
            stage.setOnCloseRequest(event -> {
                agent.closeSocket();
                System.exit(0);
            });
        }

        if (agent.getAHUpdate()) {
            Integer[] ahIndices = agent.getIndex();
            for (Integer i : ahIndices) {
                if (!auctionPanes.containsKey(i)) {
                    AuctionGUI ap = new AuctionGUI(agent.getListOfItem(i));
                    addAuctionHouse(ap);
                    auctionPanes.put(i,ap);
                }
            }
        }

        if (!auctionPanes.isEmpty()) {
            for (int index : auctionPanes.keySet()) {
                for (String item : agent.getAuctionItemKeys(index)) {
                    auctionPanes.get(index).updateStatus(
                            agent.receiveMessage(index,item),item);
                    auctionPanes.get(index).setItemBid(item,
                            agent.getBidStatus(index, item),
                            agent.checkBidOwner(index, item));
                    if (!auctionPanes.get(index).containsItem(item)) {
                        auctionPanes.get(index).addItem(item);
                    }
                }
            }
        }
        agent.returnRejectedBid();
    }

    public static void changeScenes() {
        Thread t = new Thread(agent);
        t.start();

        stage.setTitle(agent.getName()+"'s Agent");
        stage.setScene(new Scene(mainStage,340,800));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    private void initInteractables() {

        bidSlider = new Slider();
        bidSlider.setPrefSize(400,10);
        bidSlider.setMajorTickUnit(5);
        bidSlider.setMinorTickCount(0);
        bidSlider.setBlockIncrement(5);
        bidSlider.setSnapToTicks(true);
        bidSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<?extends Number> observable,
                                Number oldValue, Number newValue){
                agent.setBidAmount((Double) newValue);
                interactionHandler.updateBidDisplay((Double) newValue);
            }
        });

        placeBid = new Button("Place Bid");
        placeBid.setStyle("-fx-font: 14 sansserif; -fx-font-weight: bold");
        placeBid.setPrefSize(190,30);

        placeBid.setOnAction(event -> {

            if (!agent.isItemActive(mainStage.getCurrentItem(),
                    mainStage.getCurrentAH())) {
            } else if (agent.getBidAmount() > mainStage.getCurrentBid()) {
                String item = mainStage.getCurrentItem();
                int index = mainStage.getCurrentAH();
                try {
                    agent.sendBid(item,index);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bidSlider.setValue(0);
            } else {
                agent.setMessage(mainStage.getCurrentAH(),
                        mainStage.getCurrentItem());
            }
        });

        back = new Button("← Back");
        back.setStyle("-fx-font: 14 sansserif; -fx-font-weight: bold");
        back.setPrefSize(190,10);
        back.setOnAction(event -> {
            mainStage.navigatePrev();
        });
        next = new Button("Next →");
        next.setPrefSize(190,10);
        next.setStyle("-fx-font: 14 sansserif; -fx-font-weight: bold");
        next.setOnAction(event -> {
            mainStage.navigateNext();
        });
    }

    private static void addAuctionHouse(AuctionGUI auctionHouse) {
        mainStage.addAuctionHouse(auctionHouse);
    }
}
