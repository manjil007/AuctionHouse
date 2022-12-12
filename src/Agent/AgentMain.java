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
//            bankIP = "PROSPERO";//args[0];
//            bankPort = 9090;//Integer.parseInt(args[1]);
        }
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        agent = new Agent(bankIP,bankPort);
        //agent = new Agent("PROSPERO", 9090);//Agent(bankIP,bankPort);
        // animation timer to update gui every half second
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
        // initialize mainPane and stage
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
        // initial update of bankID in GUI
        if (agent.hasBankId) {
            amountPane.setBankIDLabel(agent.getAccountId());
            agent.clearAccountId();
        }

        // Resets the slider so that an agent cannot bid more than available
        bidSlider.setMax(agent.getAvailableBalance());
        // Updates balance in wallet pane
        amountPane.updateAmount(agent.getTotalBalance(),
                agent.getAvailableBalance());

        // Determines whether to allow program exit
        if (agent.checkActiveBids()) {
            stage.setOnCloseRequest(event -> event.consume());
        } else {
            stage.setOnCloseRequest(event -> {
                agent.closeSocket();
                System.exit(0);
            });
        }

        // updates auction panes updates are detected
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

        // Updates status messages, current bids, and adds new items too GUI
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
        // refunds any unblocked funds
        agent.returnRejectedBid();
    }

    // Transitions between log in and landing page
    public static void changeScenes() {
        Thread t = new Thread(agent);
        t.start();

        stage.setTitle(agent.getName()+"'s Agent");
        stage.setScene(new Scene(mainStage,400,800));
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    // Initializes interactable elements
    private void initInteractables() {
        // Bid slider
        bidSlider = new Slider();
        bidSlider.setPrefSize(400,10);
        bidSlider.setMajorTickUnit(5);
        bidSlider.setMinorTickCount(0);
        bidSlider.setBlockIncrement(5);
        bidSlider.setSnapToTicks(true);
        // Bid Slider functionality
        bidSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<?extends Number> observable,
                                Number oldValue, Number newValue){
                agent.setBidAmount((Double) newValue);
                interactionHandler.updateBidDisplay((Double) newValue);
            }
        });

        // Bid submission button
        placeBid = new Button("Place Bid");
        placeBid.setStyle("-fx-font: 14 sansserif; -fx-font-weight: bold");
        placeBid.setPrefSize(190,30);
        // Place bid button functionality
        placeBid.setOnAction(event -> {
            // Only allows submission if item is active and bid is valid
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

        // Navigation buttons
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

    // Adds new auction house to
    private static void addAuctionHouse(AuctionGUI auctionHouse) {
        mainStage.addAuctionHouse(auctionHouse);
    }
}
