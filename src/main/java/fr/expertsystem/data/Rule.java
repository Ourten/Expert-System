package fr.expertsystem.data;

import java.util.ArrayList;
import java.util.List;

public class Rule
{
    private RulePart leftPart;
    private RulePart rightPart;

    private Rule(RulePart leftPart, RulePart rightPart)
    {
        this.leftPart = leftPart;
        this.rightPart = rightPart;
    }

    public static Builder.LeftPartBuilder build()
    {
        return new Builder().begin();
    }

    public RulePart getLeftPart()
    {
        return leftPart;
    }

    public RulePart getRightPart()
    {
        return rightPart;
    }

    public List<MutableFact> getDependencies()
    {
        return leftPart.facts;
    }

    public List<MutableFact> getDependents()
    {
        return rightPart.facts;
    }

    @Override
    public String toString()
    {
        return leftPart.toString() + "= " + rightPart.toString();
    }

    public static class Builder
    {
        private RulePart leftPart;
        private RulePart rightPart;

        public LeftPartBuilder begin()
        {
            return new LeftPartBuilder(this);
        }

        public Rule create()
        {
            return new Rule(leftPart, rightPart);
        }

        public static class LeftPartBuilder
        {
            private List<MutableFact> facts;
            private List<Conditions>  conditions;
            private Builder           parent;

            public LeftPartBuilder(Builder parent)
            {
                this.facts = new ArrayList<>();
                this.conditions = new ArrayList<>();

                this.parent = parent;
            }

            public LeftPartBuilder fact(String fact)
            {
                this.fact(fact, false);
                return this;
            }

            public LeftPartBuilder factNeg(String fact)
            {
                this.fact(fact, true);
                return this;
            }

            public LeftPartBuilder fact(String fact, boolean negated)
            {
                facts.add(new MutableFact(fact, negated));
                return this;
            }

            public LeftPartBuilder cond(Conditions condition)
            {
                conditions.add(condition);
                return this;
            }

            public RightPartBuilder imply()
            {
                parent.leftPart = new RulePart(facts, conditions);

                return new RightPartBuilder(parent);
            }
        }

        public static class RightPartBuilder
        {
            private List<MutableFact> facts;
            private List<Conditions>  conditions;
            private Builder           parent;

            public RightPartBuilder(Builder parent)
            {
                this.facts = new ArrayList<>();
                this.conditions = new ArrayList<>();

                this.parent = parent;
            }

            public RightPartBuilder fact(String fact)
            {
                this.fact(fact, false);
                return this;
            }

            public RightPartBuilder factNeg(String fact)
            {
                this.fact(fact, true);
                return this;
            }

            public RightPartBuilder fact(String fact, boolean negated)
            {
                facts.add(new MutableFact(fact, negated));
                return this;
            }

            public RightPartBuilder cond(Conditions condition)
            {
                conditions.add(condition);
                return this;
            }

            public Rule create()
            {
                parent.rightPart = new RulePart(facts, conditions);

                return parent.create();
            }
        }
    }

    private static class RulePart
    {
        private List<MutableFact> facts;
        private List<Conditions>  conditions;

        private RulePart(List<MutableFact> facts, List<Conditions> conditions)
        {
            this.facts = facts;
            this.conditions = conditions;
        }

        public List<MutableFact> getFacts()
        {
            return facts;
        }

        public List<Conditions> getConditions()
        {
            return conditions;
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();

            for (MutableFact fact : facts)
            {
                builder.append(fact.toString());

                int idx = facts.indexOf(fact);
                if (idx < conditions.size())
                {
                    builder.append(" ");
                    builder.append(conditions.get(idx).toString());
                }

                builder.append(" ");
            }
            return builder.toString();
        }
    }
}
