package XmlWorker;

import XmlWorker.Logic.ConfigManager;
import XmlWorker.Logic.ThreadPoolManager;
import XmlWorker.Logic.TestCase;
import XmlWorker.Logic.Workers.LogFileWorker;
import XmlWorker.Logic.db.DatabaseManager;
import XmlWorker.Publisher.Publisher;

public class ServerStarter {

    private static String _serverName = "XmlWorker";
    private static ServerStarter _instance;

    private TestCase _testCase;

    private ServerStarter() {
    }

    public static void main(String[] args) {

        //InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());

        try {
            _instance = new ServerStarter();
            _instance.start();

        } catch (Throwable ex) {
            Publisher.getInstance().sendPublisherEvent(Facade.CMD_LOGGER_ADD_LOG,
                    "Failed start server:" + ex.getMessage());
            ex.printStackTrace(System.err);
            stopServerInstance();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            //_timer.cancel();
            stopServerInstance();
            //LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            //lc.stop();
        }));
    }


    private void start() {

        Facade.getInstance().init();
        ConfigManager.init();
        DatabaseManager.getInstance().init();
        LogFileWorker.getInstance().init();
        ThreadPoolManager.getInstance()
                .init(ConfigManager.getThreadPoolSize());

        _testCase = new TestCase();
        _testCase.start();
    }

    private void stop() {

        Publisher.getInstance().sendPublisherEvent(Facade.CMD_DB_SHUTDOWN);
        System.err.println("Server STOP");
    }

    private static void stopServerInstance() {
        if (_instance != null) {
            final ServerStarter serverStarter = _instance;
            _instance = null;
            serverStarter.stop();
        }
    }

    public static void stopAndExit(int status) {
        stopServerInstance();
        System.exit(status);
    }

    public static String getServerName() {
        return _serverName;
    }
}
