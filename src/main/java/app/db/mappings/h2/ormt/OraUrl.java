package app.db.mappings.h2.ormt;

import app.db.mappings.h2.ormt.OraMeta;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Map;

@JsonIgnoreProperties({"pass", "date", "deleted", "oraMeta"})
@Entity
@Table(name = "ORA_URL", schema = "ORA_AWR")
public class OraUrl {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "host", nullable = false)
    private String host;
    @Column(name = "port", nullable = false)
    private String port;
    @Column(name = "sid", nullable = false)
    private String sid;
    @Column(name = "login", nullable = false)
    private String login;
    @Column(name = "pass", nullable = false)
    private String pass;
    @CreationTimestamp
    @Column(name = "date", nullable = false)
    private Timestamp date;
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private
    OraMeta oraMeta;

    @JsonGetter("name")
    public String getNameFromOraMeta() {
        return this.getOraMeta().getName();
    }

    public boolean initUrlFields(final Map<String, String> credentials) {
        try {
            for (Map.Entry<String, String> entry : credentials.entrySet())
                this.getClass().getDeclaredField(entry.getKey()).set(this, entry.getValue());
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            this.setHost(null);
            this.setPort(null);
            this.setSid(null);
            this.setLogin(null);
            this.setPass(null);
            return false;
        }
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public OraMeta getOraMeta() {
        return oraMeta;
    }

    public void setOraMeta(OraMeta oraMeta) {
        this.oraMeta = oraMeta;
    }
}