import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Parse {
    protected  List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> csvStaff = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))){
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> ctb = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            csvStaff = ctb.parse();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return csvStaff;
    }

    protected String listToJson(List<Employee> list) {
        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }
    protected void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    protected List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> listEmployees = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(fileName));
        NodeList nodeList = document.getElementsByTagName("employee");
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node elementNodeList = nodeList.item(i);
                if (elementNodeList.getNodeType() == Node.ELEMENT_NODE) {
                    Element employeeElement = (Element) elementNodeList;
                    listEmployees.add(new Employee(
                            Integer.parseInt(employeeElement.getElementsByTagName("id").item(0).getTextContent()),
                            employeeElement.getElementsByTagName("firstName").item(0).getTextContent(),
                            employeeElement.getElementsByTagName("lastName").item(0).getTextContent(),
                            employeeElement.getElementsByTagName("country").item(0).getTextContent(),
                            Integer.parseInt(employeeElement.getElementsByTagName("age").item(0).getTextContent())));

                }
            }
        }
        return listEmployees;
    }

}
