package com.forgestorm.client.util;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;

public class YamlUtil {
    public static void saveYamlToFile(final Object object, String filePath) throws IOException {
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        final Yaml yaml = new Yaml(options);

        final FileWriter writer = new FileWriter(filePath);
        yaml.dump(object, writer);
        writer.close();
    }
}
