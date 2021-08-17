import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * A helper class allowing for the conversion between JSON and XML
 * @author Emily Fooe
 *
 */
public class ContactHelper {
    private static final String CONTACT = "Contact";
    private static final String CUSTOMER_ID = "CustomerID";
    private static final String COMPANY_NAME = "CompanyName";
    private static final String CONTACT_NAME = "ContactName";
    private static final String CONTACT_TITLE = "ContactTitle";
    private static final String ADDRESS = "Address";
    private static final String CITY = "City";
    private static final String EMAIL = "Email";
    private static final String REGION = "Region";
    private static final String POSTAL_CODE = "PostalCode";
    private static final String COUNTRY = "Country";
    private static final String PHONE = "Phone";
    private static final String FAX = "Fax";

    // Saves JsonArray in .json file
    public static Contact[] jsonFileToContactArray(File file) throws FileNotFoundException {
        return new Gson().fromJson(new FileReader(file), Contact[].class);
    }

    // Converts Contact[] to JsonArray
    public static JsonArray contactArrayToJsonArray(Contact[] contacts){
        Gson gson = new Gson();
        JsonArray contactArray = new JsonArray();
        for (Contact contact : contacts){
            contactArray.add(gson.toJsonTree(contact));
        }
        return contactArray;
    }

    // Converts contacts stored in an XML address book to Contact[]
    public static Contact[] xmlFileToContactArray(File file) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        document.getDocumentElement().normalize();

        NodeList contactList = document.getElementsByTagName(CONTACT);

        Contact[] contacts = new Contact[contactList.getLength()];
        for (int i = 0; i < contactList.getLength(); i++){
            Node currentNode = contactList.item(i);
            if (currentNode.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element currentElement = (Element)currentNode;
            contacts[i] = ContactHelper.xmlNodeToContact(currentElement);
        }
        return contacts;
    }


    // Create elements for each XML node during document construction
    public static void contactToXmlNode(Document doc, Element root, Contact contact){
        Element contactNode = doc.createElement(CONTACT);
        root.appendChild(contactNode);

        Element customerId = doc.createElement(CUSTOMER_ID);
        customerId.appendChild(doc.createTextNode(contact.getCustomerId()));
        contactNode.appendChild(customerId);

        Element companyName = doc.createElement(COMPANY_NAME);
        companyName.appendChild(doc.createTextNode(contact.getCompanyName()));
        contactNode.appendChild(companyName);

        Element contactName = doc.createElement(CONTACT_NAME);
        contactName.appendChild(doc.createTextNode(contact.getContactName()));
        contactNode.appendChild(contactName);

        Element contactTitle = doc.createElement(CONTACT_TITLE);
        contactTitle.appendChild(doc.createTextNode(contact.getContactTitle()));
        contactNode.appendChild(contactTitle);

        Element address = doc.createElement(ADDRESS);
        address.appendChild(doc.createTextNode(contact.getAddress()));
        contactNode.appendChild(address);

        Element city = doc.createElement(CITY);
        city.appendChild(doc.createTextNode(contact.getCity()));
        contactNode.appendChild(city);

        Element email = doc.createElement(EMAIL);
        email.appendChild(doc.createTextNode(contact.getEmail()));
        contactNode.appendChild(email);

        if (contact.getRegion() != null){
            Element region = doc.createElement(REGION);
            region.appendChild(doc.createTextNode(contact.getRegion()));
            contactNode.appendChild(region);
        }

        if (contact.getPostalCode() != null){
            Element postalCode = doc.createElement(POSTAL_CODE);
            postalCode.appendChild(doc.createTextNode(contact.getPostalCode()));
            contactNode.appendChild(postalCode);
        }

        Element country = doc.createElement(COUNTRY);
        country.appendChild(doc.createTextNode(contact.getCountry()));
        contactNode.appendChild(country);

        Element phone = doc.createElement(PHONE);
        phone.appendChild(doc.createTextNode(contact.getPhone()));
        contactNode.appendChild(phone);

        if (contact.getFax() != null){
            Element fax = doc.createElement(FAX);
            fax.appendChild(doc.createTextNode(contact.getFax()));
            contactNode.appendChild(fax);
        }
    }

    // Read in an XML node and convert to Contact
    public static Contact xmlNodeToContact(Element node){
        String customerId = node.getElementsByTagName(CUSTOMER_ID).item(0).getChildNodes().item(0).getNodeValue();
        String companyName = node.getElementsByTagName(COMPANY_NAME).item(0).getChildNodes().item(0).getNodeValue();
        String contactName = node.getElementsByTagName(CONTACT_NAME).item(0).getChildNodes().item(0).getNodeValue();
        String contactTitle = node.getElementsByTagName(CONTACT_TITLE).item(0).getChildNodes().item(0).getNodeValue();
        String address = node.getElementsByTagName(ADDRESS).item(0).getChildNodes().item(0).getNodeValue();
        String city = node.getElementsByTagName(CITY).item(0).getChildNodes().item(0).getNodeValue();
        String email = node.getElementsByTagName(EMAIL).item(0).getChildNodes().item(0).getNodeValue();
        // Region, postal code, and fax are optional fields
        String region = null;
        if (node.getElementsByTagName(REGION).item(0) != null){
            region = node.getElementsByTagName(REGION).item(0).getChildNodes().item(0).getNodeValue();
        }
        String postalCode = null;
        if (node.getElementsByTagName(POSTAL_CODE).item(0) != null){
            postalCode = node.getElementsByTagName(POSTAL_CODE).item(0).getChildNodes().item(0).getNodeValue();
        }
        String country = node.getElementsByTagName(COUNTRY).item(0).getChildNodes().item(0).getNodeValue();
        String phone = node.getElementsByTagName(PHONE).item(0).getChildNodes().item(0).getNodeValue();
        String fax = null;
        if (node.getElementsByTagName(FAX).item(0) != null){
            fax = node.getElementsByTagName(FAX).item(0).getChildNodes().item(0).getNodeValue();
        }
        return (new Contact.Builder(customerId)
                .companyName(companyName)
                .contactName(contactName)
                .contactTitle(contactTitle)
                .address(address)
                .city(city)
                .email(email)
                .region(region)
                .postalCode(postalCode)
                .country(country)
                .phone(phone)
                .fax(fax)).build();
    }
}
