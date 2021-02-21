package com.forgestorm.client.util.yaml;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.util.Set;

import static com.forgestorm.client.util.Log.println;

public class MapRepresenter extends Representer {

    private static final boolean PRINT_DEBUG = false;

    /**
     * Prevent saving null values to file.
     */
    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        // if value of property is null, ignore it.
        if (propertyValue == null) {
            return null;
        } else {
            println(getClass(), "javaBean: " + javaBean, false, PRINT_DEBUG);
            println(getClass(), "Property: " + property, false, PRINT_DEBUG);
            println(getClass(), "propertyValue: " + propertyValue, false, PRINT_DEBUG);
            println(getClass(), "Tag: " + customTag + "\n", false, PRINT_DEBUG);
            return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
        }
    }

    /**
     * Prevent saving package and class name to file.
     */
    @Override
    protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
        if (!classTags.containsKey(javaBean.getClass())) {
            addClassTag(javaBean.getClass(), Tag.MAP);
        }
        return super.representJavaBean(properties, javaBean);
    }

}
