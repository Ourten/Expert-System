package fr.expertsystem.gui;

import fr.expertsystem.data.Conditions;
import fr.expertsystem.data.ExpandedRuleMap;
import fr.expertsystem.data.GlobalState;
import fr.expertsystem.data.Rule;
import fr.expertsystem.parser.Parser;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.util.List;
import java.util.stream.Collectors;

public class RulePane extends AnchorPane
{
    private Visualiser visualiser;
    private Parser.Result parsed;
    private ListView<RuleText> ruleListView;

    public RulePane(Visualiser visualiser, Parser.Result parsed)
    {
        this.visualiser = visualiser;
        this.parsed = parsed;

        ruleListView = new ListView<>();
        ruleListView.setMouseTransparent(true);
        ruleListView.setFocusTraversable(false);
        ruleListView.setId("ruleListView");
        this.refreshRules();

        this.getChildren().add(ruleListView);
        AnchorPane.setLeftAnchor(ruleListView, 0D);
        AnchorPane.setRightAnchor(ruleListView, 0D);
        AnchorPane.setTopAnchor(ruleListView, 0D);
        AnchorPane.setBottomAnchor(ruleListView, 0D);
    }

    public void refreshRules()
    {
        ruleListView.setItems(parsed.getRules().stream().map(rule -> new RuleText(rule, visualiser.getState(), visualiser.getRuleMap()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    private static class RuleText extends TextFlow
    {
        public RuleText(Rule rule, GlobalState state, ExpandedRuleMap ruleMap)
        {
            this.addRuleText(rule, state, ruleMap);
            this.setTextAlignment(TextAlignment.CENTER);
        }

        private void addRuleText(Rule rule, GlobalState state, ExpandedRuleMap ruleMap)
        {
            Text ruleText = new Text(rule.toString());

            if (ruleMap.getRules().get(rule).equals(rule) && state.getRuleState(rule) ||
                    !ruleMap.getRules().get(rule).equals(rule) && state.getRuleState(ruleMap.getRules().get(rule)))
                ruleText.getStyleClass().add("rule-valid");
            else
                ruleText.getStyleClass().add("rule-invalid");

            this.getChildren().add(ruleText);
        }
    }
}
