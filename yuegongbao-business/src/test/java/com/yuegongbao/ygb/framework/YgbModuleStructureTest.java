package com.yuegongbao.ygb.framework;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YgbModuleStructureTest
{
    private static final List<String> MODULES = List.of("foundation", "compliance", "regulation", "safety",
        "warning", "aqins", "credit", "newform", "occupation", "techdefense", "aireport", "cockpit", "report");

    @Test
    void businessModulesKeepOwnLayerDirectories() throws Exception
    {
        Path javaRoot = moduleRoot().resolve("src/main/java/com/yuegongbao/ygb");
        Path resourceRoot = moduleRoot().resolve("src/main/resources/mapper/ygb");

        for (String module : MODULES)
        {
            assertJavaLayer(javaRoot, module, "domain");
            assertJavaLayer(javaRoot, module, "controller");
            assertJavaLayer(javaRoot, module, "service");
            assertJavaLayer(javaRoot, module, "mapper");
            assertResourceLayer(resourceRoot, module);
        }
    }

    @Test
    void businessEntitiesAndMappersDoNotReturnToRootPackages() throws Exception
    {
        Path javaRoot = moduleRoot().resolve("src/main/java/com/yuegongbao/ygb");
        Path resourceRoot = moduleRoot().resolve("src/main/resources/mapper/ygb");

        assertNoJavaFiles(javaRoot.resolve("controller"));
        assertNoJavaFiles(javaRoot.resolve("service"));
        assertNoJavaFiles(javaRoot.resolve("mapper"));

        assertNoRootBusinessEntityFiles(javaRoot.resolve("domain"));
        assertNoRootMapperXmlFiles(resourceRoot);
    }

    private void assertJavaLayer(Path javaRoot, String module, String layer) throws IOException
    {
        Path layerDir = javaRoot.resolve(module).resolve(layer);
        assertTrue(Files.isDirectory(layerDir), () -> "Missing module layer directory: " + layerDir);
        assertTrue(hasJavaFile(layerDir), () -> "Module layer directory is empty: " + layerDir);
    }

    private void assertResourceLayer(Path resourceRoot, String module) throws IOException
    {
        Path layerDir = resourceRoot.resolve(module);
        assertTrue(Files.isDirectory(layerDir), () -> "Missing mapper resource directory: " + layerDir);
        assertTrue(hasXmlFile(layerDir), () -> "Mapper resource directory is empty: " + layerDir);
    }

    private void assertNoJavaFiles(Path directory) throws IOException
    {
        if (!Files.exists(directory))
        {
            return;
        }
        assertFalse(hasJavaFile(directory), () -> "Root package should not contain business java files: " + directory);
    }

    private void assertNoRootBusinessEntityFiles(Path domainRoot) throws IOException
    {
        if (!Files.exists(domainRoot))
        {
            return;
        }
        try (var stream = Files.list(domainRoot))
        {
            boolean hasRootEntity = stream
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .anyMatch(name -> name.endsWith(".java") && !"package-info.java".equals(name));
            assertFalse(hasRootEntity, () -> "Root domain package should only keep shared VO objects: " + domainRoot);
        }
    }

    private void assertNoRootMapperXmlFiles(Path resourceRoot) throws IOException
    {
        if (!Files.exists(resourceRoot))
        {
            return;
        }
        try (var stream = Files.list(resourceRoot))
        {
            boolean hasRootXml = stream
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .anyMatch(name -> name.endsWith("Mapper.xml"));
            assertFalse(hasRootXml, () -> "Root mapper resource directory should not contain module mapper XML: "
                + resourceRoot);
        }
    }

    private boolean hasJavaFile(Path directory) throws IOException
    {
        try (var stream = Files.walk(directory))
        {
            return stream.anyMatch(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".java"));
        }
    }

    private boolean hasXmlFile(Path directory) throws IOException
    {
        try (var stream = Files.walk(directory))
        {
            return stream.anyMatch(path -> Files.isRegularFile(path) && path.getFileName().toString().endsWith(".xml"));
        }
    }

    private Path moduleRoot() throws URISyntaxException
    {
        Path testClassesPath = Path.of(YgbModuleStructureTest.class.getResource("/").toURI());
        return testClassesPath.getParent().getParent();
    }
}
