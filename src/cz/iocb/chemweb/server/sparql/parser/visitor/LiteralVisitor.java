package cz.iocb.chemweb.server.sparql.parser.visitor;

import static cz.iocb.chemweb.server.sparql.parser.BuiltinTypes.rdfLangStringIri;
import static cz.iocb.chemweb.server.sparql.parser.BuiltinTypes.xsdBooleanIri;
import static cz.iocb.chemweb.server.sparql.parser.BuiltinTypes.xsdDecimalIri;
import static cz.iocb.chemweb.server.sparql.parser.BuiltinTypes.xsdDoubleIri;
import static cz.iocb.chemweb.server.sparql.parser.BuiltinTypes.xsdIntegerIri;
import java.util.List;
import cz.iocb.chemweb.server.sparql.error.MessageType;
import cz.iocb.chemweb.server.sparql.error.TranslateMessage;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.BooleanLiteralContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.NumericLiteralContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.NumericLiteralNegativeContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.NumericLiteralPositiveContext;
import cz.iocb.chemweb.server.sparql.grammar.SparqlParser.RdfLiteralContext;
import cz.iocb.chemweb.server.sparql.parser.Range;
import cz.iocb.chemweb.server.sparql.parser.model.IRI;
import cz.iocb.chemweb.server.sparql.parser.model.Prologue;
import cz.iocb.chemweb.server.sparql.parser.model.expression.Literal;



public class LiteralVisitor extends BaseVisitor<Literal>
{
    private final Prologue prologue;
    private final List<TranslateMessage> messages;


    public LiteralVisitor(Prologue prologue, List<TranslateMessage> messages)
    {
        this.prologue = prologue;
        this.messages = messages;
    }


    @Override
    public Literal visitRdfLiteral(RdfLiteralContext ctx)
    {
        String value = unquote(ctx.string().getText());

        if(ctx.LANGTAG() != null)
        {
            String tag = ctx.LANGTAG().getText().substring(1);

            if(!tag.matches("([A-Za-z]{2,3}(-[A-Za-z]{3}){0,3}|[A-Za-z]{4,8})"
                    + "(-[A-Za-z]{4})?(-([A-Za-z]{2}|[0-9]{3}))?(-([A-Za-z0-9]{5,8}|[0-9][A-Za-z0-9]{3}))*"
                    + "(-[0-9A-WY-Za-wy-z](-[A-Za-z0-9]{2,8})+)*(-x(-[A-Za-z0-9]{1,8})+)?|x(-[A-Za-z0-9]{1,8})+"
                    + "|i-ami|i-bnn|i-default|i-enochian|i-hak|i-klingon|i-lux|i-mingo|i-navajo|i-pwn"
                    + "|i-tao|i-tay|i-tsu|sgn-BE-FR|sgn-BE-NL|sgn-CH-DE"))
                messages.add(new TranslateMessage(MessageType.invalidLanguageTag,
                        Range.compute(ctx.LANGTAG().getSymbol(), ctx.LANGTAG().getSymbol()), tag));

            return new Literal(value, tag);
        }
        else if(ctx.iri() != null)
        {
            IRI type = new IriVisitor(prologue, messages).visit(ctx.iri());

            if(type == null)
                return new Literal(value);

            Literal literal = new Literal(value, type);

            if(type.equals(rdfLangStringIri))
                messages.add(new TranslateMessage(MessageType.invalidDatatype, Range.compute(ctx.iri()),
                        type.toString(prologue)));
            else if(!literal.isTypeSupported())
                messages.add(new TranslateMessage(MessageType.unsupportedDatatype, Range.compute(ctx.iri()),
                        type.toString(prologue)));
            else if(literal.getValue() == null)
                messages.add(new TranslateMessage(MessageType.invalidLexicalForm, Range.compute(ctx.string()), value));

            return literal;
        }
        else
        {
            return new Literal(value);
        }
    }


    private static String unescape(String text)
    {
        // [160] ECHAR ::= '\' [tbnrf\"']
        return text.replace("\\t", "\t").replace("\\b", "\b").replace("\\n", "\n").replace("\\r", "\r")
                .replace("\\\"", "\"").replace("\\'", "'");
    }


    public static String unquote(String text)
    {
        if(text.startsWith("\"\"\"") && text.endsWith("\"\"\"") || text.startsWith("'''") && text.endsWith("'''"))
            text = text.substring(3, text.length() - 3);
        else if(text.startsWith("\"") && text.endsWith("\"") || text.startsWith("'") && text.endsWith("'"))
            text = text.substring(1, text.length() - 1);
        else
            throw new IllegalArgumentException();

        return unescape(text);
    }


    private static Literal createNumericLiteral(String text)
    {
        IRI type;

        if(text.contains("e") || text.contains("E"))
            type = xsdDoubleIri;
        else if(text.contains("."))
            type = xsdDecimalIri;
        else
            type = xsdIntegerIri;

        return new Literal(text, type);
    }


    @Override
    public Literal visitNumericLiteral(NumericLiteralContext ctx)
    {
        return createNumericLiteral(ctx.getText());
    }


    @Override
    public Literal visitNumericLiteralPositive(NumericLiteralPositiveContext ctx)
    {
        return createNumericLiteral(ctx.getText());
    }


    @Override
    public Literal visitNumericLiteralNegative(NumericLiteralNegativeContext ctx)
    {
        return createNumericLiteral(ctx.getText());
    }


    @Override
    public Literal visitBooleanLiteral(BooleanLiteralContext ctx)
    {
        return new Literal(ctx.getText(), xsdBooleanIri);
    }
}
