package cz.iocb.chemweb.server.sparql.config.pubchem;

import static cz.iocb.chemweb.server.sparql.config.pubchem.PubChemConfiguration.rdfLangStringEn;
import java.util.Arrays;
import cz.iocb.chemweb.server.sparql.mapping.NodeMapping;
import cz.iocb.chemweb.server.sparql.mapping.classes.UserIriClass;



class Measuregroup
{
    static void addIriClasses(PubChemConfiguration config)
    {
        config.addIriClass(new UserIriClass("measuregroup", Arrays.asList("integer", "integer"),
                "http://rdf.ncbi.nlm.nih.gov/pubchem/measuregroup/AID[0-9]+(_(PMID[0-9]*|[0-9]+))?"));
    }


    static void addQuadMapping(PubChemConfiguration config)
    {
        UserIriClass measuregroup = config.getIriClass("measuregroup");
        NodeMapping graph = config.createIriMapping("pubchem:measuregroup");

        {
            String table = "measuregroup_bases";
            NodeMapping subject = config.createIriMapping(measuregroup, "bioassay", "measuregroup");

            config.addQuadMapping(table, graph, subject, config.createIriMapping("rdf:type"),
                    config.createIriMapping("bao:BAO_0000040"));
            config.addQuadMapping(table, graph, subject, config.createIriMapping("dcterms:title"),
                    config.createLiteralMapping(rdfLangStringEn, "title"));
            config.addQuadMapping(table, graph, subject, config.createIriMapping("dcterms:source"),
                    config.createIriMapping("source", "source"), "source is not null");
        }

        {
            String table = "endpoint_bases";
            NodeMapping subject = config.createIriMapping(measuregroup, "bioassay", "measuregroup");

            config.addQuadMapping(table, graph, subject, config.createIriMapping("obo:OBI_0000299"),
                    config.createIriMapping("endpoint", "substance", "bioassay", "measuregroup"));
        }

        {
            String table = "measuregroup_genes";
            NodeMapping subject = config.createIriMapping(measuregroup, "bioassay", "measuregroup");

            config.addQuadMapping(table, graph, subject, config.createIriMapping("obo:BFO_0000057"),
                    config.createIriMapping("gene", "gene"));
        }

        {
            String table = "measuregroup_proteins";
            NodeMapping subject = config.createIriMapping(measuregroup, "bioassay", "measuregroup");

            config.addQuadMapping(table, graph, subject, config.createIriMapping("obo:BFO_0000057"),
                    config.createIriMapping("protein", "protein"));
        }
    }
}