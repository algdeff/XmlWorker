package XmlWorker;

public final class Facade {

    public static final String  EVENT_TYPE_BROADCAST  =  "event_type_broadcast",
                                    EVENT_TYPE_GROUP  =  "event_type_group",
                                  EVENT_TYPE_GENERIC  =  "event_type_generic",

                                     EVENT_GROUP_ALL  =  "event_group_all",
                                  EVENT_GROUP_LOGGER  =  "logger_group_event",
                                      EVENT_GROUP_DB  =  "database_manager_group_event",

                                        CMD_DB_FLUSH  =  "cmd_db_flush",
                                     CMD_DB_SHUTDOWN  =  "cmd_db_shutdown",

                                  CMD_LOGGER_ADD_LOG  =  "cmd_logger_add_log",
                               CMD_LOGGER_ADD_RECORD  =  "cmd_logger_add_record",
                                CMD_LOGGER_CLEAR_LOG  =  "cmd_logger_clear_log";

    private static volatile Facade _instance;
    private static boolean _inited = false;

    private Facade() {
    }

    public static Facade getInstance() {
        if (_instance == null) {
            synchronized (Facade.class) {
                if (_instance == null) {
                    _instance = new Facade();
                }
            }
        }
        return _instance;
    }

    void init() {
        if (_inited) {
            return;
        }


        _inited = true;
    }


}
