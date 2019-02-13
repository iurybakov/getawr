package app.db.mappings.ora;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@JsonIgnoreProperties({"DBID", "INSTANCE_NUMBER"})
@Entity
@Table(name = "dba_hist_snapshot")
public class DbaHistSnapshot {

    @Id
    @Column(name = "SNAP_ID")
    private Long snapId;
    @Column(name = "DBID")
    private Long dbId;
    @Column(name = "INSTANCE_NUMBER")
    private Long instanceNumber;
    @Column(name = "BEGIN_INTERVAL_TIME")
    private Timestamp beginIntervalTime;
    @Column(name = "END_INTERVAL_TIME")
    private Timestamp endIntervalTime;


    public Long getSnapId() {
        return snapId;
    }

    public void setSnapId(Long snapId) {
        this.snapId = snapId;
    }

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public Long getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(Long instanceNumber) {
        this.instanceNumber = instanceNumber;
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
