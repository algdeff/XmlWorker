package XmlWorker.Logic;

import XmlWorker.Facade;
import XmlWorker.Logic.db.DatabaseManager;
import XmlWorker.Publisher.Publisher;
import XmlWorker.ServerStarter;
import XmlWorker.Utils.XmlUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;

public class TestCase {

    private Path _processedPath;

    public TestCase() {
    }

    public void start() {

        /**
         *  Prepare configs from: xmlworker.conf.xml
         */
        _processedPath = ConfigManager.getProcessedFilesPath();
        prepareWorkFolders();

        Path file1 = Paths.get(_processedPath.toString(), "1.xml");
        Path file2 = Paths.get(_processedPath.toString(), "2.xml");

        final int              N = ConfigManager.getNumGeneratedEntries();
        String         rootEntry = ConfigManager.getXmlRootEntry(),
                       entryName = ConfigManager.getXmlEntryName(),
                    entryContent = ConfigManager.getXmlEntryContent(),
                   databaseTable = ConfigManager.getHdbTableName(),
                   databaseField = ConfigManager.getHdbDataFieldName();


        /**
         *  Truncate the database table
         */
        DatabaseManager.getInstance().truncateTable(databaseTable);


        /**
         *  Create XML processor, and run steps....
         */
        XmlUtil xmlUtil = new XmlUtil(rootEntry, entryName, entryContent);

        //  Step2 - Add entries to database (1.....N)
        xmlUtil.step2(N);

        //  Step3 - Create XML from database entries and save this (to file1.xml)
        xmlUtil.step3(file1, databaseTable, databaseField, "asc");

        //  Step4 - Read data from file (1.xml) -> parse -> save result to 2.xml
        xmlUtil.step4(file1, file2);


        /**
         *  Step5 - Read data from entries (2.xml) -> parse it -> calculate sum of all entries
         *  Result:
         */
        Path inputFile = file2;
        long sum = xmlUtil.step5(inputFile);

        String message = "Total sum of all entries(" + inputFile.getFileName() + "): \n" + sum;
        System.out.println(message);
        Publisher.getInstance().sendPublisherEvent(Facade.CMD_LOGGER_ADD_LOG, message);

        //  Program exit
        System.out.println("Do you want to terminate the program?");
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        try {
            String keyStream = stdin.readLine();
            if (keyStream.equals("y") || keyStream.equals("Y")) {
                System.out.println("EXIT");
                ServerStarter.stopAndExit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void prepareWorkFolders() {

        try {
            Files.createDirectories(_processedPath);
        } catch (FileAlreadyExistsException faee) {
            System.err.println("Please rename this file: " + _processedPath);
            ServerStarter.stopAndExit(1);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    private boolean isCorrectFile(Path pathname) {
        if (Files.isSymbolicLink(pathname)
                || !Files.isWritable(pathname)
                || Files.isDirectory(pathname)) return false;

        PathMatcher pathMatcher = FileSystems.getDefault()
                .getPathMatcher("glob:" + ConfigManager
                        .getTargetFileTypeGlob());

        return pathMatcher.matches(pathname.getFileName());
    }

}
