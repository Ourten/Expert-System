package fr.expertsystem.gui;

import fr.expertsystem.Main;
import fr.expertsystem.data.Fact;
import fr.expertsystem.data.FactState;
import fr.expertsystem.data.GlobalState;
import fr.expertsystem.data.Rule;
import fr.expertsystem.parser.Parser;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class Visualiser extends Application
{
    private static GlobalState   state;
    private static Parser.Result parsed;
    private static List<Rule>    rules;

    public static void start(Parser.Result parsed, GlobalState state, List<Rule> rules)
    {
        Visualiser.state = state;
        Visualiser.parsed = parsed;
        Visualiser.rules = rules;

        Application.launch();
    }

    @Override
    public void start(Stage stage)
    {
        HBox root = new HBox();
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add("/fr/expertsystem/css/style.css");

        stage.setTitle("Expert-System");
        stage.setScene(scene);
        stage.show();

        root.getChildren().add(this.setupFactPane());
        Pane rulePane = this.setupRulePane();
        root.getChildren().add(rulePane);
        HBox.setHgrow(rulePane, Priority.ALWAYS);
        root.getChildren().add(this.setupQueryPane());
    }

    private Pane setupQueryPane()
    {
        VBox queryPane = new VBox();
        queryPane.setPrefWidth(250);

        ListView<QueryBox> queryListView = new ListView<>();

        queryListView.setItems(parsed.getQueryFacts().stream().map(fact -> new QueryBox(fact, state, queryListView))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));

        queryPane.getChildren().add(queryListView);

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
            state = Main.runSolver(parsed.getInitialFacts(), parsed.getQueryFacts(), rules);
            queryListView.getItems().add(new QueryBox(fact, state, queryListView));
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

        queryPane.getChildren().add(addBox);
        return queryPane;
    }

    private Pane setupRulePane()
    {
        AnchorPane rulePane = new AnchorPane();

        ListView<String> ruleListView = new ListView<>();
        ruleListView.setMouseTransparent(true);
        ruleListView.setFocusTraversable(false);
        ruleListView.setId("ruleListView");
        ruleListView.setItems(parsed.getRules().stream().map(Rule::toString)
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));

        rulePane.getChildren().add(ruleListView);
        AnchorPane.setLeftAnchor(ruleListView, 0D);
        AnchorPane.setRightAnchor(ruleListView, 0D);
        AnchorPane.setTopAnchor(ruleListView, 0D);
        AnchorPane.setBottomAnchor(ruleListView, 0D);

        return rulePane;
    }

    private Pane setupFactPane()
    {
        VBox factsPane = new VBox();
        factsPane.setPrefWidth(250);

        ListView<FactBox> factListView = new ListView<>();

        factListView.setItems(parsed.getInitialFacts().stream().map(fact -> new FactBox(fact, factListView))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));

        factsPane.getChildren().add(factListView);

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
            factListView.getItems().add(new FactBox(new Fact(newFact.getText()), factListView));
            addFact.setDisable(true);
            newFact.setText("");
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

        factsPane.getChildren().add(addBox);

        return factsPane;
    }

    private static class FactBox extends BorderPane
    {
        private Fact fact;

        public FactBox(Fact fact, ListView<FactBox> listView)
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

    private static class QueryBox extends BorderPane
    {
        private Fact fact;

        public QueryBox(Fact fact, GlobalState state, ListView<QueryBox> listView)
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
