package com.cyq.learning;

import org.codehaus.commons.compiler.*;
import org.codehaus.commons.compiler.util.ResourceFinderClassLoader;
import org.codehaus.commons.compiler.util.resource.MapResourceCreator;
import org.codehaus.commons.compiler.util.resource.MapResourceFinder;
import org.codehaus.commons.compiler.util.resource.Resource;
import org.codehaus.commons.compiler.util.resource.StringResource;
import org.codehaus.janino.SimpleCompiler;


import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CompileTest {
    public static void main(String[] args) throws Exception {

        simpleFileCompile();

        combinedFileCompile();


    }

    private static void combinedFileCompile() throws Exception {

        ICompilerFactory compilerFactory = CompilerFactoryFactory.getDefaultCompilerFactory();

        ICompiler compiler = compilerFactory.newCompiler();

        // Store generated .class files in a Map:
        Map<String, byte[]> classes = new HashMap<String, byte[]>();

        compiler.setClassFileCreator(new MapResourceCreator(classes));

        // Now compile two units from strings:
        compiler.compile(new Resource[]{
                new StringResource(
                        "pkg1/A.java",
                        "package pkg1; public class A { public static int meth() { return pkg2.B.meth(); } }"
                ),
                new StringResource(
                        "pkg2/B.java",
                        "package pkg2; public class B { public static int meth() { return 77;            } }"
                ),
        });

        // Set up a class loader that uses the generated classes.
        ClassLoader cl = new ResourceFinderClassLoader(
                new MapResourceFinder(classes),    // resourceFinder
                ClassLoader.getSystemClassLoader() // parent
        );

        Object ret = cl.loadClass("pkg1.A").getDeclaredMethod("meth").invoke(null);
        System.out.println(ret);
    }

    private static void simpleFileCompile() throws Exception {
        String className = "Foo";
        String sourceFileName = System.getenv("PWD") + "/janino/src/main/resources/Test.java";

        // Compile the source file.
        ClassLoader cl = new SimpleCompiler(sourceFileName, new FileInputStream(sourceFileName)).getClassLoader();

        Class<?> c = cl.loadClass(className);

        // Invoke the "public static main(String[])" method.
        Method m = c.getMethod("main", String[].class);
        m.invoke(null, (Object) new String[]{"a", "b"});
    }
}
