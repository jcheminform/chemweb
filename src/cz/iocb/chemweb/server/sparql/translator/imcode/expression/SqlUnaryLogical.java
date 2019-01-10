package cz.iocb.chemweb.server.sparql.translator.imcode.expression;

import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdBoolean;
import java.util.Set;
import cz.iocb.chemweb.server.sparql.mapping.classes.ResourceClass;
import cz.iocb.chemweb.server.sparql.translator.expression.VariableAccessor;



public class SqlUnaryLogical extends SqlUnary
{
    protected SqlUnaryLogical(SqlExpressionIntercode operand, Set<ResourceClass> resourceClasses, boolean canBeNull)
    {
        super(operand, resourceClasses, canBeNull);
    }


    public static SqlExpressionIntercode create(SqlExpressionIntercode operand)
    {
        operand = SqlEffectiveBooleanValue.create(operand);

        if(operand == SqlNull.get())
            return SqlNull.get();

        return new SqlUnaryLogical(operand, asSet(xsdBoolean), operand.canBeNull());
    }


    @Override
    public SqlExpressionIntercode optimize(VariableAccessor variableAccessor)
    {
        SqlExpressionIntercode operand = getOperand().optimize(variableAccessor);
        return create(operand);
    }


    @Override
    public String translate()
    {
        return "(not " + getOperand().translate() + ")";
    }
}
