package Agent;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.util.LinkedList;

public class AuctionGUI extends VBox {
    String[] itemList;
    private int itemIndex = 0;

    LinkedList<AuctionItemGUI> auctionItemPane = new LinkedList<>();

    public AuctionGUI(String[] itemList) {
        this.itemList = itemList;
        populateItems();
        setupStage();
        reloadItem();
    }

    private void setupStage() {
        setSpacing(15);
        setStyle("-fx-padding: 10 20 15 20; -fx-background-color: white;");
    }

    private void populateItems() {
        for (String item : itemList) {
            auctionItemPane.add(new AuctionItemGUI(item));
        }
    }

    public void addItem(String item) {
        if (!containsItem(item)) {
            auctionItemPane.add(new AuctionItemGUI(item));
        }
    }
    public boolean containsItem(String item) {
        for (AuctionItemGUI itemInAH : auctionItemPane) {
            if (itemInAH.getItem().equals(item)) return true;
        }
        return false;
    }

    public void reloadItem() {
        if (!auctionItemPane.isEmpty()) {
            addToStage(auctionItemPane.get(itemIndex));
        }
    }

    public void getNextItem() {
        if (auctionItemPane.isEmpty()) return;
        getChildren().clear();
        if (itemIndex == auctionItemPane.size()-1) itemIndex = 0;
        else itemIndex++;
        addToStage(auctionItemPane.get(itemIndex));
    }

    public void getPrevItem() {
        if (auctionItemPane.isEmpty()) return;
        getChildren().clear();
        if (itemIndex == 0) itemIndex = auctionItemPane.size()-1;
        else itemIndex--;
        addToStage(auctionItemPane.get(itemIndex));
    }

    public void updateStatus(String status, String item) {
        for (AuctionItemGUI itemPane : auctionItemPane) {
            if (itemPane.getItem().equals(item)) {
                itemPane.updateStatus(status);
                break;
            }
        }
    }

    public double getCurrentBid() {
        return auctionItemPane.get(itemIndex).getCurrentBid();
    }

    public String getCurrentItem() {
        return auctionItemPane.get(itemIndex).getItem();
    }

    public void setItemBid(String name, double bid, boolean otherBid) {
        for (AuctionItemGUI itemPane : auctionItemPane) {
            if (itemPane.getItem().equals(name)) {
                itemPane.setCurrentBid(bid,otherBid);
            }
        }
    }

    public void addToStage(Node node) {
        getChildren().add(node);
    }
}
