package cz.iocb.chemweb.server.sparql.database;



public class Table
{
    private final String schema;
    private final String table;


    public Table(String schema, String table)
    {
        this.schema = schema;
        this.table = table;
    }


    public String getSchema()
    {
        return schema;
    }


    public String getName()
    {
        return table;
    }


    public String getCode()
    {
        return "\"" + schema.replaceAll("\"", "\"\"") + "\".\"" + table.replaceAll("\"", "\"\"") + "\"";
    }


    @Override
    public int hashCode()
    {
        return table.hashCode();
    }


    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;

        if(obj == null || getClass() != obj.getClass())
            return false;

        Table other = (Table) obj;

        return schema.equals(other.schema) && table.equals(other.table);
    }
}
