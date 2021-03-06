package cz.iocb.chemweb.server.sparql.parser.model.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import cz.iocb.chemweb.server.sparql.parser.BaseElement;
import cz.iocb.chemweb.server.sparql.parser.ElementVisitor;
import cz.iocb.chemweb.server.sparql.parser.error.ErrorType;
import cz.iocb.chemweb.server.sparql.parser.error.UncheckedParseException;
import cz.iocb.chemweb.server.sparql.parser.model.Variable;
import cz.iocb.chemweb.server.sparql.parser.model.expression.Expression;



/**
 * Pattern that assigns constant values ({@link #getValuesLists}) to variables ( {@link #getVariables}).
 *
 * <p>
 * Corresponds to the rules [28] ValuesClause and [61] InlineData in the SPARQL grammar.
 */
public class Values extends BaseElement implements Pattern
{
    /**
     * Single assignment of constant values ({@link #getValues}) to the variables from {@link Values}.
     *
     * <p>
     * Note: {@code null} value in the list represents {@code UNDEF}.
     */
    public static class ValuesList extends BaseElement
    {
        private final List<Expression> values;

        public ValuesList(Collection<Expression> values)
        {
            this.values = new ArrayList<>(values);
        }

        public List<Expression> getValues()
        {
            return values;
        }

        @Override
        public <T> T accept(ElementVisitor<T> visitor)
        {
            return visitor.visit(this);
        }
    }

    private final List<Variable> variables;
    private final List<ValuesList> valuesLists;

    public Values()
    {
        variables = new ArrayList<>();
        valuesLists = new ArrayList<>();
    }

    public Values(Collection<Variable> variables, Collection<ValuesList> valuesLists)
    {
        this.variables = new ArrayList<>(variables);
        this.valuesLists = new ArrayList<>(valuesLists);
    }

    public List<Variable> getVariables()
    {
        return variables;
    }

    public List<ValuesList> getValuesLists()
    {
        return valuesLists;
    }

    /**
     * Checks whether counts of variables and values match. If not, throws an {@link UncheckedParseException}.
     */
    public void checkCounts()
    {
        if(valuesLists.stream().anyMatch(list -> list.getValues().size() != variables.size()))
        {
            throw new UncheckedParseException(ErrorType.wrongNumberOfValues, this.getRange());
        }
    }

    @Override
    public <T> T accept(ElementVisitor<T> visitor)
    {
        return visitor.visit(this);
    }
}
