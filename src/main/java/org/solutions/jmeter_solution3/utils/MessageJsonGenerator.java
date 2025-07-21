package org.solutions.jmeter_solution3.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;

public class MessageJsonGenerator {
   
    private final String baseFile = "generated_message_1.json";
    private final ObjectMapper mapper;
    private final List<String> nameList;

    public MessageJsonGenerator (){
        mapper = new ObjectMapper();

        // Прочитать исходный файл из ресурсов через ClassLoader
        Map<String, Object> baseJson = null;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(baseFile)) {
            if (is == null) {
                throw new RuntimeException("Resource not found: " + baseFile);
            }
            baseJson = mapper.readValue(is, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Map<String, Object>> metrics = (List<Map<String, Object>>) baseJson.get("metrics");

        // Собрать все уникальные name
        Set<String> allNames = new HashSet<>();
        for (Map<String, Object> metric : metrics) {
            allNames.add((String) metric.get("name"));
        }

        nameList = new ArrayList<>(allNames);
    }


    public  String generateMessage() {
          

            Map<String, Object> message = new LinkedHashMap<>();
            long msgTimestamp = System.currentTimeMillis();
            message.put("timestamp", msgTimestamp);

            List<Map<String, Object>> newMetrics = new ArrayList<>();
            for (int j = 0; j < 4850; j++) {
                String name = nameList.get(new Random().nextInt(nameList.size()));
                long metricTimestamp = msgTimestamp + new Random().nextInt(10000) - 5000;
                String dataType = new Random().nextBoolean() ? "Int32" : "Float";
                Object value;
                if (dataType.equals("Int32")) {
                    value = new Random().nextInt(200001) - 100000; // Integer
                } else {
                    value = Math.round((new Random().nextDouble() * 20000 - 10000) * 1_000_000d) / 1_000_000d; // Double
                }

                Map<String, Object> metric = new LinkedHashMap<>();
                metric.put("name", name);
                metric.put("timestamp", metricTimestamp);
                metric.put("dataType", dataType);
                metric.put("value", value);

                newMetrics.add(metric);
            }
            message.put("metrics", newMetrics);
            message.put("seq", new Random().nextInt());

            try { return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);} 
            catch (IOException e) { throw new RuntimeException(e); }

    }
}
