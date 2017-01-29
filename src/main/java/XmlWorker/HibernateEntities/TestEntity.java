package XmlWorker.HibernateEntities;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by DeFF on 27.01.2017.
 */
@Entity
@Table(name = "test")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TestEntity {
    private int id;
    private Integer field;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "FIELD", nullable = true)
    public Integer getField() {
        return field;
    }

    public void setField(Integer field) {
        this.field = field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestEntity that = (TestEntity) o;

        if (id != that.id) return false;
        if (field != null ? !field.equals(that.field) : that.field != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (field != null ? field.hashCode() : 0);
        return result;
    }
}
