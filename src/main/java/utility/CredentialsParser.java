package utility;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Map;
import java.util.HashMap;

public class CredentialsParser {
    private static final File xmlFile = new File("src/main/resources/credentials.xml");
    public static Map<String, String> getDBCredentials() {
        Map<String, String> credentials = new HashMap<>();
        try {
            // xml 파싱 빌드업
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            NodeList nList = doc.getElementsByTagName("db");

            Node nNode = nList.item(0);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                credentials.put("username", eElement.getElementsByTagName("username").item(0).getTextContent());
                credentials.put("password", eElement.getElementsByTagName("password").item(0).getTextContent());
                credentials.put("url", eElement.getElementsByTagName("url").item(0).getTextContent());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return credentials;
    }
}
