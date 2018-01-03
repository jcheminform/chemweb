package cz.iocb.chemweb.server.sparql.translator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import cz.iocb.chemweb.server.db.DatabaseSchema;
import cz.iocb.chemweb.server.db.postgresql.ConnectionPool;
import cz.iocb.chemweb.server.sparql.mapping.ConstantIriMapping;
import cz.iocb.chemweb.server.sparql.mapping.ConstantLiteralMapping;
import cz.iocb.chemweb.server.sparql.mapping.NodeMapping;
import cz.iocb.chemweb.server.sparql.mapping.ParametrisedIriMapping;
import cz.iocb.chemweb.server.sparql.mapping.ParametrisedLiteralMapping;
import cz.iocb.chemweb.server.sparql.mapping.QuadMapping;
import cz.iocb.chemweb.server.sparql.mapping.classes.IriClass;
import cz.iocb.chemweb.server.sparql.mapping.classes.LiteralClass;
import cz.iocb.chemweb.server.sparql.mapping.classes.ResourceClass;
import cz.iocb.chemweb.server.sparql.parser.model.expression.Literal;
import cz.iocb.chemweb.server.sparql.procedure.ProcedureDefinition;



public abstract class SparqlDatabaseConfiguration
{
    protected DatabaseSchema schema;
    protected ConnectionPool connectionPool;

    protected HashMap<String, String> prefixes = new HashMap<String, String>();
    protected LinkedHashMap<String, ResourceClass> classes = new LinkedHashMap<String, ResourceClass>();
    protected List<QuadMapping> mappings = new ArrayList<QuadMapping>();
    protected LinkedHashMap<String, ProcedureDefinition> procedures = new LinkedHashMap<String, ProcedureDefinition>();


    protected SparqlDatabaseConfiguration()
    {
        for(LiteralClass literal : LiteralClass.getClasses())
            classes.put(literal.getName(), literal);
    }


    public void addIriClass(IriClass iriClass)
    {
        classes.put(iriClass.getName(), iriClass);
    }


    public NodeMapping createIriMapping(IriClass iriClass, String... columns)
    {
        return new ParametrisedIriMapping(iriClass, Arrays.asList(columns));
    }


    public NodeMapping createIriMapping(String iriClassName, String... columns)
    {
        return new ParametrisedIriMapping(getIriClass(iriClassName), Arrays.asList(columns));
    }


    public ConstantIriMapping createIriMapping(String value)
    {
        String iri = null;

        if(value.startsWith("<"))
        {
            iri = value.substring(1, value.length() - 1);
        }
        else
        {
            String[] parts = value.split(":");
            iri = prefixes.get(parts[0]) + parts[1];
        }

        IriClass iriClass = null;

        for(ResourceClass c : classes.values())
        {
            if(c instanceof IriClass && ((IriClass) c).match(iri))
            {
                if(iriClass != null)
                    throw new RuntimeException("ambigous iri class for " + value);

                iriClass = (IriClass) c;
            }
        }

        assert iriClass != null;

        if(iriClass == null)
            throw new RuntimeException("no iri class for " + value);

        return new ConstantIriMapping(iriClass, iri);
    }


    public NodeMapping createLiteralMapping(LiteralClass literalClass, String column)
    {
        return new ParametrisedLiteralMapping(literalClass, column);
    }


    public NodeMapping createLiteralMapping(String value)
    {
        return new ConstantLiteralMapping(LiteralClass.xsdString, new Literal(value));
    }


    public void addQuadMapping(String table, NodeMapping graph, NodeMapping subject, NodeMapping predicate,
            NodeMapping object)
    {
        QuadMapping map = new QuadMapping(table, graph, subject, predicate, object);
        mappings.add(map);
    }


    public void addQuadMapping(String table, NodeMapping graph, NodeMapping subject, NodeMapping predicate,
            NodeMapping object, String condition)
    {
        QuadMapping map = new QuadMapping(table, graph, subject, predicate, object, condition);
        mappings.add(map);
    }


    public HashSet<String> getIriValues(String table)
    {
        HashSet<String> set = new HashSet<String>();

        try (Connection connection = connectionPool.getConnection())
        {
            try (PreparedStatement statement = connection.prepareStatement("select iri from " + table))
            {
                try (java.sql.ResultSet result = statement.executeQuery())
                {
                    while(result.next())
                        set.add(result.getString(1));
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return set;
    }


    public IriClass getIriClass(String name)
    {
        return (IriClass) classes.get(name);
    }


    public HashMap<String, String> getPrefixes()
    {
        return prefixes;
    }


    public LinkedHashMap<String, ResourceClass> getClasses()
    {
        return classes;
    }


    public List<QuadMapping> getMappings()
    {
        return mappings;
    }


    public LinkedHashMap<String, ProcedureDefinition> getProcedures()
    {
        return procedures;
    }


    public DatabaseSchema getSchema() throws SQLException
    {
        return schema;
    }


    public ConnectionPool getConnectionPool()
    {
        return connectionPool;
    }
}