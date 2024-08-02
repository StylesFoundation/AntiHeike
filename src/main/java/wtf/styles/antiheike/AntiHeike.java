package wtf.styles.antiheike;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.Unsafe;
import wtf.styles.antiheike.util.ModulesScanner;
import wtf.styles.antiheike.util.Stopwatch;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Mod("antiheike")
public class AntiHeike {
    public static AntiHeike INSTANCE;
    private final Logger logger = LogManager.getLogger();
    private final List<String> allowedClasses = new ArrayList<>(); // Classes in libraries and mods
    private final List<String> invokeStack = new CopyOnWriteArrayList<>();
    private final Stopwatch stopwatch = new Stopwatch();
    private final Stopwatch joinStopwatch = new Stopwatch();
    public Unsafe theUnsafe; // Unsafe Instance, for crash cheater's client

    public AntiHeike() {
        INSTANCE = this;
        try {
            logger.info("Obtaining Unsafe instance...");
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            theUnsafe = (Unsafe) unsafeField.get(null);
            logger.info("Unsafe instance obtained.");

            logger.info("Scanning mod classes...");
            File modsDirectory = new File(Minecraft.getInstance().gameDirectory, "mods");
            List<File> filesToScan = new ArrayList<>(List.of(Objects.requireNonNull(modsDirectory.listFiles())));
            for (String classPath : System.getProperty("java.class.path").split(";")) {
                filesToScan.add(new File(classPath));
            }
            filesToScan.forEach(file -> logger.info("Found classpath {}.", file.getAbsolutePath()));
            filesToScan.removeIf(file -> !file.getName().endsWith(".jar"));
            for (File file : filesToScan) {
                try (JarFile jarFile = new JarFile(file)) {
                    for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (entryName.endsWith("/") || entryName.endsWith(".class")) {
                            allowedClasses.add(entryName.replace(".class", "").replace("/", "."));
                        }
                    }
                } catch (IOException e) {
                    logger.catching(e);
                }
            }
            logger.info("Found {} classes.", allowedClasses.size());
            logger.info("Anti-Heike mod is loaded and ready for detection.");
        } catch (Throwable e) {
            logger.catching(e);
        }
    }

    // Called from Minecraft.getInstance() to trace cheating invocations
    public void traceInvoke() {
        if (stopwatch.hasTimeElapsed(5000)) {

            // todo: you can send this list to server
            for (String scanModule : ModulesScanner.scanModules()) {
                if(scanModule.contains("younkoo-client") || scanModule.contains("styles-lite-release") || scanModule.contains("nobody")){
                    logger.info("Detected: " + scanModule);
                    theUnsafe.putAddress(114514L, 1919810L);
                }
            }

            stopwatch.reset();
            invokeStack.forEach(className -> {
                if (!allowedClasses.contains(className)) {
                    logger.info("Detected: " + className);
                    theUnsafe.putAddress(114514L, 1919810L);
                }
            });
            invokeStack.clear();
        }

        if (joinStopwatch.hasTimeElapsed(1000)) {
            joinStopwatch.reset();
            try {
                StackTraceElement[] stackTrace = new RuntimeException("Stack Trace").getStackTrace();
                StackTraceElement stackTraceElement = stackTrace[3];
                String className = stackTraceElement.getClassName();
                // Avoid detecting forge runtime-deobfuscated joined classes.
                if (className.startsWith("net.minecraft") || className.startsWith("wtf.styles") || className.startsWith("com.mojang")) {
                    return;
                }
                invokeStack.add(className);
            } catch (Exception e) {
                logger.catching(e);
            }
        }
    }
}
