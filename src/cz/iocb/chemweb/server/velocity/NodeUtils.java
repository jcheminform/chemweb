package cz.iocb.chemweb.server.velocity;

import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringEscapeUtils;
import cz.iocb.chemweb.server.db.Literal;
import cz.iocb.chemweb.server.db.ReferenceNode;
import cz.iocb.chemweb.shared.utils.Encode;



public class NodeUtils
{
    private HashMap<String, String> prefixes;


    public NodeUtils(HashMap<String, String> prefixes)
    {
        this.prefixes = prefixes;
    }


    public String escapeHtml(Literal node)
    {
        if(node == null)
            return null;

        return StringEscapeUtils.escapeHtml4(node.getValue());
    }


    public String prefixedIRI(ReferenceNode node)
    {
        String result = node.getValue();

        for(Entry<String, String> prefix : prefixes.entrySet())
            if(result.startsWith(prefix.getValue()))
                return result.replaceFirst(prefix.getValue(), prefix.getKey() + ":");

        return result;
    }


    public String nodeId(ReferenceNode node)
    {
        return "NODE_" + Encode.base32m(node.getValue());
    }
}
