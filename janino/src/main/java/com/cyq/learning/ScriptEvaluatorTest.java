package com.cyq.learning;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ScriptEvaluator;

import java.lang.reflect.InvocationTargetException;

public class ScriptEvaluatorTest {
    public static void main(String[] args) throws InvocationTargetException, CompileException {
        simpleScriptTest();
    }

    private static void simpleScriptTest() throws CompileException, InvocationTargetException {
        ScriptEvaluator se = new ScriptEvaluator();

        se.cook(""
                + "static void method1() {\n"
                + "System.out.println(1);\n"
                + "}\n"
                + "\n"
                + "method1();\n"
                + "method2();\n"
                + "\n"
                + "static void method2() {\n"
                + "System.out.println(2);\n"
                + "}\n"
        );

        se.evaluate(new Object[0]);
    }
}
