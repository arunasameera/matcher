import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class BSBMatcher {

    public static void main(String[] args) {
        String excelFilePath = "path/to/your/excel/file.xlsx"; // Update with your Excel file path
        String xmlFilePath = "path/to/your/xml/file.xml"; // Update with your XML file path

        // Read BSB numbers from Excel file
        List<String> bsbList = readBSBFromExcel(excelFilePath);

        // Read BSB numbers from XML file
        Set<String> xmlBSBSet = readBSBFromXML(xmlFilePath);

        // Match BSB numbers and print results
        for (String bsb : bsbList) {
            if (xmlBSBSet.contains(bsb)) {
                System.out.println("Matching BSB: " + bsb);
            }
        }
    }

    private static List<String> readBSBFromExcel(String excelFilePath) {
        List<String> bsbList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(new File(excelFilePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
            for (Row row : sheet) {
                Cell cell = row.getCell(0); // Assuming BSB numbers are in the first column
                String bsb = cell.getStringCellValue().trim();
                // Append '0' if BSB length is 5 digits
                if (bsb.length() == 5) {
                    bsb = "0" + bsb;
                }
                bsbList.add(bsb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bsbList;
    }

    private static Set<String> readBSBFromXML(String xmlFilePath) {
        Set<String> bsbSet = new HashSet<>();
        try {
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();
            NodeList issuerList = doc.getElementsByTagName("Issuer");

            for (int i = 0; i < issuerList.getLength(); i++) {
                Node node = issuerList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String bsb = element.getTextContent().trim();
                    bsbSet.add(bsb);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bsbSet;
    }
}