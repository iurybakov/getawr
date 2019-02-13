package app.db.services.ora;

import app.db.mappings.ora.DbaHistSnapshot;
import app.db.repositories.ora.OracleSnapRepository;
import app.web.json.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class OracleService {

    @PersistenceContext(unitName  = "oracleEntityManager")
    private EntityManager entityManager;

    @Autowired
    private OracleSnapRepository oracleSnapRepository;

    @Transactional(transactionManager = "oracleTransactionManager", readOnly = true)
    public void setInMapOperatingSystemAndVersion(final Map<String, String> credentialOraMeta) {

        final Query platformName = entityManager.createNativeQuery("SELECT PLATFORM_NAME FROM V$DATABASE");
        final Query version = entityManager.createNativeQuery("SELECT VERSION FROM PRODUCT_COMPONENT_VERSION WHERE ROWNUM = 1");

        credentialOraMeta.put("os", platformName.getSingleResult().toString());
        credentialOraMeta.put("version", version.getSingleResult().toString());
    }

    @Transactional(transactionManager = "oracleTransactionManager", readOnly = true)
    public ResponseData getPeriods() {

        ResponseData responseData = new ResponseData();
        List<DbaHistSnapshot> list = oracleSnapRepository.getMaxMinSnapPeriods();
        System.out.println(list.size());
        responseData.setData(list);
        return responseData;
    }

    @Transactional(transactionManager = "oracleTransactionManager", readOnly = true)
    @SuppressWarnings("unchecked")
    public Object getAwr(Map<String, String> data) throws Exception {

        List<DbaHistSnapshot> listSnap = oracleSnapRepository.findAllSnapPeriodsBydbIdAndBetween(Long.parseLong(data.get("dbId")),
                Timestamp.from(Instant.parse(data.get("dateFrom"))),
                Timestamp.from(Instant.parse(data.get("dateTo"))));
        Long toSnap = null;

        if (listSnap.size() == 0)
            throw new Exception("Error, no report items found for the specified period");
        if (listSnap.size() == 1) {
            if (oracleSnapRepository.existsById(listSnap.get(0).getSnapId() + 1))
                toSnap = listSnap.get(0).getSnapId() + 1;
            else if (oracleSnapRepository.existsById(listSnap.get(0).getSnapId() - 1))
                toSnap = listSnap.get(0).getSnapId() - 1;
            else
                throw new Exception("Error, found only one record for BDID '" + listSnap.get(0).getDbId() + "'");
        } else
            toSnap = listSnap.get(1).getSnapId();

        final Query query = entityManager.createNativeQuery("SELECT output FROM TABLE(DBMS_WORKLOAD_REPOSITORY.AWR_REPORT_HTML(?1, ?2, ?3, ?4))");
        query.setParameter(1, listSnap.get(0).getDbId());
        query.setParameter(2, listSnap.get(0).getInstanceNumber());
        query.setParameter(3, listSnap.get(0).getSnapId());
        query.setParameter(4, toSnap);

        return query.getResultStream().collect(Collectors.joining());
    }
}

