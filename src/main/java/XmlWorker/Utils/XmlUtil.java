package XmlWorker.Utils;

import XmlWorker.Facade;
import XmlWorker.HibernateEntities.TestEntity;
import XmlWorker.Logic.ConfigManager;
import XmlWorker.Logic.ThreadPoolManager;
import XmlWorker.Logic.Workers.GenerateDatabaseContentThread;
import XmlWorker.Logic.db.DatabaseManager;
import XmlWorker.Publisher.Publisher;
import org.jdom2.*;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class XmlUtil {

    String _rootEntry;
    String _entryName;
    String _entryContent;


    public XmlUtil(String rootEntry, String entryName, String entryContent) {
        _rootEntry = rootEntry;
        _entryName = entryName;
        _entryContent = entryContent;
    }

    public void step2(int numberDatabaseRecords) {
        Publisher.getInstance().sendPublisherEvent(Facade.CMD_LOGGER_ADD_LOG,
                "Step2 begin.....");

        List<String> records = new ArrayList<>();
        for (int i=1; i <= numberDatabaseRecords; i++) {
            records.add(String.valueOf(i));
        }

        System.out.println("total records prepaired: " + records.size());

        int maxRecordsPerTask = 1000;

        Iterator<String> iterator = records.iterator();
        List<String> task = new ArrayList<>();

        while (iterator.hasNext()) {
            task.add(iterator.next());
            if (task.size() > maxRecordsPerTask) {
                System.out.println("prepared task, size: " + task.size());
                ThreadPoolManager.getInstance().executeFutureTask(new GenerateDatabaseContentThread(task));
                task.clear();
            }
        }
        System.out.println("prepared task, remain task size: " + task.size());
        if (task.size() > 0) {
            ThreadPoolManager.getInstance().executeFutureTask(new GenerateDatabaseContentThread(task));
            task.clear();
        }


        List<String> result = new ArrayList<>();
        int resultSize = 0;
        while (resultSize < numberDatabaseRecords) {

            System.out.println("Reading result....");

            Future<ArrayList> future = ThreadPoolManager.getInstance().getCompletionFutureTask();

            try {
                result.addAll(future.get());
                //result = future.get();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            } catch (ExecutionException ee) {
                ee.printStackTrace();
            }

            resultSize = result.size();
            System.out.println("result complete - " + result.size() + "/" + resultSize);

        }

        Publisher.getInstance().sendPublisherEvent(Facade.CMD_DB_FLUSH);
        Publisher.getInstance().sendGroupEvent(Facade.EVENT_GROUP_LOGGER,
                "Step2 COMPLETE");
    }

    public void step3(Path outputFilename, String tableName, String fieldName) {
        Publisher.getInstance().sendPublisherEvent(Facade.CMD_LOGGER_ADD_LOG,
                "Step3 begin.....");

        Document xmlDoc = new Document();
        Element root = new Element(_rootEntry);
        xmlDoc.setRootElement(root);

        List list = DatabaseManager.getInstance().getSortedResultset(tableName, fieldName, "asc");

        //int numEntries = list.size();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Element entry = new Element(_entryName);
            //entry.addContent(new Comment("field value"));
            Element field = new Element(_entryContent);
            field.addContent(iterator.next().toString());
            entry.addContent(field);
            root.addContent(entry);
        }

        saveFile(outputFilename, xmlDoc);

//        OutputStream output = null;
//        try {
//            output = new BufferedOutputStream(new FileOutputStream(filename.toFile()));
//            Format fmt = Format.getPrettyFormat();
//            XMLOutputter serializer = new XMLOutputter(fmt);
//            serializer.output(xmlDoc, System.out);
//            serializer.output(xmlDoc, output);
//        } catch (IOException e) {
//            System.err.println(e);
//        } finally {
//            try {
//                output.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        Publisher.getInstance().sendPublisherEvent(Facade.CMD_LOGGER_ADD_LOG,
                "Step3 COMPLETE");
    }

    public void step4(Path inputFile, Path outputFile) {
        Publisher.getInstance().sendPublisherEvent(Facade.CMD_LOGGER_ADD_LOG,
                "Step4 begin.....");
        ArrayList<String> resultset = new ArrayList<>();

        Document inputXmlDoc;

        Document outXmlDoc = new Document();
        Element outRoot = new Element(_rootEntry);
        outXmlDoc.setRootElement(outRoot);

        SAXBuilder parser = new SAXBuilder();

        try {
            inputXmlDoc = parser.build(new File(inputFile.toString()));
            List elements = inputXmlDoc.getRootElement().getContent(new ElementFilter(_entryName));
            Iterator iterator = elements.iterator();

            while (iterator.hasNext()) {
                Element inputEntry = (Element)iterator.next();
                //String id = entry.getAttributeValue("id");
                String inputContent = inputEntry.getChildText(_entryContent);

                Element outEntry = new Element(_entryName);
                outEntry.setAttribute(_entryContent, inputContent);
                outRoot.addContent(outEntry);
            }

        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }

        saveFile(outputFile, outXmlDoc);

        Publisher.getInstance().sendPublisherEvent(Facade.CMD_LOGGER_ADD_LOG,
                "Step4 COMPLETE");
        //return resultset;
    }


    private void saveFile(Path filename, Document outXmlDoc) {

        OutputStream output = null;
        try {
            output = new BufferedOutputStream(new FileOutputStream(filename.toFile()));
            Format fmt = Format.getPrettyFormat();
            XMLOutputter serializer = new XMLOutputter(fmt);
            //serializer.output(outXmlDoc, System.out);
            serializer.output(outXmlDoc, output);
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}