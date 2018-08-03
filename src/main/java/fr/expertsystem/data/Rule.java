package fr.expertsystem.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Rule
{
    private RulePart leftPart;
    private RulePart rightPart;

    public Rule(RulePart leftPart, RulePart rightPart)
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

    public List<Fact> getDependencies()
    {
        return leftPart.getFacts();
    }

    public List<Fact> getDependents()
    {
        return rightPart.getFacts();
    }

    @Override
    public String toString()
    {
        return leftPart.toString() + "=> " + rightPart.toString();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return Objects.equals(getLeftPart(), rule.getLeftPart()) &&
                Objects.equals(getRightPart(), rule.getRightPart());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getLeftPart(), getRightPart());
    }

    public Rule copy()
    {
        return new Rule(this.leftPart.copy(), this.rightPart.copy());
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

        public interface IPartBuilder
        {
            IPartBuilder fact(String fact);

            IPartBuilder cond(Conditions condition);

            IPartBuilder imply();

            Rule create();
        }

        public static class LeftPartBuilder implements IPartBuilder
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
                elements.add(new Fact(fact));
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

            @Override
            public Rule create()
            {
                throw new RuntimeException("Cannot create from left part!");
            }
        }

        public static class RightPartBuilder implements IPartBuilder
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
                elements.add(new Fact(fact));
                return this;
            }

            public RightPartBuilder cond(Conditions condition)
            {
                elements.add(condition);
                return this;
            }

            @Override
            public IPartBuilder imply()
            {
                throw new RuntimeException("Cannot imply in right part!");
            }

            public Rule create()
            {
                parent.rightPart = new RulePart(elements);
                return parent.create();
            }
        }
    }

    public static class RulePart
    {
        private List<IRuleElement> elements;

        public RulePart(List<IRuleElement> elements)
        {
            this.elements = elements;
        }

        public List<Fact> getFacts()
        {
            return elements.stream().filter(Fact.class::isInstance)
                    .map(Fact.class::cast).collect(Collectors.toList());
        }

        public List<Conditions> getConditions()
        {
            return elements.stream().filter(Conditions.class::isInstance)
                    .map(Conditions.class::cast).collect(Collectors.toList());
        }

        public List<IRuleElement> getElements()
        {
            return elements;
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();

            this.elements.forEach(element ->
            {
                builder.append(element.toString());

                if (element != Conditions.NOT)
                    builder.append(" ");
            });
            return builder.toString();
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RulePart rulePart = (RulePart) o;
            return Objects.equals(getElements(), rulePart.getElements());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getElements());
        }

        public RulePart copy()
        {
            return new RulePart(new ArrayList<>(this.elements));
        }
    }
}
