package app.db.mappings.h2.sec;

import javax.persistence.*;

@Entity
@Table(name = "TBL_USER_ROLE", schema = "SEC_USERS")
@SequenceGenerator(name = "IDS", sequenceName = "TBL_USERS_SEQUENCE", allocationSize = 1, schema = "SEC_USERS")
public class TblUserRole {

    @Id
    @GeneratedValue(generator = "IDS", strategy = GenerationType.SEQUENCE)
    @Column(name = "userId", nullable = false)
    private Long userId;
    @Column(name = "roleName", nullable = false)
    private String roleName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
