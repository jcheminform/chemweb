package cz.iocb.chemweb.server.sparql.config.common;

import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdBoolean;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdString;
import java.util.Arrays;
import cz.iocb.chemweb.server.sparql.config.SparqlDatabaseConfiguration;
import cz.iocb.chemweb.server.sparql.database.Function;
import cz.iocb.chemweb.server.sparql.mapping.classes.IntegerUserIriClass;
import cz.iocb.chemweb.server.sparql.mapping.classes.StringUserIriClass;
import cz.iocb.chemweb.server.sparql.mapping.extension.FunctionDefinition;



public class Common
{
    public static void addPrefixes(SparqlDatabaseConfiguration config)
    {
        config.addPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        config.addPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        config.addPrefix("owl", "http://www.w3.org/2002/07/owl#");
        config.addPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");

        config.addPrefix("template", "http://bioinfo.iocb.cz/0.9/template#");
        config.addPrefix("fulltext", "http://bioinfo.uochb.cas.cz/rdf/v1.0/fulltext#");
    }


    public static void addResourceClasses(SparqlDatabaseConfiguration config)
    {
        config.addIriClass(new StringUserIriClass("purl:uniprot", "http://purl.uniprot.org/uniprot/"));

        config.addIriClass(new IntegerUserIriClass("linkedchemistry:chembl", "integer",
                "http://linkedchemistry.info/chembl/chemblid/CHEMBL"));

        config.addIriClass(new StringUserIriClass("rdf:wwpdb", "http://rdf.wwpdb.org/pdb/", 4));
        config.addIriClass(new StringUserIriClass("rdf:ensembl", "http://rdf.ebi.ac.uk/resource/ensembl/"));
        config.addIriClass(new IntegerUserIriClass("rdf:drugbank", "integer",
                "http://wifo5-04.informatik.uni-mannheim.de/drugbank/resource/drugs/DB", 5));

        config.addIriClass(new IntegerUserIriClass("identifiers:pubmed", "integer", "http://identifiers.org/pubmed/"));
        config.addIriClass(new IntegerUserIriClass("identifiers:wikipathway", "integer",
                "http://identifiers.org/wikipathways/WP"));
        config.addIriClass(new StringUserIriClass("identifiers:pfam", "http://identifiers.org/pfam/", "PF[0-9]{5}"));
        config.addIriClass(new StringUserIriClass("identifiers:intact", "http://identifiers.org/intact/", "[A-Z0-9]*"));
        config.addIriClass(
                new StringUserIriClass("identifiers:obo.go", "http://identifiers.org/obo.go/", "GO:[0-9]{7}"));
        config.addIriClass(
                new StringUserIriClass("identifiers:pdb", "http://identifiers.org/pdb/", "[0-9][A-Z0-9]{3}"));
        config.addIriClass(
                new StringUserIriClass("identifiers:interpro", "http://identifiers.org/interpro/", "IPR[0-9]{6}"));
        config.addIriClass(new StringUserIriClass("identifiers:reactome", "http://identifiers.org/reactome/",
                "R-[A-Z]{3}-[1-9][0-9]*"));
        config.addIriClass(new StringUserIriClass("identifiers:ec-code", "http://identifiers.org/ec-code/",
                "((-|[1-9][0-9]*)\\.){3}(-|n?[1-9][0-9]*)"));
        config.addIriClass(
                new StringUserIriClass("identifiers:ensembl", "http://identifiers.org/ensembl/", "ENSG[0-9]{11}"));
        config.addIriClass(new StringUserIriClass("identifiers:lincs.smallmolecule",
                "http://identifiers.org/lincs.smallmolecule/", "LSM-[1-9][0-9]*"));
        config.addIriClass(new StringUserIriClass("identifiers:mesh", "http://identifiers.org/mesh/",
                "[A-Z][0-9]+(\\.[0-9]+|[A-Z][0-9]+)*"));

        config.addIriClass(new IntegerUserIriClass("reference:ncbi-taxonomy", "integer",
                "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id="));
        config.addIriClass(new IntegerUserIriClass("reference:pubchem-assay", "integer",
                "http://pubchem.ncbi.nlm.nih.gov/assay/assay.cgi?aid="));
        config.addIriClass(new StringUserIriClass("reference:life",
                "http://life.ccs.miami.edu/life/summary?mode=CellLine&source=LINCS&input=", "LCL-[0-9]{4}"));

        config.addIriClass(
                new StringUserIriClass("reference:pharmgkb-gene", "http://www.pharmgkb.org/gene/", "PA[1-9][0-9]*"));
        config.addIriClass(new StringUserIriClass("reference:timbal", "http://mordred.bioc.cam.ac.uk/timbal/",
                "[A-Za-z0-9%()-]+"));
        config.addIriClass(
                new StringUserIriClass("reference:cgd", "http://research.nhgri.nih.gov/CGD/view/?g=", "[A-Z0-9-]+"));
        config.addIriClass(new StringUserIriClass("reference:uniprot", "http://www.uniprot.org/uniprot/"));

        config.addIriClass(
                new StringUserIriClass("reference:zinc", "http://zinc15.docking.org/substances/", "ZINC[0-9]{12}"));
        config.addIriClass(new StringUserIriClass("reference:surechembl", "https://www.surechembl.org/chemical/",
                "SCHEMBL[0-9]+"));
        config.addIriClass(new StringUserIriClass("reference:molport", "https://www.molport.com/shop/molecule-link/",
                "MolPort(-[0-9]{3}){3}"));
        config.addIriClass(new StringUserIriClass("reference:emolecules",
                "https://www.emolecules.com/cgi-bin/more?vid=", "[1-9][0-9]*"));
        config.addIriClass(new StringUserIriClass("reference:mcule", "https://mcule.com/", "MCULE-[1-9][0-9]*"));
        config.addIriClass(new StringUserIriClass("reference:ibm_patent_structure",
                "http://www-935.ibm.com/services/us/gbs/bao/siip/nih/?sid=", "[A-F0-9]+"));
        config.addIriClass(new StringUserIriClass("reference:nikkaji",
                "http://jglobal.jst.go.jp/en/redirect?Nikkaji_No=", "[A-Z0-9.]+"));
        config.addIriClass(new StringUserIriClass("reference:actor", "http://actor.epa.gov/actor/chemical.xhtml?casrn=",
                "[1-9][0-9]*-[0-9]{2}-[0-9]"));
        config.addIriClass(new IntegerUserIriClass("reference:chebi", "integer",
                "http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI%3A"));
        config.addIriClass(new StringUserIriClass("reference:pdbe",
                "http://www.ebi.ac.uk/pdbe-srv/pdbechem/chemicalCompound/show/", "[A-Z0-9]{1,3}"));
        config.addIriClass(
                new StringUserIriClass("reference:nmrshiftdb2", "http://nmrshiftdb.org/molecule/", "[1-9][0-9]*"));
        config.addIriClass(
                new StringUserIriClass("reference:kegg", "http://www.genome.jp/dbget-bin/www_bget?", "C[0-9]{5}"));
        config.addIriClass(new StringUserIriClass("reference:drugbank", "http://www.drugbank.ca/drugs/", "DB[0-9]{5}"));
        config.addIriClass(new StringUserIriClass("reference:hmdb", "http://www.hmdb.ca/metabolites/", "HMDB[0-9]{7}"));
        config.addIriClass(new StringUserIriClass("reference:iuphar",
                "http://www.guidetopharmacology.org/GRAC/LigandDisplayForward?ligandId=", "[1-9][0-9]*"));
        config.addIriClass(
                new StringUserIriClass("reference:selleck", "http://www.selleckchem.com/products/", "[^/]*", ".html"));
        config.addIriClass(
                new StringUserIriClass("reference:pharmgkb-drug", "https://www.pharmgkb.org/drug/", "PA[1-9][0-9]*"));
        config.addIriClass(new StringUserIriClass("reference:expression_atlas",
                "http://www.ebi.ac.uk/gxa/query?conditionQuery=", ".+"));
        config.addIriClass(new StringUserIriClass("reference:recon", "https://vmh.uni.lu/#metabolite/", "[^/]+"));
        config.addIriClass(new StringUserIriClass("reference:wikipedia", "http://en.wikipedia.org/wiki/", ".+"));
        config.addIriClass(new StringUserIriClass("reference:fda_srs",
                "http://fdasis.nlm.nih.gov/srs/ProxyServlet?mergeData=true&objectHandle=DBMaint&APPLICATION_NAME=fdasrs&actionHandle=default&nextPage=jsp/srs/ResultScreen.jsp&TXTSUPERLISTID=",
                "[A-Z0-9]{10}"));
    }


    public static void addFunctions(SparqlDatabaseConfiguration config)
    {
        String fulltext = config.getPrefixes().get("fulltext");

        FunctionDefinition match = new FunctionDefinition(fulltext + "match", new Function("common", "fulltext_match"),
                xsdBoolean, Arrays.asList(FunctionDefinition.stringLiteral, xsdString), false, true);

        config.addFunction(match);
    }
}