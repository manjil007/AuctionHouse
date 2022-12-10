package Agent;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class StartStage extends VBox {
    Agent agent;

    public StartStage(Agent agent) {
        startSetup();
        this.agent = agent;
    }

    public void startSetup() {
        setSpacing(20);
        setStyle("-fx-padding: 20 40 20 40");

        Label prompt = new Label("Create an account");
        prompt.setStyle("-fx-font: 18 futura; -fx-font-weight: bold");
        addToPane(prompt);

        TextField nameField = new TextField();
        nameField.setPromptText("\uD83D\uDC64 Name");
        nameField.setPrefHeight(20);
        nameField.setFocusTraversable(false);

        TextField balanceField = new TextField();
        balanceField.setPromptText("\uD83D\uDCB2 Balance");
        balanceField.setPrefHeight(20);
        balanceField.setFocusTraversable(false);

        Button submit = new Button("Submit");
        submit.setPrefSize(400,20);
        submit.setDisable(true);
        submit.setFocusTraversable(false);
        submit.setStyle("-fx-base: #e07a55; -fx-text-fill: white");
        submit.setOnAction(event -> {
            if (!balanceField.getText().equals("") &&
                    !nameField.getText().matches("^[0-9]+$")) {
                agent.setName(nameField.getText());
                agent.setTotalBalance(Double.parseDouble(balanceField.getText()));
                agent.setAvailableBalance(Double.parseDouble(balanceField.getText()));
                AgentMain.changeScenes();
            }
        });

        CheckBox agree = new CheckBox("I agree to the Terms of Service.");
        agree.setOnAction(event -> {
            submit.setDisable(!agree.isSelected());
        });
        agree.setFocusTraversable(false);

        addToPane(nameField);
        addToPane(balanceField);
        addToPane(agree);
        addToPane(submit);
    }

    private void addToPane(Node node) {
        getChildren().add(node);
    }
}
