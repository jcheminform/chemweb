package cz.iocb.chemweb.server.sparql.mapping;

import java.util.ArrayList;
import java.util.List;
import cz.iocb.chemweb.server.db.DatabaseSchema.KeyPair;
import cz.iocb.chemweb.server.sparql.mapping.classes.IriClass;
import cz.iocb.chemweb.server.sparql.parser.model.IRI;
import cz.iocb.chemweb.server.sparql.parser.model.VariableOrBlankNode;
import cz.iocb.chemweb.server.sparql.parser.model.triple.Node;



public class ParametrisedIriMapping extends IriMapping implements ParametrisedMapping
{
    private final List<String> columns;


    public ParametrisedIriMapping(IriClass iriClass, List<String> columns)
    {
        super(iriClass);
        this.columns = columns;
    }


    @Override
    public boolean match(Node node)
    {
        if(node instanceof VariableOrBlankNode)
            return true;

        if(node instanceof IRI && getIriClass().match(((IRI) node).getUri().toString()))
            return true;

        return false;
    }


    @Override
    public String getSqlValueAccess(int i)
    {
        return columns.get(i);
    }


    @Override
    public NodeMapping remapColumns(List<KeyPair> columnMap)
    {
        ArrayList<String> remappedColumns = new ArrayList<String>();

        for(String col : columns)
        {
            String remapped = columnMap.stream().filter(s -> s.getParent().equals(col)).findAny().get().getForeign();
            assert remapped != null;
            remappedColumns.add(remapped);
        }

        return new ParametrisedIriMapping(getIriClass(), remappedColumns);
    }


    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;

        if(obj == null || !(obj instanceof ParametrisedIriMapping))
            return false;

        if(!super.equals(obj))
            return false;

        ParametrisedIriMapping other = (ParametrisedIriMapping) obj;

        return columns.equals(other.columns);
    }
}
