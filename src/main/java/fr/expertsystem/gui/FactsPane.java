package fr.expertsystem.gui;

import fr.expertsystem.data.Fact;
import fr.expertsystem.parser.Parser;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class FactsPane extends VBox
{
    private ListView<FactBox> factListView;
    private Parser.Result     parsed;
    private Visualiser        visualiser;

    public FactsPane(Visualiser visualiser, Parser.Result parsed)
    {
        this.setPrefWidth(250);

        this.visualiser = visualiser;
        this.parsed = parsed;

        this.factListView = new ListView<>();
        this.refreshList(parsed.getInitialFacts());

        this.getChildren().add(factListView);

        HBox addBox = new HBox();

        TextField newFact = new TextField();
        newFact.setPrefWidth(150);
        newFact.setOnKeyTyped(e ->
        {
            if (!e.getCharacter().matches("[A-Z]") || newFact.getText().length() >= 1)
                e.consume();
            if (newFact.getText().length() == 0 && e.getCharacter().matches("[a-z]"))
            {
                newFact.setText(e.getCharacter().toUpperCase());
                newFact.positionCaret(1);
            }
        });

        Button addFact = new Button("Add Fact");
        addFact.setOnAction(e ->
        {
            Fact fact = new Fact(newFact.getText());
            factListView.getItems().add(new FactBox(visualiser, fact, factListView, parsed));
            addFact.setDisable(true);
            newFact.setText("");

            parsed.getInitialFacts().add(fact);
            visualiser.refreshGraph();
        });
        newFact.textProperty().addListener(obs ->
        {
            if (newFact.getText().length() == 1 && factListView.getItems().stream().noneMatch(box -> box.getFact().getID().equals(newFact.getText())))
                addFact.setDisable(false);
            else
                addFact.setDisable(true);
        });
        addFact.setDisable(true);

        HBox.setHgrow(newFact, Priority.ALWAYS);
        addBox.getChildren().addAll(newFact, addFact);
        VBox.setVgrow(factListView, Priority.ALWAYS);

        this.getChildren().add(addBox);
    }

    public void refreshList(List<Fact> facts)
    {
        this.factListView.setItems(facts.stream().map(fact -> new FactBox(this.visualiser, fact, factListView, parsed))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    private static class FactBox extends BorderPane
    {
        private Fact fact;

        public FactBox(Visualiser visualiser, Fact fact, ListView<FactBox> listView, Parser.Result parsed)
        {
            Label label = new Label(fact.getID());
            label.getStyleClass().add("fact-label");
            this.setLeft(label);

            Button remove = new Button("Remove");
            remove.getStyleClass().add("remove-button");
            remove.setPadding(Insets.EMPTY);
            remove.setOnAction(e ->
            {
                listView.getItems().remove(this);
                parsed.getInitialFacts().remove(fact);
                visualiser.refreshGraph();
            });
            this.setRight(remove);

            this.setStyle("-fx-background-color: transparent");

            this.fact = fact;
        }

        public Fact getFact()
        {
            return fact;
        }
    }
}
