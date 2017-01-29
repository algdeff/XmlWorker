package XmlWorker.Logic;

import XmlWorker.ServerStarter;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigManager {

    /**
     ConfigManager

     Property keys from config file
     default: xmlworker.conf.xml
     */

    private static final String         CONFIG_FILE_PATH  =  "xmlworker.conf.xml",

                                             OUTPUT_PATH  =  "processed_files_path",
                                           LOG_FILE_PATH  =  "log_file_path_name",
                                               HDB_TABLE  =  "hdb_table_name",
                                          HDB_DATA_FIELD  =  "hdb_data_field_name",
                                          XML_ROOT_ENTRY  =  "xml_root_entry",
                                          XML_ENTRY_NAME  =  "xml_entry_name",
                                       XML_ENTRY_CONTENT  =  "xml_entry_content",
                                             GENERATED_N  =  "num_generated_entries",
                                        THREAD_POOL_SIZE  =  "thread_pool_size",
                                   TARGET_FILE_TYPE_GLOB  =  "target_file_type_glob";

    private static final ConcurrentHashMap<String, String> _properties;

    static {
        Configurations configs = new Configurations();
        _properties = new ConcurrentHashMap<>();
        try {
            FileBasedConfigurationBuilder<XMLConfiguration> builder = configs.xmlBuilder(CONFIG_FILE_PATH);
            XMLConfiguration config = builder.getConfiguration();
            Iterator<String> iterator = config.getKeys();
            while (iterator.hasNext()) {
                String propertyName = iterator.next();
                System.err.println(propertyName + ": " + config.getString(propertyName));
                _properties.put(propertyName, config.getString(propertyName));
            }

        } catch (Exception cex) {
            System.err.println("Correct config file not found: " + CONFIG_FILE_PATH);
            ServerStarter.stopAndExit(1);
        }
    }

//    private static class SingletonInstance {
//        private static final ConfigManager INSTANCE = new ConfigManager();
//    }

    private ConfigManager() {
    }

//    public static ConfigManager getInstance() {
//        return SingletonInstance.INSTANCE;
//    }

    public static void init() {
    }

    public static Path getProcessedFilesPath() {
        return Paths.get(_properties.get(OUTPUT_PATH));
    }


    public static Path getLogFilePath() {
        return Paths.get(_properties.get(LOG_FILE_PATH));
    }

    public static String getHdbTableName() {
        return _properties.get(HDB_TABLE);
    }

    public static String getHdbDataFieldName() {
        return _properties.get(HDB_DATA_FIELD);
    }

    public static String getXmlRootEntry() {
        return _properties.get(XML_ROOT_ENTRY);
    }

    public static String getXmlEntryName() {
        return _properties.get(XML_ENTRY_NAME);
    }

    public static String getXmlEntryContent() {
        return _properties.get(XML_ENTRY_CONTENT);
    }

    public static int getNumGeneratedEntries() {
        return Integer.parseInt(_properties.get(GENERATED_N));
    }

    public static int getThreadPoolSize() {
        return Integer.parseInt(_properties.get(THREAD_POOL_SIZE));
    }

    public static String getTargetFileTypeGlob() {
        return _properties.get(TARGET_FILE_TYPE_GLOB);
    }

}