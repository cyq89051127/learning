package com.cyq.learning;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IExpressionEvaluator;
import org.codehaus.janino.ExpressionEvaluator;

import java.lang.reflect.InvocationTargetException;

public class ExpressionEvaluatorTest {
    public static void main(String[] args) throws Exception {
        simpleExpressionTest();

        predictTest();

    }

    private static void simpleExpressionTest() throws CompileException, InvocationTargetException {
        ExpressionEvaluator eval = new ExpressionEvaluator();
        eval.cook("1+1");
        System.out.println(eval.evaluate(null));
    }

    /***
     * This method evaluates an express which is used to comapre two values.
     *It's like an function :
     * Boolean function(Double first, Integer second) {
     *     first >= second ? true else false;
     * }
     * @throws Exception
     */
    private static void predictTest() throws Exception {
        IExpressionEvaluator ee = CompilerFactoryFactory.getDefaultCompilerFactory().newExpressionEvaluator();
        // set the parameters and types of parameters
        ee.setParameters(new String[]{"first","second"}, new Class[]{double.class,Integer.class});
        // set the return  value type
        ee.setExpressionType(Boolean.class);
        ee.cook("first >= second ? true : false");
        Object s = ee.evaluate(new Object[]{32,102} );
        System.out.println(s);
    }
}
