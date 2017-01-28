package XmlWorker.Logic;

import XmlWorker.Logic.db.DatabaseManager;
import XmlWorker.ServerStarter;
import XmlWorker.Utils.XmlUtil;

import java.io.IOException;
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
        String         rootEntry = ConfigManager.getXmlRootEntry();
        String         entryName = ConfigManager.getXmlEntryName();
        String      entryContent = ConfigManager.getXmlEntryContent();
        String     databaseTable = ConfigManager.getHdbTableName();
        String     databaseField = ConfigManager.getHdbDataFieldName();


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
        Integer sum = xmlUtil.step5(file2);
        System.err.println("Total sum of all entries(" + file2.getFileName() + "): \n" + sum);

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

