package org.bdj;

import java.io.*;
import java.net.*;
import java.lang.*;
import java.lang.reflect.*;

import java.awt.BorderLayout;
import javax.tv.xlet.Xlet;
import javax.tv.xlet.XletContext;
import org.havi.ui.HScene;
import org.havi.ui.HSceneFactory;

import org.bdj.sandbox.Exploit;
import org.bdj.sandbox.ExploitInternal;

public class InitXlet implements Xlet {
    private HScene scene;
    private Screen screen;
    private InternalJarLoader internalJarLoader;
    private Thread internalJarLoaderThread;
    private final String jarLoaderThreadName = "JarLoader";
    
    public void initXlet(XletContext context) {
        Status.setScreenOutputEnabled(true);
        Status.setNetworkLoggerEnabled(false);

        screen = Screen.getInstance();
        screen.setSize(1920, 1080);
        screen.setTitle("PS5 BD-JB v1.4.3-b2 Autoloader");
        Status.setProgress(0, "Initializing...");

        scene = HSceneFactory.getInstance().getDefaultHScene();
        scene.add(screen, BorderLayout.CENTER);
        scene.validate();
    }
    
    public void startXlet() {
    screen.setVisible(true);
    scene.setVisible(true);

    Status.setProgress(0, "Preparing display...");
    Status.info("Waiting for video output...");

    try {
        Thread.sleep(2000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }

    Status.success("Screen initialized");

    try {
        Status.info("Beginning sandbox escape sequence...");
        Status.setProgress(10, "Executing sandbox escape...");

        if (!Exploit.disableSecurityManager()) {
            ExploitInternal.disableSecurityManager();
        }

        if (System.getSecurityManager() == null) {
            Status.success("Sandbox escape completed");
        } else {
            Status.error("Sandbox escape failed");
        }

        // Warm up network stack to prevent dlopen failures in dynamic JARs
        try {
            Status.info("Warming up network stack...");
            InetAddress.getByName("127.0.0.1");
        } catch (Throwable ignored) {
        }

    } catch (Exception e) {
        Status.printStackTrace("Sandbox escape error: ", e);
    }

    // Add sanity check
    if (System.getSecurityManager() == null) {
        try {
            Status.info("Preparing autoloader components...");
            Status.setProgress(20, "Initializing JAR loader...");

            internalJarLoader = new InternalJarLoader();
            internalJarLoaderThread =
                new Thread(internalJarLoader, jarLoaderThreadName);
            internalJarLoaderThread.start();

        } catch (Throwable e) {
            Status.printStackTrace("Loader startup failed", e);
        }
    } else {
        Status.error("Cannot start loader - sandbox is still active");
    }
}

    public void pauseXlet() {
        screen.setVisible(false);
    }

    public void destroyXlet(boolean unconditional) {
        scene.remove(screen);
        scene = null;
    }
}




