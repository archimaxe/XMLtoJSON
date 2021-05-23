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
import org.xml.sax.XMLReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception{
//        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.xml";
        List<Employee> emps = parseXml(fileName);
        String employs = listToJson(emps);
        writeString(employs);
    }

    // Получаем список сотрудников:
    public static List<Employee> parseCSV(String[] columnMapping, String fileName) throws Exception {
        File csvFile = new File(fileName);
        if (csvFile.exists()) {
            try (CSVReader reader = new CSVReader(new FileReader(csvFile))){

                /** ColumnPositionMappingStrategy определяет класс, к которому будут привязывать
                данные из CSV документа, а также порядок расположения полей в этом документе */

                ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(Employee.class);
                strategy.setColumnMapping(columnMapping);

                /** CsvToBean создает инструмент для взаимодействия CSV документа и выбранной ранее стратегии
                 * CsvToBean позволяет распарсить CSV файл в список объектов */

                CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                        .withMappingStrategy(strategy)
                        .build();
                return csvToBean.parse();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new FileNotFoundException("File now found");
        }
        return null;
    }

    public static List<Employee> parseXml(String fileName){
        File xmlFile = new File(fileName);
        List<Employee> workerList = new ArrayList<>();
        if (xmlFile.exists()) {
            try {
                /** DOM XML пapcep читaeт coдepжимoe XML фaйлa и зaгpyжaeт его в oпepaтивнyю
                пaмять. Taким oбpaзoм, cтpoитcя oбъeктнaя мoдeль иcхoднoгo XML документа,
                используя кoтopyю мoжнo paбoтaть c дaнными */
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(xmlFile);

                // Получаем корневой узел элемента:
                Node root = doc.getDocumentElement();

                // Получаем список дочерних узлов:
                NodeList nodeList = root.getChildNodes();

                for (int i = 0; i < nodeList.getLength(); i++){
                    Node node = nodeList.item(i);

                    /** Используя метод getNodeType(), можно узнать тип узла */
                    if (Node.ELEMENT_NODE == node.getNodeType()){
                        Element employee = (Element) node;

                        /** Узлы можно найти по их тагу с помощью метода getElementsByTagName() */
                        String stringId = employee.getElementsByTagName("id").item(0).getTextContent();
                        long id = Integer.parseInt(stringId);
                        String firstName = employee.getElementsByTagName("firstName").item(0).getTextContent();
                        String lastName = employee.getElementsByTagName("lastName").item(0).getTextContent();
                        String country = employee.getElementsByTagName("country").item(0).getTextContent();
                        String stringAge = employee.getElementsByTagName("age").item(0).getTextContent();
                        int age = Integer.parseInt(stringAge);
                        Employee worker = new Employee(id, firstName, lastName, country, age);
                        workerList.add(worker);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return workerList;
    }

    /** Полученный список преобразуем в строчку в формате JSON - Конвертируем объект созданного класса в JSON при помощи метода toJson()  */
    public static String listToJson(List<Employee> object){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(object, listType);
    }

    /** Далее запишем полученный JSON в файл с помощью метода writeString() */
    public static void writeString(String employee){
        try (FileWriter file = new FileWriter("data.json")){
            file.write(employee.toString());
            file.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


