package XmlWorker.Logic.db;

import XmlWorker.Facade;
import XmlWorker.HibernateEntities.TestEntity;
import XmlWorker.Publisher.Interfaces.IListener;
import XmlWorker.Publisher.Interfaces.IPublisherEvent;
import XmlWorker.Publisher.Publisher;
import org.hibernate.*;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.query.Query;
import org.hibernate.service.spi.Stoppable;

import javax.persistence.QueryHint;
import javax.persistence.metamodel.EntityType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public final class DatabaseManager implements IListener{

//    private static final String CONFIG_FILE_LOCATION = "/hibernate.cfg.xml";
//    private static Configuration configuration = new Configuration();

    private static final ThreadLocal<Session> THREAD_LOCAL = new ThreadLocal<>();
    private static SessionFactory sessionFactory;

    private static final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure(new File("hibernate.cfg.xml")).build();

    static {
        try {
//            configuration.configure(CONFIG_FILE_LOCATION);
//            sessionFactory = configuration.buildSessionFactory();
            //sessionFactory = cfg.configure(new File("hibernate.cfg.xml")).buildSessionFactory();

            MetadataSources metadataSources = new MetadataSources(registry);
            sessionFactory = metadataSources.buildMetadata().buildSessionFactory();

        } catch (Throwable ex) {
            System.err.println("Error DataBase initialization");
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static boolean _init = false;

    private static class SingletonInstance {
        private static final DatabaseManager INSTANCE = new DatabaseManager();
    }

    private DatabaseManager() {
    }

    public static DatabaseManager getInstance() {
        return SingletonInstance.INSTANCE;
    }

    private static void rebuildSessionFactory() {
        try {
//            configuration.configure(CONFIG_FILE_LOCATION);
//            sessionFactory = configuration.buildSessionFactory();

            MetadataSources metadataSources = new MetadataSources(registry);
            sessionFactory = metadataSources.buildMetadata().buildSessionFactory();

        } catch (Exception e) {
            System.err.println("Error Creating SessionFactory");
            e.printStackTrace();
        }
    }

    public static Session getSession() throws HibernateException {
        Session session = THREAD_LOCAL.get();

        if (session == null || !session.isOpen()) {
            if (sessionFactory == null) rebuildSessionFactory();
            session = sessionFactory.openSession();
            THREAD_LOCAL.set(session);
        }
        return session;
    }

    public static void closeSession() throws HibernateException {
        Session session = THREAD_LOCAL.get();
        THREAD_LOCAL.set(null);

        if (session != null) {
            session.close();
        }
    }

    public void init() {
        if (_init) return;

        registerOnPublisher();
        _init = true;
    }

    private void flushCache() {
        System.out.println("Database manager: flush");
        getSession().flush();
        closeSession();
    }

    private void shutdown() {
//        final SessionFactoryImplementor sessionFactoryImplementor = (SessionFactoryImplementor) sessionFactory;
//        ConnectionProvider connectionProvider = sessionFactoryImplementor.getConnectionProvider();
//        if (Stoppable.class.isInstance(connectionProvider)) {
//            ((Stoppable) connectionProvider).stop();
//        }
        sessionFactory.close();
    }

    public void saveEntity(Object entity) {
        final Session session = getSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(entity);
        transaction.commit();
        session.close();
    }

    public List executeQuery(String hqlQuery) {
        final Session session = getSession();
        List result = new ArrayList();

        try {
            Transaction transaction = session.beginTransaction();
            Query query = session.createQuery(hqlQuery);
            result = query.getResultList(); //query.list();
            query.executeUpdate();
            transaction.commit();

        } catch (IllegalArgumentException | HibernateException ex) {
            System.err.println("Wrong database query!");
        } finally {
            session.close();
        }

        return result;
    }

    public void truncateTable(String tableName){
        final Session session = getSession();
        String hql = String.format("delete from %s", tableName);

        try {
            Transaction transaction = session.beginTransaction();
            session.createQuery(hql).executeUpdate();
            transaction.commit();
        } catch (HibernateException he) {
            System.err.println("Wrong database query!");
        } finally {
            session.close();
        }
    }

    public List getQueryResult(String hqlQuery) {
        final Session session = getSession();
        List result = new ArrayList();

        try {
            Transaction transaction = session.beginTransaction();
            Query query = session.createQuery(hqlQuery);
            result = query.list();
            transaction.commit();

        } catch (IllegalArgumentException | HibernateException ex) {
            System.err.println("Wrong database query!");
        } finally {
            session.close();
        }

        return result;
    }

    public List getSortedResultset(String tableName, String fieldName, String orderBy_asc_desc) {
        String order = "";
        if (orderBy_asc_desc != null && ( orderBy_asc_desc.equals("asc") || orderBy_asc_desc.equals("desc") )) {
            order = String.format("order by %1$s %2$s", fieldName, orderBy_asc_desc);
        }
        String hqlQuery = String.format("select %2$s from %1$s %3$s", tableName, fieldName, order);
        System.out.println(hqlQuery);
        return getQueryResult(hqlQuery);
    }

    public List getSortedResultset(String tableName, String fieldName) {
        return getSortedResultset(tableName, fieldName, null);
    }

    public List<String> getTestEntries() throws Exception {
        final Session session = getSession();
        List<String> result = new ArrayList<>();

        try {
            Metamodel metamodel = session.getSessionFactory().getMetamodel();
            EntityType entityType = metamodel.entity(TestEntity.class);
            String entityName = entityType.getName();
            Query query = session.createQuery("from " + entityName);

            for (Object o : query.list()) {
                System.out.println("  " + o);
//                result = o.toString();
//                result.add((int) o.getField );
            }

        } finally {
            session.close();
        }
        return result;
    }


    public void registerOnPublisher() {
        Publisher.getInstance().registerNewListener(this, Facade.EVENT_GROUP_DB);
    }
    public String[] listenerInterests() {
        return new String[] {
                Facade.CMD_DB_FLUSH,
                Facade.CMD_DB_SHUTDOWN
        };
    }
    public void listenerHandler(IPublisherEvent publisherEvent) {
//        System.out.println("DatabaseManager received event " + publisherEvent.getName()
//                + " / " + publisherEvent.getType() + ":\n" + publisherEvent.getBody().toString());
        if (publisherEvent.getType().equals(Facade.EVENT_TYPE_GROUP)) {
            System.out.println("DatabaseManager received group event: " + publisherEvent.getBody().toString());
        }

        switch (publisherEvent.getName()) {
            case Facade.CMD_DB_FLUSH: {
                flushCache();
                System.out.println(publisherEvent.getName() + ": \n" + publisherEvent.getBody().toString());
                break;
            }
            case Facade.CMD_DB_SHUTDOWN: {
                shutdown();
                System.out.println(publisherEvent.getName() + ": \n" + publisherEvent.getBody().toString());
                break;
            }
        }

    }

}
