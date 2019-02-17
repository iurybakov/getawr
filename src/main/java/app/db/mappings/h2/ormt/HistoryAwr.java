package app.db.mappings.h2.ormt;

import javax.persistence.*;

@Entity
@Table(name = "HISTORY_AWR", schema = "ORA_AWR")
public class HistoryAwr {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "tennantId", nullable = false)
    private Long tennantId;

    @Column(name = "snapFrom", nullable = false)
    private Long snapFrom;

    @Column(name = "snapTo", nullable = false)
    private Long snapTo;

    @Column(name = "report", nullable = false)
    private char[] report;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private
    HistoryAwrDate historyAwrDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTennantId() {
        return tennantId;
    }

    public void setTennantId(Long tennantId) {
        this.tennantId = tennantId;
    }

    public Long getSnapFrom() {
        return snapFrom;
    }

    public void setSnapFrom(Long snapFrom) {
        this.snapFrom = snapFrom;
    }

    public Long getSnapTo() {
        return snapTo;
    }

    public void setSnapTo(Long snapTo) {
        this.snapTo = snapTo;
    }

    public char[] getReport() {
        return report;
    }

    public void setReport(char[] report) {
        this.report = report;
    }

    public HistoryAwrDate getHistoryAwrDate() {
        return historyAwrDate;
    }

    public void setHistoryAwrDate(HistoryAwrDate historyAwrDate) {
        this.historyAwrDate = historyAwrDate;
    }
}
