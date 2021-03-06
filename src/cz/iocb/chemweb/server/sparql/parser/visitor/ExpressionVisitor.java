package cz.iocb.chemweb.server.sparql.parser.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.AdditiveExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.AggregateContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.ArgListContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.BooleanLiteralContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.BuiltInCallContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.ConditionalAndExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.ConditionalOrExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.ConstraintContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.ExistsFunctionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.ExpressionListContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.FunctionCallContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.IriRefOrFunctionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.MultiplicativeExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.NotExistsFunctionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.NumericLiteralContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.NumericLiteralNegativeContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.NumericLiteralPositiveContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.PrimaryExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.RdfLiteralContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.RegexExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.RelationalExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.RelationalSetExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.StrReplaceExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.SubStringExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.UnaryAdditiveExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.UnaryExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.UnaryLiteralExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.UnaryNegationExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.UnarySignedLiteralExpressionContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.VarContext;
import cz.iocb.chemweb.server.sparql.parser.Position;
import cz.iocb.chemweb.server.sparql.parser.Range;
import cz.iocb.chemweb.server.sparql.parser.model.IRI;
import cz.iocb.chemweb.server.sparql.parser.model.Variable;
import cz.iocb.chemweb.server.sparql.parser.model.expression.BinaryExpression;
import cz.iocb.chemweb.server.sparql.parser.model.expression.BracketedExpression;
import cz.iocb.chemweb.server.sparql.parser.model.expression.BuiltInCallExpression;
import cz.iocb.chemweb.server.sparql.parser.model.expression.ExistsExpression;
import cz.iocb.chemweb.server.sparql.parser.model.expression.Expression;
import cz.iocb.chemweb.server.sparql.parser.model.expression.FunctionCallExpression;
import cz.iocb.chemweb.server.sparql.parser.model.expression.InExpression;
import cz.iocb.chemweb.server.sparql.parser.model.expression.Literal;
import cz.iocb.chemweb.server.sparql.parser.model.expression.UnaryExpression;



public class ExpressionVisitor extends BaseVisitor<Expression>
{
    @Override
    public Expression visitConstraint(ConstraintContext ctx)
    {
        if(ctx.expression() != null)
        {
            return new BracketedExpression(visit(ctx.expression()));
        }

        return super.visitConstraint(ctx);
    }

    @Override
    public Expression visitPrimaryExpression(PrimaryExpressionContext ctx)
    {
        if(ctx.expression() != null)
        {
            return new BracketedExpression(visit(ctx.expression()));
        }

        return super.visitPrimaryExpression(ctx);
    }

    @Override
    public UnaryExpression visitUnaryAdditiveExpression(UnaryAdditiveExpressionContext ctx)
    {
        UnaryExpression.Operator operator = ctx.op.getText().equals("+") ? UnaryExpression.Operator.Plus
                : UnaryExpression.Operator.Minus;

        return new UnaryExpression(operator, visit(ctx.expression()));
    }

    @Override
    public UnaryExpression visitUnaryNegationExpression(UnaryNegationExpressionContext ctx)
    {
        return new UnaryExpression(UnaryExpression.Operator.Not, visit(ctx.expression()));
    }

    @Override
    public BinaryExpression visitMultiplicativeExpression(MultiplicativeExpressionContext ctx)
    {
        BinaryExpression.Operator operator = ctx.op.getText().equals("*") ? BinaryExpression.Operator.Multiply
                : BinaryExpression.Operator.Divide;

        return new BinaryExpression(operator, visit(ctx.expression(0)), visit(ctx.expression(1)));
    }

    @Override
    public Expression visitAdditiveExpression(AdditiveExpressionContext ctx)
    {
        BinaryExpression.Operator operator = ctx.op.getText().equals("+") ? BinaryExpression.Operator.Add
                : BinaryExpression.Operator.Subtract;

        return new BinaryExpression(operator, visit(ctx.expression(0)), visit(ctx.expression(1)));
    }

    @Override
    public BinaryExpression visitUnarySignedLiteralExpression(UnarySignedLiteralExpressionContext ctx)
    {
        Expression left = visit(ctx.expression());
        Expression right = visit(ctx.unaryLiteralExpression());

        Literal rightLiteral;

        // we have to extract the sign from the numeric literal from right
        if(right instanceof Literal)
        {
            rightLiteral = (Literal) right;
        }
        else
        {
            rightLiteral = (Literal) ((BinaryExpression) right).getLeft();
        }

        BinaryExpression.Operator operator = rightLiteral.getStringValue().startsWith("+")
                ? BinaryExpression.Operator.Add : BinaryExpression.Operator.Subtract;

        rightLiteral.setStringValue(rightLiteral.getStringValue().substring(1));
        rightLiteral.setRange(
                new Range(moveByOneCharacter(rightLiteral.getRange().getStart()), rightLiteral.getRange().getEnd()));

        return new BinaryExpression(operator, left, right);
    }

    private static Position moveByOneCharacter(Position original)
    {
        return new Position(original.getLineNumber(), original.getPositionInLine() + 1);
    }

    @Override
    public Expression visitUnaryLiteralExpression(UnaryLiteralExpressionContext ctx)
    {
        ParserRuleContext leftCtx = ctx.numericLiteralPositive() != null ? ctx.numericLiteralPositive()
                : ctx.numericLiteralNegative();
        Expression left = visit(leftCtx);

        if(ctx.op == null)
            return left;

        BinaryExpression.Operator operator = ctx.op.getText().equals("*") ? BinaryExpression.Operator.Multiply
                : BinaryExpression.Operator.Divide;

        Expression right = visit(ctx.unaryExpression());

        return new BinaryExpression(operator, left, right);
    }


    @Override
    public UnaryExpression visitUnaryExpression(UnaryExpressionContext ctx)
    {
        UnaryExpression.Operator operator;
        switch(ctx.op.getText())
        {
            case "!":
                operator = UnaryExpression.Operator.Not;
                break;
            case "+":
                operator = UnaryExpression.Operator.Plus;
                break;
            case "-":
                operator = UnaryExpression.Operator.Minus;
                break;
            default:
                throw new AssertionError();
        }

        return new UnaryExpression(operator, visit(ctx.primaryExpression()));
    }

    private List<Expression> parseExpressionList(ExpressionListContext ctx)
    {
        if(ctx == null)
            return new ArrayList<>();

        return ctx.expression().stream().map(this::visit).collect(Collectors.toList());
    }

    @Override
    public InExpression visitRelationalSetExpression(RelationalSetExpressionContext ctx)
    {
        Expression left = visit(ctx.expression());
        List<Expression> right = parseExpressionList(ctx.expressionList());
        boolean negated = ctx.NOT() != null;

        return new InExpression(left, right, negated);
    }

    @Override
    public BinaryExpression visitRelationalExpression(RelationalExpressionContext ctx)
    {
        BinaryExpression.Operator operator;
        switch(ctx.op.getText())
        {
            case "=":
                operator = BinaryExpression.Operator.Equals;
                break;
            case "!=":
                operator = BinaryExpression.Operator.NotEquals;
                break;
            case "<":
                operator = BinaryExpression.Operator.LessThan;
                break;
            case ">":
                operator = BinaryExpression.Operator.GreaterThan;
                break;
            case "<=":
                operator = BinaryExpression.Operator.LessThanOrEqual;
                break;
            case ">=":
                operator = BinaryExpression.Operator.GreaterThanOrEqual;
                break;
            default:
                throw new AssertionError();
        }

        return new BinaryExpression(operator, visit(ctx.expression(0)), visit(ctx.expression(1)));
    }

    @Override
    public BinaryExpression visitConditionalAndExpression(ConditionalAndExpressionContext ctx)
    {
        return new BinaryExpression(BinaryExpression.Operator.And, visit(ctx.expression(0)), visit(ctx.expression(1)));
    }

    @Override
    public BinaryExpression visitConditionalOrExpression(ConditionalOrExpressionContext ctx)
    {
        return new BinaryExpression(BinaryExpression.Operator.Or, visit(ctx.expression(0)), visit(ctx.expression(1)));
    }

    @Override
    public Expression visitBuiltInCall(BuiltInCallContext ctx)
    {
        // for alternatives that are handled in a special way ((NOT) EXISTS)
        Expression superResult = super.visitBuiltInCall(ctx);
        if(superResult != null)
            return superResult;

        ArgumentsVisitor argumentsVisitor = new ArgumentsVisitor();

        ParseTree functionNameNode = ctx.children.get(0);
        String functionName = functionNameNode.getChildCount() == 0 ? functionNameNode.getText()
                : functionNameNode.getChild(0).getText();

        List<Expression> arguments = argumentsVisitor.visitBuiltInCall(ctx);

        BuiltInCallExpression result = new BuiltInCallExpression(functionName, arguments);
        if(argumentsVisitor.foundDistinct())
            result.setDistinct(true);

        return result;
    }

    @Override
    public ExistsExpression visitExistsFunction(ExistsFunctionContext ctx)
    {
        return new ExistsExpression(new GraphPatternVisitor().visit(ctx.groupGraphPattern()), false);
    }

    @Override
    public ExistsExpression visitNotExistsFunction(NotExistsFunctionContext ctx)
    {
        return new ExistsExpression(new GraphPatternVisitor().visit(ctx.groupGraphPattern()), true);
    }

    private static FunctionCallExpression parseFunctionCall(IRI iri, ArgListContext ctx)
    {
        ArgumentsVisitor argumentsVisitor = new ArgumentsVisitor();

        List<Expression> arguments = argumentsVisitor.visit(ctx);

        FunctionCallExpression result = new FunctionCallExpression(iri, arguments);
        if(argumentsVisitor.foundDistinct())
            result.setDistinct(true);
        return result;
    }

    @Override
    public Expression visitIriRefOrFunction(IriRefOrFunctionContext ctx)
    {
        IRI iri = new IriVisitor().visit(ctx.iri());

        if(ctx.argList() == null)
            return iri;

        return parseFunctionCall(iri, ctx.argList());
    }

    @Override
    public FunctionCallExpression visitFunctionCall(FunctionCallContext ctx)
    {
        IRI iri = new IriVisitor().visit(ctx.iri());

        return parseFunctionCall(iri, ctx.argList());
    }

    @Override
    public Variable visitVar(VarContext ctx)
    {
        return new Variable(ctx.getText());
    }

    @Override
    public Literal visitRdfLiteral(RdfLiteralContext ctx)
    {
        return new LiteralVisitor().visitRdfLiteral(ctx);
    }

    @Override
    public Literal visitNumericLiteral(NumericLiteralContext ctx)
    {
        return new LiteralVisitor().visitNumericLiteral(ctx);
    }

    @Override
    public Literal visitNumericLiteralPositive(NumericLiteralPositiveContext ctx)
    {
        return new LiteralVisitor().visitNumericLiteralPositive(ctx);
    }

    @Override
    public Literal visitNumericLiteralNegative(NumericLiteralNegativeContext ctx)
    {
        return new LiteralVisitor().visitNumericLiteralNegative(ctx);
    }

    @Override
    public Literal visitBooleanLiteral(BooleanLiteralContext ctx)
    {
        return new LiteralVisitor().visitBooleanLiteral(ctx);
    }
}

class ArgumentsVisitor extends BaseVisitor<List<Expression>>
{
    private boolean foundDistinct = false;

    public boolean foundDistinct()
    {
        return foundDistinct;
    }

    private static List<Expression> visitExpressions(List<? extends ParserRuleContext> contexts)
    {
        return contexts.stream().map(new ExpressionVisitor()::visit).collect(Collectors.toList());
    }

    @Override
    public List<Expression> visitBuiltInCall(BuiltInCallContext ctx)
    {
        // for alternatives that have their own rules
        List<Expression> superResult = super.visitBuiltInCall(ctx);
        if(superResult != null)
            return superResult;

        if(!ctx.expression().isEmpty())
        {
            return visitExpressions(ctx.expression());
        }

        if(ctx.expressionList() != null)
        {
            return visit(ctx.expressionList());
        }

        if(ctx.var() != null)
        {
            List<Expression> result = new ArrayList<>();
            result.add(new ExpressionVisitor().visit(ctx.var()));
            return result;
        }

        return new ArrayList<>();
    }

    @Override
    public List<Expression> visitExpressionList(ExpressionListContext ctx)
    {
        return visitExpressions(ctx.expression());
    }

    @Override
    public List<Expression> visitAggregate(AggregateContext ctx)
    {
        ExpressionVisitor expressionVisitor = new ExpressionVisitor();

        if(ctx.DISTINCT() != null)
            foundDistinct = true;

        if(ctx.ASTERISK() != null)
            return new ArrayList<>();

        List<Expression> result = new ArrayList<>();
        result.add(expressionVisitor.visit(ctx.expression()));

        if(ctx.string() != null)
            result.add(withRange(new Literal(LiteralVisitor.unquote(ctx.string().getText())), ctx.string()));

        return result;
    }

    @Override
    public List<Expression> visitSubStringExpression(SubStringExpressionContext ctx)
    {
        return visitExpressions(ctx.expression());
    }

    @Override
    public List<Expression> visitStrReplaceExpression(StrReplaceExpressionContext ctx)
    {
        return visitExpressions(ctx.expression());
    }

    @Override
    public List<Expression> visitRegexExpression(RegexExpressionContext ctx)
    {
        return visitExpressions(ctx.expression());
    }

    @Override
    public List<Expression> visitArgList(ArgListContext ctx)
    {
        if(ctx.DISTINCT() != null)
            foundDistinct = true;

        if(ctx.expressionList() == null)
            return new ArrayList<>();

        return visit(ctx.expressionList());
    }
}
