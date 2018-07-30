package fr.expertsystem.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return leftPart.getFacts();
    }

    public List<MutableFact> getDependents()
    {
        return rightPart.getFacts();
    }

    @Override
    public String toString()
    {
        return leftPart.toString() + "=> " + rightPart.toString();
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
            private List<IRuleElement> elements;
            private Builder            parent;

            public LeftPartBuilder(Builder parent)
            {
                this.elements = new ArrayList<>();
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
                elements.add(new MutableFact(fact, negated));
                return this;
            }

            public LeftPartBuilder cond(Conditions condition)
            {
                elements.add(condition);
                return this;
            }

            public RightPartBuilder imply()
            {
                parent.leftPart = new RulePart(elements);

                return new RightPartBuilder(parent);
            }
        }

        public static class RightPartBuilder
        {
            private List<IRuleElement> elements;
            private Builder            parent;

            public RightPartBuilder(Builder parent)
            {
                this.elements = new ArrayList<>();

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
                elements.add(new MutableFact(fact, negated));
                return this;
            }

            public RightPartBuilder cond(Conditions condition)
            {
                elements.add(condition);
                return this;
            }

            public Rule create()
            {
                parent.rightPart = new RulePart(elements);
                return parent.create();
            }
        }
    }

    private static class RulePart
    {
        private List<IRuleElement> elements;

        private RulePart(List<IRuleElement> elements)
        {
            this.elements = elements;
        }

        public List<MutableFact> getFacts()
        {
            return elements.stream().filter(MutableFact.class::isInstance)
                    .map(MutableFact.class::cast).collect(Collectors.toList());
        }

        public List<Conditions> getConditions()
        {
            return elements.stream().filter(Conditions.class::isInstance)
                    .map(Conditions.class::cast).collect(Collectors.toList());
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();

            this.elements.forEach(element ->
            {
                builder.append(element.toString());
                builder.append(" ");
            });
            return builder.toString();
        }
    }
}
