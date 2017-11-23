package cz.iocb.chemweb.server.sparql.pubchem;

import static cz.iocb.chemweb.server.sparql.mapping.classes.LiteralClass.xsdString;
import java.util.Arrays;
import cz.iocb.chemweb.server.sparql.mapping.NodeMapping;
import cz.iocb.chemweb.server.sparql.mapping.classes.IriClass;



class InchiKey
{
    static void addIriClasses(PubChemConfiguration config)
    {
        config.addIriClass(
                new IriClass("inchikey", Arrays.asList("integer"), "http://rdf.ncbi.nlm.nih.gov/pubchem/inchikey/.*"));
    }


    static void addQuadMapping(PubChemConfiguration config)
    {
        IriClass inchikey = config.getIriClass("inchikey");
        NodeMapping graph = config.createIriMapping("pubchem:inchikey");

        {
            String table = "inchikey_bases";
            NodeMapping subject = config.createIriMapping(inchikey, "id");

            config.addQuadMapping(table, graph, subject, config.createIriMapping("rdf:type"),
                    config.createIriMapping("sio:CHEMINF_000399"));
            config.addQuadMapping(table, graph, subject, config.createIriMapping("sio:has-value"),
                    config.createLiteralMapping(xsdString, "inchikey"));
        }

        {
            String table = "inchikey_compounds";
            NodeMapping subject = config.createIriMapping(inchikey, "inchikey");

            config.addQuadMapping(table, graph, subject, config.createIriMapping("sio:is-attribute-of"),
                    config.createIriMapping("compound", "compound"));
        }

        {
            String table = "inchikey_subjects";
            NodeMapping subject = config.createIriMapping(inchikey, "inchikey");

            config.addQuadMapping(table, graph, subject, config.createIriMapping("dcterms:subject"),
                    config.createIriMapping("mesh", "subject"));
        }
    }
}
