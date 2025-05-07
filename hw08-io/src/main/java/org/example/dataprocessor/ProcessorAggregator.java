package org.example.dataprocessor;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.example.model.Measurement;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        // группирует выходящий список по name, при этом суммирует поля value

        if (data == null) {
            return new TreeMap<>();
        }
        return data.stream()
                .collect(Collectors.groupingBy(
                        Measurement::name, TreeMap::new, Collectors.summingDouble(Measurement::value)));
    }
}
