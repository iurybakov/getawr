package app.db.mappings.h2.sec;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
@JsonIgnoreProperties({ "password", "deleted", "tblUserRole" })
@Entity
@Table(name = "TBL_USERS", schema = "SEC_USERS")
public class TblUsers {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @OneToOne(fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private
    TblUserRole tblUserRole;

    @JsonGetter("role")
    public String getRole() {
        return this.getTblUserRole().getRoleName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public TblUserRole getTblUserRole() {
        return tblUserRole;
    }

    public void setTblUserRole(TblUserRole tblUserRole) {
        this.tblUserRole = tblUserRole;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
