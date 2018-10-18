package cz.iocb.chemweb.server.sparql.mapping.classes;

import static cz.iocb.chemweb.server.sparql.mapping.classes.ResultTag.DATETIME;
import static cz.iocb.chemweb.server.sparql.parser.BuiltinTypes.xsdDateTimeIri;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Hashtable;
import cz.iocb.chemweb.server.sparql.parser.model.expression.Literal;
import cz.iocb.chemweb.server.sparql.parser.model.triple.Node;
import cz.iocb.chemweb.server.sparql.translator.expression.VariableAccessor;



public class DateTimeConstantZoneClass extends LiteralClass
{
    private static final Hashtable<Integer, DateTimeConstantZoneClass> instances = new Hashtable<Integer, DateTimeConstantZoneClass>();

    private final int zone;


    DateTimeConstantZoneClass(int zone)
    {
        super("datetime$" + zone, Arrays.asList("timestamptz"), Arrays.asList(DATETIME), xsdDateTimeIri);
        this.zone = zone;
    }


    public static DateTimeConstantZoneClass get(int zone)
    {
        DateTimeConstantZoneClass instance = instances.get(zone);

        if(instance == null)
        {
            synchronized(instances)
            {
                instance = instances.get(zone);

                if(instance == null)
                {
                    instance = new DateTimeConstantZoneClass(zone);
                    instances.put(zone, instance);
                }
            }
        }

        return instance;
    }


    @Override
    public String getLiteralPatternCode(Literal literal, int part)
    {
        return "sparql.zoneddatetime_datetime('" + literal.getStringValue().trim() + "'::sparql.zoneddatetime)";
    }


    @Override
    public String getPatternCode(String column, int part, boolean isBoxed)
    {
        return (isBoxed ? "sparql.rdfbox_extract_datetime_datetime(" : "sparql.zoneddatetime_datetime(") + column + ")";
    }


    @Override
    public String getExpressionCode(Literal literal)
    {
        return "sparql.zoneddatetime_datetime('" + literal.getStringValue().trim() + "'::sparql.zoneddatetime)";
    }


    @Override
    public String getExpressionCode(String variable, VariableAccessor variableAccessor, boolean rdfbox)
    {
        String code = variableAccessor.getSqlVariableAccess(variable, this, 0);

        if(rdfbox)
            code = "sparql.cast_as_rdfbox_from_datetime(" + code + ", '" + zone + "'::int4)";

        return code;
    }


    @Override
    public String getResultCode(String variable, int part)
    {
        return "sparql.zoneddatetime_create(" + getSqlColumn(variable, 0) + ", '" + zone + "'::int4)";
    }


    @Override
    public boolean match(Node node)
    {
        if(!super.match(node))
            return false;

        if(!(node instanceof Literal))
            return true;

        int zone = DateTimeConstantZoneClass.getZone((Literal) node);

        if(zone != this.zone)
            return false;

        return true;
    }


    public int getZone()
    {
        return zone;
    }


    public static int getZone(Literal literal)
    {
        if(literal.getValue() instanceof OffsetDateTime)
            return ((OffsetDateTime) literal.getValue()).getOffset().getTotalSeconds();

        return Integer.MIN_VALUE;
    }
}