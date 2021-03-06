package cz.iocb.chemweb.server.sparql.parser.model.pattern;

import cz.iocb.chemweb.server.sparql.parser.Element;



/**
 * Marker interface used to represent any single pattern, like a triple, UNION or a group of patterns in braces.
 *
 * <p>
 * Corresponds to the rules [56] GraphPatternNotTriples and [81] TriplesSameSubjectPath in the SPARQL grammar.
 */
public interface Pattern extends Element
{
}
