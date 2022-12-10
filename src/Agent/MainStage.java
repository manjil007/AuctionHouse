package Agent;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.util.LinkedList;

public class MainStage extends VBox {
    TabPane auctionHouses = new TabPane();
    LinkedList<AuctionGUI> tabs = new LinkedList<>();
    private int ahNumber = 1;

    public MainStage(AmountPane wallet, InteractionHandler interactionPane) {
        auctionHouses.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        auctionHouses.setTabMinWidth(60);
        auctionHouses.setStyle("-fx-base: white; -fx-font: 13 sansserif;" +
                "-fx-font-weight: bold");

        addToStage(auctionHouses);
        addToStage(interactionPane);
        addToStage(wallet);
    }

    public void addAuctionHouse(AuctionGUI auctionPane) {
        auctionHouses.getTabs().add(new Tab("AH"+ahNumber,auctionPane));
        tabs.add(auctionPane);
        ahNumber++;
    }

    public void navigateNext() {
        if (!tabs.isEmpty()) {
            tabs.get(getCurrentAH()).getNextItem();
        }
    }

    public void navigatePrev() {
        if (!tabs.isEmpty()) {
            tabs.get(getCurrentAH()).getPrevItem();
        }
    }

    public int getCurrentAH() {
        return auctionHouses.getSelectionModel().getSelectedIndex();
    }

    public double getCurrentBid() {
        return tabs.get(getCurrentAH()).getCurrentBid();
    }

    public String getCurrentItem() {
        return tabs.get(getCurrentAH()).getCurrentItem();
    }

    public void addToStage(Node node) {
        getChildren().add(node);
    }
}
