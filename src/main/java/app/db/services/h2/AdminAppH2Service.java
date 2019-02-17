package app.db.services.h2;

import app.db.mappings.h2.sec.TblUserRole;
import app.db.mappings.h2.sec.TblUsers;
import app.db.repositories.h2.TblUserRoleRepository;
import app.db.repositories.h2.TblUsersRepository;
import app.web.json.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminAppH2Service {

    @Autowired
    private TblUserRoleRepository tblUserRoleRepository;
    @Autowired
    private TblUsersRepository tblUsersRepository;


    @Transactional(transactionManager = "h2TransactionManager")
    private void insert(final Map<String, String> credential) {

        final TblUsers tblUsers = new TblUsers();
        final TblUserRole tblUserRole = new TblUserRole();

        tblUserRole.setRoleName(credential.get("role"));
        tblUserRoleRepository.save(tblUserRole);

        tblUsers.setUsername(credential.get("user"));
        tblUsers.setPassword(credential.get("pass"));

        tblUsers.setId(tblUserRole.getUserId());

        tblUsersRepository.save(tblUsers);
    }

    @Transactional(transactionManager = "h2TransactionManager")
    private boolean operate(final Long id, final String action) {

        final Optional<TblUsers> users = tblUsersRepository.findById(id);

        if (!users.isPresent())
            return false;

        final TblUsers tbUs = users.get();

        switch (action) {
            case "delete":
                tbUs.setDeleted(true);
                tbUs.setUsername("~" + tbUs.getUsername() + tbUs.getId());
                break;
            case "disable":
                tbUs.setEnabled(false);
                break;
            case "enable":
                tbUs.setEnabled(true);
                break;
            default:
                return false;
        }

        tblUsersRepository.save(tbUs);

        return true;
    }

    @Transactional(transactionManager = "h2TransactionManager", readOnly = true)
    public ResponseData getListUsers(final Pageable pageNum) {

        final ResponseData responseData = new ResponseData();
        final Page<TblUsers> page = tblUsersRepository.findByDeleted(false, pageNum);
        final Map<String, String> properties = new HashMap<>();

        if(page == null || page.isEmpty())
            return responseData.setData(new ArrayList<>());

        properties.put("pageNumber", String.valueOf(pageNum.getPageNumber()));
        properties.put("allRows", String.valueOf(page.getTotalElements()));
        responseData.setProperties(properties);

        return responseData.setData(page.getContent());
    }


    public ResponseData insertUser(final Map<String, String> credential, final Pageable pageNum) {

        insert(credential);

        return getListUsers(pageNum);
    }


    public ResponseData operateUser(final Long id, final String action, final Pageable pageNum) {

        if (!operate(id, action))
            return null;

        return getListUsers(pageNum);
    }
}
