package XmlWorker.Logic.Workers;

import XmlWorker.Facade;
import XmlWorker.Logic.ConfigManager;
import XmlWorker.Logic.ThreadPoolManager;
import XmlWorker.Publisher.Interfaces.IListener;
import XmlWorker.Publisher.Interfaces.IPublisherEvent;
import XmlWorker.Publisher.Publisher;
import XmlWorker.ServerStarter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LogFileWorker implements IListener {

    private Path _logFilePath;

    private static boolean _inited = false;

    private static class SingletonInstance {
        private static final LogFileWorker INSTANCE = new LogFileWorker();
    }

    private LogFileWorker() {
    }

    public static LogFileWorker getInstance() {
        return SingletonInstance.INSTANCE;
    }

    public void init() {
        if (_inited) return;

        _logFilePath = ConfigManager.getLogFilePath();
        _inited = true;
        registerOnPublisher();
        //startAutoLoggingRecords();
    }

    private void startAutoLoggingRecords() {

        while (true) {

            Future<ArrayList> future = ThreadPoolManager.getInstance().getCompletionFutureTask();
            List<String> result = new ArrayList<>();
            try {
                result = future.get();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            } catch (ExecutionException ee) {
                ee.printStackTrace();
            }

            addRecords(result);
        }

    }

    private void addLog(String message) {
        addRecord(dateTimeNow() + " - " + message);
    }

    private void addRecord(String record) {
        List<String> records = new ArrayList<>();
        records.add(record);
        addRecords(records);
    }

    private void addRecords(List<String> records) {

        try {
            Files.write(_logFilePath, records, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
            ServerStarter.stopAndExit(1);
        }

    }

    private String dateTimeNow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    @Override
    public void registerOnPublisher() {
        Publisher.getInstance().registerNewListener(this, Facade.EVENT_GROUP_LOGGER);
    }

    @Override
    public String[] listenerInterests() {
        return new String[] {
                Facade.CMD_LOGGER_ADD_LOG,
                Facade.CMD_LOGGER_ADD_RECORD,
                Facade.CMD_LOGGER_CLEAR_LOG
        };
    }

    @Override
    public void listenerHandler(IPublisherEvent publisherEvent) {
//        System.out.println("Logger received event " + publisherEvent.getName()
//                + " / " + publisherEvent.getType() + ":\n" + publisherEvent.getBody().toString());
        if (publisherEvent.getType().equals(Facade.EVENT_TYPE_GROUP)) {
            addLog(publisherEvent.getBody().toString());
            System.err.println("Logger received group event ("
                    + publisherEvent.getGroupName() + "): \n" + publisherEvent.getBody().toString());
        }

        switch (publisherEvent.getName()) {
            case Facade.CMD_LOGGER_ADD_LOG: {
                addLog(publisherEvent.getBody().toString());
                System.out.println("Logger event ("
                        + publisherEvent.getName() + "): \n" + publisherEvent.getBody().toString());
                break;

            }
            case Facade.CMD_LOGGER_ADD_RECORD: {
                addRecord(publisherEvent.getBody().toString());
                System.out.println("Logger event ("
                        + publisherEvent.getName() + "): \n" + publisherEvent.getBody().toString());
                break;

            }
            case Facade.CMD_LOGGER_CLEAR_LOG: {
                System.out.println("Logger event ("
                        + publisherEvent.getName() + "): \n" + publisherEvent.getBody().toString());
                break;
            }
        }
    }

}

