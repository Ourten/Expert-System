package fr.expertsystem.gui;

import fr.expertsystem.Main;
import fr.expertsystem.data.GlobalState;
import fr.expertsystem.data.Rule;
import fr.expertsystem.parser.Parser;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
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

    private FactsPane factsPane;
    private QueryPane queryPane;

    @Override
    public void start(Stage stage)
    {
        HBox root = new HBox();
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 1000, 400);
        scene.getStylesheets().add("/fr/expertsystem/css/style.css");

        stage.setTitle("Expert-System");
        stage.setScene(scene);
        stage.show();

        this.factsPane = new FactsPane(this, parsed);
        root.getChildren().add(factsPane);
        Pane rulePane = this.setupRulePane();
        root.getChildren().add(rulePane);
        HBox.setHgrow(rulePane, Priority.ALWAYS);

        this.queryPane = new QueryPane(this, parsed, rules);
        root.getChildren().add(queryPane);
    }

    void refreshGraph()
    {
        this.setState(Main.runSolver(parsed.getInitialFacts(), parsed.getQueryFacts(), rules));
        this.queryPane.refreshQuery();
    }

    void setState(GlobalState state)
    {
        Visualiser.state = state;
    }

    GlobalState getState()
    {
        return Visualiser.state;
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
}
