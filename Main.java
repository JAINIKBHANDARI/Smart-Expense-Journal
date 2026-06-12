import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final Path SOURCE_ROOT = Paths.get("src", "main", "java");
    private static final Path OUTPUT_ROOT = Paths.get("out");
    private static final String APP_MAIN_CLASS = "com.smartexpensejournal.Main";

    public static void main(String[] args) throws Exception {
        compileProjectSources();
        runApplication(args);
    }

    private static void compileProjectSources() throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Please run with a JDK, not a JRE. Java compiler was not found.");
        }

        cleanOutputRoot();
        Files.createDirectories(OUTPUT_ROOT);
        List<File> sourceFiles = findJavaSourceFiles();
        if (sourceFiles.isEmpty()) {
            throw new IllegalStateException("No Java source files found in " + SOURCE_ROOT.toAbsolutePath());
        }

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(sourceFiles);
            List<String> options = new ArrayList<>();
            options.add("-d");
            options.add(OUTPUT_ROOT.toString());
            options.add("-sourcepath");
            options.add(SOURCE_ROOT.toString());

            boolean success = compiler.getTask(null, fileManager, null, options, null, compilationUnits).call();
            if (!success) {
                throw new IllegalStateException("Compilation failed. Fix the errors above and run again.");
            }
        }
    }

    private static void cleanOutputRoot() throws Exception {
        if (!Files.exists(OUTPUT_ROOT)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(OUTPUT_ROOT)) {
            List<Path> existingPaths = paths
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());
            for (Path path : existingPaths) {
                Files.delete(path);
            }
        }
    }

    private static List<File> findJavaSourceFiles() throws Exception {
        try (Stream<Path> paths = Files.walk(SOURCE_ROOT)) {
            return paths
                    .filter(path -> path.toString().endsWith(".java"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
    }

    private static void runApplication(String[] args) throws Exception {
        URL[] urls = {OUTPUT_ROOT.toUri().toURL()};
        URLClassLoader classLoader = new URLClassLoader(urls, ClassLoader.getPlatformClassLoader());
        Thread.currentThread().setContextClassLoader(classLoader);
        Class<?> appMain = Class.forName(APP_MAIN_CLASS, true, classLoader);
        Method main = appMain.getMethod("main", String[].class);
        main.invoke(null, (Object) args);
    }
}
