package com.forgestorm.client.util.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;

public class YamlUtil {
    public static void saveYamlToFile(final Object object, String filePath) {
        final DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setCanonical(false);
        options.setPrettyFlow(true);

        final Yaml yaml = new Yaml(new MapRepresenter(), options);

        final FileWriter writer;
        try {
            writer = new FileWriter(filePath);
            yaml.dump(object, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
