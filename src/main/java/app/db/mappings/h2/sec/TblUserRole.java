package app.db.mappings.h2.sec;

import javax.persistence.*;

@Entity
@Table(name = "TBL_USER_ROLE", schema = "SEC_USERS")
@SequenceGenerator(name = "IDR", sequenceName = "TBL_USERS_ROLE_SEQUENCE", allocationSize = 1, schema = "SEC_USERS")
public class TblUserRole {

    @Id
    @GeneratedValue(generator = "IDR", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "rolename", nullable = false, unique = true)
    private String rolename;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String roleName) {
        this.rolename = roleName;
    }
}
