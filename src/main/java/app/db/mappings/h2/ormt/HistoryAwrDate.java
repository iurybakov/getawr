package app.db.mappings.h2.ormt;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "HISTORY_AWR_DATE", schema = "ORA_AWR")
@SequenceGenerator(name = "IDH", sequenceName = "ORA_AWR.HISTORY_AWR_SEQUENCE", allocationSize = 1, schema = "ORA_AWR")
public class HistoryAwrDate {

    @Id
    @GeneratedValue(generator = "IDH", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "NAMEORADB", nullable = false)
    private String nameOraDb;
    @Column(name = "BEGIN_INTERVAL_TIME", nullable = false)
    private Timestamp beginIntervalTime;
    @Column(name = "END_INTERVAL_TIME", nullable = false)
    private Timestamp endIntervalTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameOraDb() {
        return nameOraDb;
    }

    public void setNameOraDb(String nameOraDb) {
        this.nameOraDb = nameOraDb;
    }

    public Timestamp getBeginIntervalTime() {
        return beginIntervalTime;
    }

    public void setBeginIntervalTime(Timestamp beginIntervalTime) {
        this.beginIntervalTime = beginIntervalTime;
    }

    public Timestamp getEndIntervalTime() {
        return endIntervalTime;
    }

    public void setEndIntervalTime(Timestamp endIntervalTime) {
        this.endIntervalTime = endIntervalTime;
    }
}
