package org.bdj.external;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import org.bdj.Status;

public class JarExecutor {
    public static void execute(File jarFile) throws Exception {
        Status.println("Executing JAR: " + jarFile.getAbsolutePath());
        
        JarFile jar = new JarFile(jarFile);
        Manifest manifest = jar.getManifest();
        jar.close();
        
        if (manifest == null) {
            throw new Exception("No manifest found in JAR: " + jarFile.getName());
        }
        
        String mainClassName = manifest.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
        if (mainClassName == null) {
            throw new Exception("No Main-Class specified in manifest: " + jarFile.getName());
        }
        
        ClassLoader parentLoader = JarExecutor.class.getClassLoader();
        URL jarUrl = jarFile.toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, parentLoader);
        
        Class mainClass = classLoader.loadClass(mainClassName);
        Method mainMethod = mainClass.getMethod("main", new Class[]{String[].class});
        
        Status.println("Running " + mainClassName + "...");
        mainMethod.invoke(null, new Object[]{new String[0]});
        Status.println(mainClassName + " execution completed");
    }
}
