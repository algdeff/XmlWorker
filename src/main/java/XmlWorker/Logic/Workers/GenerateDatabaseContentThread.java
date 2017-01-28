package XmlWorker.Logic.Workers;

import XmlWorker.HibernateEntities.TestEntity;
import XmlWorker.Logic.db.DatabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;

public class GenerateDatabaseContentThread implements Callable {

    private List<String> _records;

    public GenerateDatabaseContentThread(List<String> records) {
        _records = new ArrayList<>();
        _records.addAll(records); //_records = records; //invalid
    }

    @Override
    public List<String> call() {
        //System.out.println("inner Name:  total task size: " + _records.size() + "/");

        for (String record : _records) {
            TestEntity databaseRecord = new TestEntity();
            databaseRecord.setField(Integer.parseInt(record));
            DatabaseManager.getInstance().saveEntity(databaseRecord);
        }

        return _records;
    }
}
