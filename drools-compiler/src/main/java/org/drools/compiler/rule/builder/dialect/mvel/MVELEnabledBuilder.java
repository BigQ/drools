package org.drools.compiler.rule.builder.dialect.mvel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.rule.builder.EnabledBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.dialect.DialectUtil;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELEnabledExpression;
import org.drools.core.reteoo.RuleTerminalNode.SortDeclarations;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Rule.SafeEnabled;
import org.drools.core.spi.KnowledgeHelper;
import org.kie.internal.security.KiePolicyHelper;

public class MVELEnabledBuilder
    implements
    EnabledBuilder {

    public void build(RuleBuildContext context) {
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        try {
            // This builder is re-usable in other dialects, so specify by name            
            MVELDialect dialect = (MVELDialect) context.getDialect( "mvel" );

            Map<String, Class< ? >> otherVars = new HashMap<String, Class< ? >>();
            otherVars.put( "rule",
                           org.drools.core.rule.Rule.class );

            Map<String, Declaration> declrs = context.getDeclarationResolver().getDeclarations( context.getRule() );

            AnalysisResult analysis = dialect.analyzeExpression( context,
                                                                 context.getRuleDescr(),
                                                                 (String) context.getRuleDescr().getEnabled(),
                                                                 new BoundIdentifiers( context.getDeclarationResolver().getDeclarationClasses( declrs ),
                                                                                       context.getPackageBuilder().getGlobals() ),
                                                                 otherVars );

            final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();
            int i = usedIdentifiers.getDeclrClasses().keySet().size();
            Declaration[] previousDeclarations = new Declaration[i];
            i = 0;
            for ( String id :  usedIdentifiers.getDeclrClasses().keySet() ) {
                previousDeclarations[i++] = declrs.get( id );
            }
            Arrays.sort( previousDeclarations, SortDeclarations.instance  );            

            String exprStr = (String) context.getRuleDescr().getEnabled();
            exprStr = exprStr.substring( 1,
                                         exprStr.length() - 1 ) + " ";
            MVELCompilationUnit unit = dialect.getMVELCompilationUnit( exprStr,
                                                                       analysis,
                                                                       previousDeclarations,
                                                                       null,
                                                                       otherVars,
                                                                       context,
                                                                       "drools",
                                                                       KnowledgeHelper.class,
                                                                       false );

            MVELEnabledExpression expr = new MVELEnabledExpression( unit,
                                                                    dialect.getId() );
            context.getRule().setEnabled( KiePolicyHelper.isPolicyEnabled() ? new SafeEnabled(expr) : expr );

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.addCompileable( context.getRule(),
                                 expr );

            expr.compile( data );
        } catch ( final Exception e ) {
            DialectUtil.copyErrorLocation(e, context.getRuleDescr());
            context.addError( new DescrBuildError( context.getParentDescr(),
                                                          context.getRuleDescr(),
                                                          null,
                                                          "Unable to build expression for 'enabled' : " + e.getMessage() + " '" + context.getRuleDescr().getEnabled() + "'" ) );
        }
    }

}
