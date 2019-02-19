package app.db.services.ora;

import app.db.mappings.ora.DbaHistSnapshot;
import app.db.repositories.ora.OracleSnapRepository;
import app.db.services.apiservice.OracleJpaService;
import app.web.json.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class OracleService implements OracleJpaService {

    private static final Logger LOG = LoggerFactory.getLogger(OracleService.class);

    @PersistenceContext(unitName  = "oracleEntityManager")
    private EntityManager entityManager;

    @Autowired
    private OracleSnapRepository oracleSnapRepository;

    @Transactional(transactionManager = "oracleTransactionManager", readOnly = true)
    public void setInMapOperatingSystemAndVersion(final Map<String, String> credentialOraMeta) {

        LOG.info("setInMapOperatingSystemAndVersion");
        final Query platformName = entityManager.createNativeQuery("SELECT PLATFORM_NAME FROM V$DATABASE");
        final Query version = entityManager.createNativeQuery("SELECT VERSION FROM PRODUCT_COMPONENT_VERSION WHERE ROWNUM = 1");

        credentialOraMeta.put("os", platformName.getSingleResult().toString());
        credentialOraMeta.put("version", version.getSingleResult().toString());
    }

    @Transactional(transactionManager = "oracleTransactionManager", readOnly = true)
    public ResponseData getPeriods() throws Exception {

        LOG.info("getPeriods");
        ResponseData responseData = new ResponseData();
        List<DbaHistSnapshot> list = oracleSnapRepository.getMaxMinSnapPeriods();
        if (list == null || list.size() == 0)
            throw new Exception("Error, empty list snaps");
        responseData.setData(list);
        return responseData;
    }


    @Transactional(transactionManager = "oracleTransactionManager", readOnly = true)
    @SuppressWarnings("unchecked")
    public Map<String, Long> getSnapsId(Map<String, String> data) throws Exception {

        LOG.info("getSnapsId");
        final Map<String, Long> snapsMap = new HashMap<>();
        DbaHistSnapshot dbaHistSnapshot;

        List<DbaHistSnapshot> listSnap = oracleSnapRepository.findAllSnapPeriodsBydbIdAndBetween(Long.parseLong(data.get("dbId")),
                Timestamp.from(Instant.parse(data.get("dateFrom"))),
                Timestamp.from(Instant.parse(data.get("dateTo"))));

        if (listSnap == null || listSnap.isEmpty())
            throw new Exception("Error, empty snaps list between specify periods, this is possible if the database was restarted during specify period");

        if (listSnap.size() == 1) {
            if (oracleSnapRepository.existsById(listSnap.get(0).getSnapId() + 1L))
                dbaHistSnapshot = oracleSnapRepository.findById(listSnap.get(0).getSnapId() + 1L).get();
            else if (oracleSnapRepository.existsById(listSnap.get(0).getSnapId() - 1L))
                dbaHistSnapshot = oracleSnapRepository.findById(listSnap.get(0).getSnapId() - 1L).get();
            else
                throw new Exception("Error, found only one record for BDID '" + listSnap.get(0).getDbId() + "'");
        } else
            dbaHistSnapshot = listSnap.get(1);

        if(dbaHistSnapshot.getSnapId() > listSnap.get(0).getSnapId()) {
            snapsMap.put("fromDate", listSnap.get(0).getEndIntervalTime().getTime());
            snapsMap.put("fromSnap", listSnap.get(0).getSnapId());
            snapsMap.put("toDate", dbaHistSnapshot.getEndIntervalTime().getTime());
            snapsMap.put("toSnap", dbaHistSnapshot.getSnapId());
        }
        else {
            snapsMap.put("fromDate", dbaHistSnapshot.getEndIntervalTime().getTime());
            snapsMap.put("fromSnap", dbaHistSnapshot.getSnapId());
            snapsMap.put("toDate", listSnap.get(0).getEndIntervalTime().getTime());
            snapsMap.put("toSnap", listSnap.get(0).getSnapId());
        }
        snapsMap.put("tennantId", Long.parseLong(data.get("tennantId")));
        snapsMap.put("dbId", listSnap.get(0).getDbId());
        snapsMap.put("instanceNumber", listSnap.get(0).getInstanceNumber());

        return snapsMap;
    }


    @Transactional(transactionManager = "oracleTransactionManager", readOnly = true)
    @SuppressWarnings("unchecked")
    public String getAwr(Map<String, Long> data) throws Exception {

        LOG.info("getAwr");
        final Query query = entityManager.createNativeQuery("SELECT output FROM TABLE(DBMS_WORKLOAD_REPOSITORY.AWR_REPORT_HTML(?1, ?2, ?3, ?4))");
        query.setParameter(1, data.get("dbId"));
        query.setParameter(2, data.get("instanceNumber"));
        query.setParameter(3, data.get("fromSnap"));
        query.setParameter(4, data.get("toSnap"));

        return query.getResultStream().collect(Collectors.joining()).toString();
    }
}

