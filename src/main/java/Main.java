import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static final Path currentDir = Paths.get(System.getProperty("user.dir"));
    private static final Path outputDir = Paths.get(currentDir.toString(), "output");
    private static final Path inputDir = Paths.get(currentDir.toString(), "input");

    private static final File baseSchema = Paths.get(currentDir.toString(), "schemas", "contact.xsd").toFile();

    private static void printMenu(){
        System.out.println("=============================");
        System.out.println("[J]: Convert to JSON");
        System.out.println("[X]: Convert to XML");
        System.out.println("[V]: Validate against schema");
        System.out.println("[Q]: Quit");
        System.out.println("=============================");
        System.out.println();
    }

    private static void printError(String message){
        System.out.println("\033[0;31m[ERROR] " + message + "\033[0m");
    }

    private static boolean invalidFileExtension(String filename, String fileExtension){
        if (fileExtension.length() >= filename.length()){
            return true;
        }
        return filename.substring(filename.length() - fileExtension.length()).compareTo(fileExtension) != 0;
    }

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        boolean running = true;
            while (running){
                printMenu();
                switch (scan.nextLine()){
                    case "J":
                        List<Path> inputFiles = getInputFiles(".xml");
                        if (inputFiles.size() > 0){
                            System.out.println("Please type the name of the file to convert (e.g., \"ab.xml\")");
                            printInputFiles(inputFiles);
                        } else {
                            printError("No input files found in \"" + inputDir + "\"\n");
                            break;
                        }

                        // Ensure file ends in ".xml" and exists
                        String inputFile = scan.nextLine();
                        Path filepath = Paths.get(inputDir.toString(), inputFile);
                        while (invalidFileExtension(inputFile, ".xml") || !Files.exists(filepath)){
                            if (inputFile.compareToIgnoreCase("M") == 0){
                                break;
                            }
                            if (invalidFileExtension(inputFile, ".xml")){
                                printError("Filename must end in \".xml\"");
                                System.out.println("Enter \"M\" to return to the main menu or type the name of the" +
                                        " file to convert (e.g., \"ab.xml\")");
                            } else if (!Files.exists(filepath)){
                                printError("Unable to find \"" + inputFile + "\" in \"" + inputDir + "\"\n");
                                System.out.println("Enter \"M\" to return to the main menu or type the name of the" +
                                        " file to convert (e.g., \"ab.xml\")");
                            }
                            inputFile = scan.nextLine();
                            filepath = Paths.get(inputDir.toString(), inputFile);
                        }

                        if (inputFile.compareToIgnoreCase("M") == 0){
                            break;
                        }
                        // Ensure file ends in ".json"
                        System.out.println("Please type the name of the file to output to (e.g., \"contacts.json\")");
                        String outputFile = scan.nextLine();
                        while (invalidFileExtension(outputFile, ".json")){
                            if (outputFile.compareToIgnoreCase("M") == 0){
                                break;
                            }
                            printError("Filename must end in \".json\"");
                            System.out.println("Enter \"M\" to return to the main menu or type the name of the file" +
                                    " to output to (e.g., \"contacts.json\")");
                            outputFile = scan.nextLine();
                        }

                        if (outputFile.compareToIgnoreCase("M") == 0){
                            break;
                        }

                        // Write to file
                        try {
                            File file = filepath.toFile();
                            Contact[] contacts = ContactHelper.xmlFileToContactArray(file);
                            JsonArray contactArray = ContactHelper.contactArrayToJsonArray(contacts);
                            String result = writeJson(contactArray, outputFile);
                            System.out.println("Successfully saved to " + result);
                        } catch (ParserConfigurationException | SAXException e) {
                            printError("Failed to convert file.");
                            e.printStackTrace();
                        }
                        break;

                    case "X":
                        System.out.println("You chose X");
                        inputFiles = getInputFiles(".json");
                        if (inputFiles.size() > 0){
                            System.out.println("Please type the name of the file to convert (e.g., \"ab.json\")");
                            printInputFiles(inputFiles);
                        } else {
                            printError("No input files found in \"" + inputDir + "\"\n");
                            break;
                        }

                        // Ensure file ends in ".xml" and exists
                        inputFile = scan.nextLine();
                        filepath = Paths.get(inputDir.toString(), inputFile);
                        while (invalidFileExtension(inputFile, ".json") || !Files.exists(filepath)){
                            if (inputFile.compareToIgnoreCase("M") == 0){
                                break;
                            }
                            if (invalidFileExtension(inputFile, ".json")){
                                printError("Filename must end in \".json\"");
                                System.out.println("Enter \"M\" to return to the main menu or type the name of the" +
                                        " file to convert (e.g., \"contacts.json\")");
                            } else if (!Files.exists(filepath)){
                                printError("Unable to find \"" + inputFile + "\" in \"" + inputDir + "\"\n");
                                System.out.println("Enter \"M\" to return to the main menu or type the name of the" +
                                        " file to convert (e.g., \"contacts.json\")");
                            }
                            inputFile = scan.nextLine();
                            filepath = Paths.get(inputDir.toString(), inputFile);
                        }

                        if (inputFile.compareToIgnoreCase("M") == 0){
                            break;
                        }
                        // Ensure file ends in ".json"
                        System.out.println("Please type the name of the file to output to (e.g., \"contacts.xml\")");
                        outputFile = scan.nextLine();
                        while (invalidFileExtension(outputFile, ".xml")){
                            if (outputFile.compareToIgnoreCase("M") == 0){
                                break;
                            }
                            printError("Filename must end in \".xml\"");
                            System.out.println("Enter \"M\" to return to the main menu or type the name of the file" +
                                    " to output to (e.g., \"contacts.xml\")");
                            outputFile = scan.nextLine();
                        }

                        if (outputFile.compareToIgnoreCase("M") == 0){
                            break;
                        }

                        // Write to file
                        try {
                            File file = filepath.toFile();
                            Contact[] list = ContactHelper.jsonFileToContactArray(file);
                            String result = writeXml(outputFile, list);
                           if (result != null){
                               System.out.println("Successfully saved to " + result);
                           }

                        } catch (ParserConfigurationException e) {
                            printError("Failed to convert file.");
                            e.printStackTrace();
                        }
                        break;

                    case "V":
                        inputFiles = getInputFiles(".xml");
                        if (inputFiles.size() > 0){
                            System.out.println("Please type the name of the file to validate (e.g., \"ab.xml\")");
                            printInputFiles(inputFiles);
                        } else {
                            printError("No input files found in \"" + inputDir + "\"\n");
                            break;
                        }

                        // Ensure file ends in ".xml" and exists
                        inputFile = scan.nextLine();
                        filepath = Paths.get(inputDir.toString(), inputFile);
                        while (invalidFileExtension(inputFile, ".xml") || !Files.exists(filepath)) {
                            if (inputFile.compareToIgnoreCase("M") == 0) {
                                break;
                            }
                            if (invalidFileExtension(inputFile, ".xml")) {
                                printError("Filename must end in \".xml\"");
                                System.out.println("Enter \"M\" to return to the main menu or type the name of the" +
                                        " file to validate (e.g., \"ab.xml\")");
                            } else if (!Files.exists(filepath)) {
                                printError("Unable to find \"" + inputFile + "\" in \"" + inputDir + "\"\n");
                                System.out.println("Enter \"M\" to return to the main menu or type the name of the" +
                                        " file to validate (e.g., \"ab.xml\")");
                            }
                            inputFile = scan.nextLine();
                            filepath = Paths.get(inputDir.toString(), inputFile);
                        }
                            try {
                                validateSchema(baseSchema, Paths.get(inputDir.toString(), inputFile).toFile());
                                System.out.println("Success!");
                            } catch (SAXException e) {
                                printError(e.getMessage());
                            }


                        break;

                    case "Q":
                        running = false;
                        break;

                    default:
                        System.out.println("Command not recognized");
                        break;
                }

            }
            scan.close();


    }

    // Print files in input directory
    private static List<Path> getInputFiles(String fileExt) throws IOException {
        Path current = Paths.get(System.getProperty("user.dir"));
        Path inputDirectory = Paths.get(current.toString(), "input");
        List<Path> files;
        files = Files.list(inputDirectory).filter(path -> path.toString().endsWith(fileExt)).collect(Collectors.toList());
        return files;
    }

    private static void printInputFiles(List<Path> files){
        System.out.printf("Found %d file(s) with the specified filetype in the input directory:\n", files.size());
        for (Path file : files) {
            System.out.printf("> %s\n", file.getFileName());
        }
    }

    private static Path getOutfile(String fileName, String fileExtension){
        // Trim file extension (contacts.json => contacts)
        if (fileName.substring(fileName.length() - fileExtension.length()).compareTo(fileExtension) == 0){
            fileName = fileName.substring(0, fileName.length() - fileExtension.length());
        }

        // Check for desired output path, e.g., contacts.json
        Path outputPath = Paths.get(outputDir.toString(), fileName);
        if (!Files.exists(Paths.get(outputPath + fileExtension))){
            return Paths.get(outputPath + fileExtension);
        }

        // If path exists, add number to file name, e.g., contacts (1).json
        Path uniquePath;
        int counter = 0;
        do {
            uniquePath = Paths.get(String.format("%s (%d)",
                    outputPath, ++counter));
        } while (Files.exists(Paths.get(uniquePath + fileExtension)));
        uniquePath = Paths.get(uniquePath + fileExtension);
        return uniquePath;
    }

    private static String writeJson(JsonArray json, String jsonOutputFile) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path candidate = getOutfile(jsonOutputFile, ".json");
        Writer writer;
        writer = new FileWriter(candidate.toFile());
        gson.toJson(json, writer);
        writer.flush();
        writer.close();
        return candidate.toString();
    }

    private static String writeXml(String fileName, Contact[] contacts) throws ParserConfigurationException {
        Path filepath = getOutfile(fileName, ".xml");
        // root elements
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("AddressBook");
        doc.appendChild(rootElement);

        for (Contact contact : contacts) {
            ContactHelper.contactToXmlNode(doc, rootElement, contact);
        }

        // write dom document to a file
        try (FileOutputStream output =
                     new FileOutputStream(filepath.toString())) {
            writeXml(doc, output);
            return filepath.toString();
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void validateSchema(File schemaFile, File xmlFile) throws SAXException, IOException {

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlFile));

    }

    // write doc to output stream
    private static void writeXml(Document doc,
                                 OutputStream output) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        // pretty print XML
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);

    }
}
