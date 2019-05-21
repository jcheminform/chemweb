package cz.iocb.chemweb.server.sparql.translator.imcode.expression;

import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.intBlankNode;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.iri;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.rdfLangString;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.strBlankNode;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.unsupportedIri;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.unsupportedLiteral;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdBoolean;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdDate;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdDateTime;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdDecimal;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdDouble;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdFloat;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdInt;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdInteger;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdLong;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdShort;
import static cz.iocb.chemweb.server.sparql.mapping.classes.BuiltinClasses.xsdString;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import cz.iocb.chemweb.server.sparql.mapping.classes.BlankNodeClass;
import cz.iocb.chemweb.server.sparql.mapping.classes.DateConstantZoneClass;
import cz.iocb.chemweb.server.sparql.mapping.classes.DateTimeConstantZoneClass;
import cz.iocb.chemweb.server.sparql.mapping.classes.IntBlankNodeClass;
import cz.iocb.chemweb.server.sparql.mapping.classes.IriClass;
import cz.iocb.chemweb.server.sparql.mapping.classes.LangStringConstantTagClass;
import cz.iocb.chemweb.server.sparql.mapping.classes.LiteralClass;
import cz.iocb.chemweb.server.sparql.mapping.classes.ResourceClass;
import cz.iocb.chemweb.server.sparql.mapping.classes.StrBlankNodeClass;
import cz.iocb.chemweb.server.sparql.mapping.classes.UserIntBlankNodeClass;
import cz.iocb.chemweb.server.sparql.mapping.classes.UserIriClass;
import cz.iocb.chemweb.server.sparql.mapping.classes.UserStrBlankNodeClass;
import cz.iocb.chemweb.server.sparql.translator.expression.VariableAccessor;
import cz.iocb.chemweb.server.sparql.translator.imcode.SqlBaseClass;



public abstract class SqlExpressionIntercode extends SqlBaseClass
{
    private static final List<ResourceClass> numericOrder = Arrays.asList(xsdShort, xsdInt, xsdLong, xsdInteger,
            xsdDecimal, xsdFloat, xsdDouble);

    private final boolean canBeNull;
    private final boolean isDeterministic;
    private final boolean isBoxed;
    private final Set<ResourceClass> resourceClasses;
    protected final Set<String> variables = new HashSet<String>();


    protected SqlExpressionIntercode(Set<ResourceClass> resourceClasses, boolean canBeNull, boolean isDeterministic)
    {
        this.canBeNull = canBeNull;
        this.isDeterministic = isDeterministic;
        this.isBoxed = getExpressionResourceClass(resourceClasses) == null;
        this.resourceClasses = resourceClasses;
    }


    public abstract SqlExpressionIntercode optimize(VariableAccessor variableAccessor);


    public abstract String translate();


    public boolean canBeNull()
    {
        return canBeNull;
    }


    public boolean isBoxed()
    {
        return isBoxed;
    }


    public boolean isDeterministic()
    {
        return isDeterministic;
    }


    public Set<ResourceClass> getResourceClasses()
    {
        return resourceClasses;
    }


    public Set<String> getVariables()
    {
        return variables;
    }


    public ResourceClass getExpressionResourceClass()
    {
        return getExpressionResourceClass(resourceClasses);
    }


    protected String getResourceName()
    {
        if(isBoxed)
            return "rdfbox";

        ResourceClass resourceClass = getExpressionResourceClass();

        if(resourceClass instanceof DateTimeConstantZoneClass)
            return "plain_datetime";

        if(resourceClass instanceof DateConstantZoneClass)
            return "plain_date";

        if(resourceClass instanceof LangStringConstantTagClass)
            return "plain_lang_string";

        if(resourceClass instanceof IriClass)
            return "iri";

        return resourceClass.getName();
    }


    public static boolean isIri(ResourceClass resClass)
    {
        return resClass instanceof IriClass;
    }


    public static boolean isBlankNode(ResourceClass resClass)
    {
        return resClass instanceof BlankNodeClass;
    }


    public static boolean isIntBlankNode(ResourceClass resClass)
    {
        return resClass instanceof IntBlankNodeClass;
    }


    public static boolean isStrBlankNode(ResourceClass resClass)
    {
        return resClass instanceof StrBlankNodeClass;
    }


    public static boolean isLiteral(ResourceClass resClass)
    {
        return resClass instanceof LiteralClass;
    }


    public static boolean isDateTime(ResourceClass resClass)
    {
        return resClass == xsdDateTime || resClass instanceof DateTimeConstantZoneClass;
    }


    public static boolean isDate(ResourceClass resClass)
    {
        return resClass == xsdDate || resClass instanceof DateConstantZoneClass;
    }


    public static boolean isLangString(ResourceClass resClass)
    {
        return resClass == rdfLangString || resClass instanceof LangStringConstantTagClass;
    }


    public static boolean isStringLiteral(ResourceClass resClass)
    {
        return resClass == xsdString || resClass == rdfLangString || resClass instanceof LangStringConstantTagClass;
    }


    public static boolean isNumeric(ResourceClass resClass)
    {
        return numericOrder.contains(resClass);
    }


    public static boolean isNumericCompatibleWith(ResourceClass resClass, ResourceClass requestClass)
    {
        if(!isNumeric(resClass))
            return false;

        if(requestClass == null)
            return true;

        return numericOrder.indexOf(resClass) <= numericOrder.indexOf(requestClass);
    }


    public static boolean isEffectiveBooleanClass(ResourceClass resClass)
    {
        return isNumeric(resClass) || resClass == xsdBoolean || resClass == xsdString || resClass == unsupportedLiteral;
    }


    protected static Set<ResourceClass> asSet(ResourceClass... resourceClasses)
    {
        Set<ResourceClass> set = new HashSet<ResourceClass>();

        for(ResourceClass resourceClass : resourceClasses)
            set.add(resourceClass);

        return set;
    }


    protected static ResourceClass getExpressionResourceClass(Set<ResourceClass> resourceClasses)
    {
        if(resourceClasses.size() == 0)
            return null;

        if(resourceClasses.contains(rdfLangString) || resourceClasses.contains(unsupportedLiteral))
            return null;

        if(resourceClasses.size() == 1)
            return resourceClasses.iterator().next();

        if(resourceClasses.stream().allMatch(r -> isDateTime(r)))
            return xsdDateTime;

        if(resourceClasses.stream().allMatch(r -> isDate(r)))
            return xsdDate;

        if(resourceClasses.stream().allMatch(r -> isIri(r)))
            return iri;

        if(resourceClasses.stream().allMatch(r -> isIntBlankNode(r)))
            return intBlankNode;

        if(resourceClasses.stream().allMatch(r -> isStrBlankNode(r)))
            return strBlankNode;

        return null;
    }


    protected static Set<ResourceClass> joinResourceClasses(Set<ResourceClass> left, Set<ResourceClass> right)
    {
        Set<ResourceClass> set = new HashSet<ResourceClass>();
        set.addAll(left);
        set.addAll(right);

        if(set.contains(xsdDateTime))
            set = set.stream().filter(r -> !(r instanceof DateTimeConstantZoneClass)).collect(Collectors.toSet());

        if(set.contains(xsdDate))
            set = set.stream().filter(r -> !(r instanceof DateConstantZoneClass)).collect(Collectors.toSet());

        if(set.contains(rdfLangString))
            set = set.stream().filter(r -> !(r instanceof LangStringConstantTagClass)).collect(Collectors.toSet());

        if(set.contains(iri))
            set = set.stream().filter(r -> !(r instanceof IriClass && r != iri)).collect(Collectors.toSet());

        if(set.contains(intBlankNode))
            set = set.stream().filter(r -> !(r instanceof UserIntBlankNodeClass)).collect(Collectors.toSet());

        if(set.contains(strBlankNode))
            set = set.stream().filter(r -> !(r instanceof UserStrBlankNodeClass)).collect(Collectors.toSet());

        return set;
    }


    protected static Set<ResourceClass> intersectResourceClasses(Set<ResourceClass> left, Set<ResourceClass> right)
    {
        Set<ResourceClass> merged = new HashSet<ResourceClass>();
        merged.addAll(left);
        merged.addAll(right);

        Set<ResourceClass> set = new HashSet<ResourceClass>();

        for(ResourceClass resourceClass : merged)
        {
            if(left.contains(resourceClass) && right.contains(resourceClass))
                set.add(resourceClass);

            if(resourceClass instanceof DateTimeConstantZoneClass && merged.contains(xsdDateTime))
                set.add(resourceClass);

            if(resourceClass instanceof DateConstantZoneClass && merged.contains(xsdDate))
                set.add(resourceClass);

            if(resourceClass instanceof LangStringConstantTagClass && merged.contains(rdfLangString))
                set.add(resourceClass);

            if((resourceClass instanceof UserIriClass || resourceClass == unsupportedIri) && merged.contains(iri))
                set.add(resourceClass);

            if(resourceClass instanceof UserIntBlankNodeClass && merged.contains(intBlankNode))
                set.add(resourceClass);

            if(resourceClass instanceof UserStrBlankNodeClass && merged.contains(strBlankNode))
                set.add(resourceClass);
        }

        return set;
    }


    protected static String translateAsNullCheck(SqlExpressionIntercode operand, boolean not)
    {
        StringBuilder builder = new StringBuilder();

        if(operand instanceof SqlVariable)
        {
            SqlVariable variable = (SqlVariable) operand;
            String name = variable.getName();

            boolean hasOuterVariants = false;

            if(variable.getResourceClasses().size() > 1)
                builder.append("(");

            for(ResourceClass resourceClass : variable.getResourceClasses())
            {
                if(not)
                    appendOr(builder, hasOuterVariants);
                else
                    appendAnd(builder, hasOuterVariants);

                hasOuterVariants = true;
                boolean hasInnerVariants = false;

                if(resourceClass.getPatternPartsCount() > 1)
                    builder.append("(");

                for(int i = 0; i < resourceClass.getPatternPartsCount(); i++)
                {
                    String access = variable.getVariableAccessor().getSqlVariableAccess(name, resourceClass, i);

                    if(not)
                        appendAnd(builder, hasInnerVariants);
                    else
                        appendOr(builder, hasInnerVariants);

                    hasInnerVariants = true;
                    builder.append(access);
                    builder.append(not ? " IS NOT NULL" : " IS NULL");
                }

                if(resourceClass.getPatternPartsCount() > 1)
                    builder.append(")");
            }

            if(variable.getResourceClasses().size() > 1)
                builder.append(")");
        }
        else
        {
            builder.append(operand.translate());
            builder.append(not ? " IS NOT NULL" : " IS NULL");
        }

        return builder.toString();
    }


    protected static String translateAsUnboxedOperand(SqlExpressionIntercode operand, ResourceClass resourceClass)
    {
        StringBuilder builder = new StringBuilder();

        if(operand instanceof SqlVariable)
        {
            SqlVariable variable = (SqlVariable) operand;
            String name = variable.getName();

            List<ResourceClass> compatibleClasses = variable.getResourceClasses().stream()
                    .filter(r -> r == resourceClass
                            || isNumeric(r) && isNumeric(resourceClass) && isNumericCompatibleWith(r, resourceClass)
                            || isDateTime(r) && isDateTime(resourceClass) || isDate(r) && isDate(resourceClass)
                            || isIri(r) && isIri(resourceClass) || isIntBlankNode(r) && isIntBlankNode(resourceClass)
                            || isStrBlankNode(r) && isStrBlankNode(resourceClass))
                    .collect(Collectors.toList());


            boolean hasAlternative = false;

            if(compatibleClasses.size() > 1)
                builder.append("COALESCE(");

            for(ResourceClass compatibleClass : compatibleClasses)
            {
                String code = compatibleClass.getExpressionCode(name, variable.getVariableAccessor(), false);

                appendComma(builder, hasAlternative);
                hasAlternative = true;

                if(compatibleClass instanceof DateTimeConstantZoneClass)
                {
                    builder.append("sparql.zoneddatetime_create(");
                    builder.append(code);
                    builder.append(", ");
                    builder.append(((DateTimeConstantZoneClass) compatibleClass).getZone());
                    builder.append("::int4)");
                }
                else if(compatibleClass instanceof DateConstantZoneClass)
                {
                    builder.append("sparql.zoneddate_create(");
                    builder.append(code);
                    builder.append(", ");
                    builder.append(((DateConstantZoneClass) compatibleClass).getZone());
                    builder.append("::int4)");
                }
                else if(compatibleClass instanceof UserIntBlankNodeClass)
                {
                    builder.append("(");
                    builder.append(code);
                    builder.append(" & x'FFFFFFFF'::int8)::int4");
                }
                else if(compatibleClass instanceof UserStrBlankNodeClass)
                {
                    builder.append("substr(");
                    builder.append(code);
                    builder.append(", 9)");
                }
                else if(compatibleClass instanceof IriClass)
                {
                    builder.append(code);
                }
                else if(compatibleClass != resourceClass)
                {
                    builder.append("sparql.cast_as_");
                    builder.append(resourceClass.getName());
                    builder.append("_from_");
                    builder.append(compatibleClass.getName());
                    builder.append("(");
                    builder.append(code);
                    builder.append(")");
                }
                else
                {
                    builder.append(code);
                }
            }

            if(compatibleClasses.size() > 1)
                builder.append(")");
        }
        else
        {
            ResourceClass expressionClass = operand.getExpressionResourceClass();

            if(resourceClass == xsdDateTime && expressionClass instanceof DateTimeConstantZoneClass)
            {
                builder.append("sparql.zoneddatetime_create(");
                builder.append(operand.translate());
                builder.append(", ");
                builder.append(((DateTimeConstantZoneClass) expressionClass).getZone());
                builder.append("::int4)");
            }
            else if(resourceClass == xsdDate && expressionClass instanceof DateConstantZoneClass)
            {
                builder.append("sparql.zoneddate_create(");
                builder.append(operand.translate());
                builder.append(", ");
                builder.append(((DateConstantZoneClass) expressionClass).getZone());
                builder.append("::int4)");
            }
            else if(resourceClass == iri && expressionClass instanceof IriClass)
            {
                builder.append(operand.translate());
            }
            else if(resourceClass == intBlankNode && expressionClass instanceof UserIntBlankNodeClass)
            {
                builder.append("('");
                builder.append(((UserIntBlankNodeClass) expressionClass).getSegment());
                builder.append("'::int8 << 32 | ");
                builder.append(operand.translate());
                builder.append(")");
            }
            else if(resourceClass == strBlankNode && expressionClass instanceof UserStrBlankNodeClass)
            {
                builder.append("('");
                builder.append(((UserStrBlankNodeClass) expressionClass).getSegment());
                builder.append("'::varchar || ");
                builder.append(operand.translate());
                builder.append(")");
            }
            else if(operand.isBoxed())
            {
                if(isNumeric(resourceClass))
                    builder.append("sparql.rdfbox_extract_derivated_from_");
                else
                    builder.append("sparql.rdfbox_extract_");

                builder.append(resourceClass.getName());
                builder.append("(");
                builder.append(operand.translate());
                builder.append(")");
            }
            else if(!operand.getResourceClasses().contains(resourceClass))
            {
                builder.append("sparql.cast_as_");
                builder.append(resourceClass.getName());
                builder.append("_from_");
                builder.append(operand.getResourceName());
                builder.append("(");
                builder.append(operand.translate());
                builder.append(")");
            }
            else
            {
                builder.append(operand.translate());
            }
        }

        return builder.toString();
    }


    protected static String translateAsBoxedOperand(SqlExpressionIntercode operand, Set<ResourceClass> requestedClasses)
    {
        StringBuilder builder = new StringBuilder();

        if(operand instanceof SqlVariable)
        {
            SqlVariable variable = (SqlVariable) operand;
            String name = variable.getName();

            boolean hasAlternative = false;

            if(requestedClasses.size() > 1)
                builder.append("COALESCE(");

            for(ResourceClass patternClass : requestedClasses)
            {
                appendComma(builder, hasAlternative);
                hasAlternative = true;

                builder.append(patternClass.getExpressionCode(name, variable.getVariableAccessor(), true));
            }

            if(requestedClasses.size() > 1)
                builder.append(")");
        }
        else if(operand.isBoxed())
        {
            builder.append(operand.translate());
        }
        else
        {
            ResourceClass resourceClass = operand.getExpressionResourceClass();

            if(resourceClass instanceof DateTimeConstantZoneClass)
            {
                builder.append("sparql.cast_as_rdfbox_from_datetime(");
                builder.append(operand.translate());
                builder.append(", '");
                builder.append(((DateTimeConstantZoneClass) resourceClass).getZone());
                builder.append("'::int4)");
            }
            else if(resourceClass instanceof DateConstantZoneClass)
            {
                builder.append("sparql.cast_as_rdfbox_from_date(");
                builder.append(operand.translate());
                builder.append(", '");
                builder.append(((DateConstantZoneClass) resourceClass).getZone());
                builder.append("'::int4)");
            }
            else if(resourceClass instanceof LangStringConstantTagClass)
            {
                builder.append("sparql.cast_as_rdfbox_from_lang_string(");
                builder.append(operand.translate());
                builder.append(", '");
                builder.append(((LangStringConstantTagClass) resourceClass).getTag());
                builder.append("'::varchar)");
            }
            else
            {
                builder.append("sparql.cast_as_rdfbox");
                builder.append("_from_");
                builder.append(operand.getResourceName());
                builder.append("(");
                builder.append(operand.translate());
                builder.append(")");
            }
        }

        return builder.toString();
    }


    protected static String translateAsStringLiteral(SqlExpressionIntercode operand, ResourceClass resourceClass)
    {
        if(operand instanceof SqlVariable)
            return ((SqlVariable) operand).getExpressionValue(resourceClass, false);
        else if(!operand.isBoxed())
            return operand.translate();
        else if(resourceClass == xsdString)
            return "sparql.rdfbox_extract_string(" + operand.translate() + ")";
        else
            return "sparql.rdfbox_extract_lang_string_string(" + operand.translate() + ")";
    }
}
