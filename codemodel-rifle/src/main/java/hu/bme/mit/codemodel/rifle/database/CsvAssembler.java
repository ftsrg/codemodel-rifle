package hu.bme.mit.codemodel.rifle.database;

import hu.bme.mit.codemodel.rifle.database.querybuilder.AsgNode;
import hu.bme.mit.codemodel.rifle.database.querybuilder.AsgRelation;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CsvAssembler {

    private List<Map<String, Object>> nodeCsvElements = new ArrayList<>();
    private CellProcessor[] nodeCellProcessors;
    private String[] nodeHeaders;

    private List<Map<String, Object>> relationshipCsvElements = new ArrayList<>();
    private CellProcessor[] relationshipCellProcessors;
    private String[] relationshipHeaders;

    private void configureNodesWriter() {
        List<CellProcessor> processors = new ArrayList<>();
        List<String> headerElements = new ArrayList<>();

        Set<String> allPropertyNames = new HashSet<>();
        this.nodeCsvElements.forEach(elem -> allPropertyNames.addAll(elem.keySet()));

        for (String propertyName : allPropertyNames) {
            processors.add(new Optional());
            headerElements.add(propertyName);
        }

        this.nodeCellProcessors = processors.toArray(new CellProcessor[processors.size()]);
        this.nodeHeaders = headerElements.toArray(new String[headerElements.size()]);
    }

    private void configureRelationshipsWriter() {
        this.relationshipCellProcessors = new CellProcessor[] { new NotNull(), new NotNull(), new NotNull() };
        this.relationshipHeaders = new String[] { ":START_ID", ":END_ID", ":TYPE" };
    }

    public void addNodeToCsv(AsgNode node) {
        Map<String, Object> csvElementFromNode = new HashMap<>();
        csvElementFromNode.put(":LABEL", node.getLabels().parallelStream().collect(Collectors.joining(";")));
        csvElementFromNode.putAll(node.getProperties());

        csvElementFromNode.remove("id");
        csvElementFromNode.put("id:ID", node.getId());

        this.nodeCsvElements.add(csvElementFromNode);
    }

    public void addRelationShipToCsv(AsgRelation relation) {
        Map<String, Object> csvElementFromRelation = new HashMap<>();
        csvElementFromRelation.put(":START_ID", relation.getFromNode().getId());
        csvElementFromRelation.put(":END_ID", relation.getToNode().getId());
        csvElementFromRelation.put(":TYPE", relation.getRelationshipLabel());
        this.relationshipCsvElements.add(csvElementFromRelation);
    }

    public void writeCsv(String intoWhichDirectory, String fileName) throws Exception {
        this.writeNodesCsv(intoWhichDirectory, fileName);
        this.writeRelationshipsCsv(intoWhichDirectory, fileName);
    }

    private void writeNodesCsv(String intoWhichDirectory, String fileName) throws Exception {
        this.configureNodesWriter();

        File file = Paths.get(intoWhichDirectory, fileName + "-nodes.csv").toFile();

        try (CsvMapWriter mapWriter = new CsvMapWriter(new FileWriter(file.getAbsolutePath()), CsvPreference.STANDARD_PREFERENCE)) {
            mapWriter.writeHeader(this.nodeHeaders);
            for (Map<String, Object> csvElement : nodeCsvElements) {
                mapWriter.write(csvElement, this.nodeHeaders, this.nodeCellProcessors);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeRelationshipsCsv(String intoWhichDirectory, String fileName) throws Exception {
        this.configureRelationshipsWriter();

        File file = Paths.get(intoWhichDirectory, fileName + "-relationships.csv").toFile();

        try (CsvMapWriter mapWriter = new CsvMapWriter(new FileWriter(file.getAbsolutePath()), CsvPreference.STANDARD_PREFERENCE)) {
            mapWriter.writeHeader(this.relationshipHeaders);
            for (Map<String, Object> csvElement : relationshipCsvElements) {
                mapWriter.write(csvElement, this.relationshipHeaders, this.relationshipCellProcessors);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
