package app.db.mappings.h2.ormt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@JsonIgnoreProperties({"deleted"})
@Entity
@Table(name = "ORA_META", schema = "ORA_AWR")
@SequenceGenerator(name = "IDO", sequenceName = "ORA_AWR.ORA_ID_SEQUENCE", allocationSize = 1, schema = "ORA_AWR")
public class OraMeta {

    @Id
    @GeneratedValue(generator = "IDO", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "name", nullable = false, unique = true) // TODO add name + _host rule of naming in ui
    private String name;
    @Column(name = "version", nullable = false)
    private String version = "unknown";
    @Column(name = "os", nullable = false)
    private String os = "unknown";
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getOs() {
        return os;
    }

    public void setOs(final String os) {
        this.os = os;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(final Boolean deleted) {
        this.deleted = deleted;
    }

}
