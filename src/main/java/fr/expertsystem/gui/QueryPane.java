package fr.expertsystem.gui;

import fr.expertsystem.Main;
import fr.expertsystem.data.Fact;
import fr.expertsystem.data.FactState;
import fr.expertsystem.data.GlobalState;
import fr.expertsystem.data.Rule;
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

public class QueryPane extends VBox
{
    private ListView<QueryBox> queryListView;
    private Parser.Result      parsed;
    private Visualiser         visualiser;

    public QueryPane(Visualiser visualiser, Parser.Result parsed, List<Rule> rules)
    {
        this.setPrefWidth(250);

        this.parsed = parsed;
        this.visualiser = visualiser;
        queryListView = new ListView<>();
        this.getChildren().add(queryListView);

        HBox addBox = new HBox();

        TextField newQuery = new TextField();
        newQuery.setPrefWidth(150);
        newQuery.setOnKeyTyped(e ->
        {
            if (!e.getCharacter().matches("[A-Z]") || newQuery.getText().length() >= 1)
                e.consume();
            if (newQuery.getText().length() == 0 && e.getCharacter().matches("[a-z]"))
            {
                newQuery.setText(e.getCharacter().toUpperCase());
                newQuery.positionCaret(1);
            }
        });

        Button addQuery = new Button("Add Query");
        addQuery.setOnAction(e ->
        {
            Fact fact = new Fact(newQuery.getText());
            parsed.getQueryFacts().add(fact);
            visualiser.setState(Main.runSolver(parsed.getInitialFacts(), parsed.getQueryFacts(), rules));
            queryListView.getItems().add(new QueryBox(parsed, fact, visualiser.getState(), queryListView));
            addQuery.setDisable(true);
            newQuery.setText("");
        });
        newQuery.textProperty().addListener(obs ->
        {
            if (newQuery.getText().length() == 1 && queryListView.getItems().stream().noneMatch(box -> box.getFact().getID().equals(newQuery.getText())))
                addQuery.setDisable(false);
            else
                addQuery.setDisable(true);
        });
        addQuery.setDisable(true);

        HBox.setHgrow(newQuery, Priority.ALWAYS);
        addBox.getChildren().addAll(newQuery, addQuery);

        this.getChildren().add(addBox);
    }

    public void refreshQuery()
    {
        queryListView.setItems(parsed.getQueryFacts().stream().map(fact ->
                new QueryBox(parsed, fact, visualiser.getState(), queryListView))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    private static class QueryBox extends BorderPane
    {
        private Fact fact;

        public QueryBox(Parser.Result parsed, Fact fact, GlobalState state, ListView<QueryBox> listView)
        {
            Label label = new Label(fact.getID() + " - " + state.getFactState(fact));
            label.getStyleClass().add("fact-label");

            if (state.getFactState(fact) == FactState.TRUE)
                label.getStyleClass().add("query-true");
            else
                label.getStyleClass().add("query-false");
            this.setLeft(label);

            Button remove = new Button("Remove");
            remove.getStyleClass().add("remove-button");
            remove.setPadding(Insets.EMPTY);
            remove.setOnAction(e ->
            {
                listView.getItems().remove(this);
                parsed.getQueryFacts().remove(fact);
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
