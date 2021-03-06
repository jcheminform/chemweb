package cz.iocb.chemweb.server.db;


public class BlankNode extends ReferenceNode
{
    public BlankNode(String value)
    {
        super(value);
    }


    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;

        if(obj == null || !(obj instanceof BlankNode))
            return false;

        return value.equals(((BlankNode) obj).value);
    }
}
