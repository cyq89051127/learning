package com.cyq.learning;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IClassBodyEvaluator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassBodyEvaluatorTest {
    private static String body = "public static void fun(String[] args) {"
            + " System.out.println(java.util.Arrays.asList(args));"
            + "}";

    public static void main(String[] args) throws Exception {
        String[] arguments = new String[]{"first","second","third"};

//        System.arraycopy(args, 0, arguments, 0, arguments.length);

        IClassBodyEvaluator cbe = CompilerFactoryFactory.getDefaultCompilerFactory().newClassBodyEvaluator();
        cbe.cook(body);
        Class<?> c = cbe.getClazz();

        // Invoke the "public static main(String[])" method.
        Method m           = c.getMethod("fun", String[].class);
        Object returnValue = m.invoke(null, (Object) arguments);
    }
}
