package pl.edu.uj.dusinski.dao;

import java.util.List;
import java.util.Map;

public class YahooCurrency {

    private final CurrencyDetails list;

    public YahooCurrency(CurrencyDetails list) {
        this.list = list;
    }

    public CurrencyDetails getList() {
        return list;
    }

    public static class CurrencyDetails{

        private final Map<String,String> meta;
        private final List<ResourceMap> resources;

        public CurrencyDetails(Map<String, String> meta, List<ResourceMap> resources) {
            this.meta = meta;
            this.resources = resources;
        }

        public Map<String, String> getMeta() {
            return meta;
        }

        public List<ResourceMap> getResources() {
            return resources;
        }
    }

    public static class ResourceMap{
        private final Resource resource;

        private ResourceMap(Resource resource) {
            this.resource = resource;
        }

        public Resource getResource() {
            return resource;
        }
    }

    public static class Resource{

        private final String classname;
        private final Fields fields;

        public Resource(String classname, Fields fields) {
            this.classname = classname;
            this.fields = fields;
        }

        public String getClassname() {
            return classname;
        }

        public Fields getFields() {
            return fields;
        }
    }

    public static class Fields{
        private final String name;
        private final String price;

        private Fields(String name, String price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public String getPrice() {
            return price;
        }
    }
}
