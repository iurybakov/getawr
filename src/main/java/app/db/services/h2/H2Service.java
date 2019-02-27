package app.db.services.h2;

import app.db.mappings.h2.ormt.OraMeta;
import app.db.mappings.h2.ormt.OraUrl;
import app.db.repositories.h2.OraMetaRepository;
import app.db.repositories.h2.OraUrlRepository;
import app.db.services.apiservice.ContentOraInfoJpaService;
import app.web.json.ResponseData;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class H2Service implements ContentOraInfoJpaService {

    private static final Logger LOG = LoggerFactory.getLogger(H2Service.class);

    @Autowired
    private OraUrlRepository oraUrlRepository;
    @Autowired
    private OraMetaRepository oraMetaRepository;


    /*
    #
    #  Get content by filter and pageable for table on '/home' endpoint
    #
    */
    @Transactional(transactionManager = "h2TransactionManager", readOnly = true)
    public ResponseData getOraMeta(final Map<String, String> filter, final Pageable pageNum) {

        LOG.info("getOraMetaResponseData");
        final ResponseData responseData = new ResponseData();
        final Map<String, String> properties = new HashMap<>();
        Page<OraMeta> page;

        if (filter.size() == 0)
            page = oraMetaRepository.findByDeleted(false, pageNum);

        else
            page = oraMetaRepository.findAll((Specification<OraMeta>) (root, criteriaQuery, criteriaBuilder) -> {

                final Predicate[] predicates = new Predicate[filter.size() + 1];
                predicates[0] = criteriaBuilder.equal(root.get("deleted"), false);
                int indexArrayPredicates = 1;
                for (Map.Entry<String, String> entry : filter.entrySet())
                    predicates[indexArrayPredicates++] = criteriaBuilder.like(root.get(entry.getKey()), "%" + entry.getValue() + "%");
                return criteriaBuilder.and(predicates);

            }, pageNum);

        properties.put("pageNumber", String.valueOf(pageNum.getPageNumber()));

        if (page == null || page.isEmpty()) {
            properties.put("allRows", "0");
            responseData.setProperties(properties);
            return responseData;
        }

        properties.put("allRows", String.valueOf(page.getTotalElements()));
        responseData.setProperties(properties);
        responseData.setData(page.getContent());
        return responseData;
    }


    /*
    #
    #  Create new datasource object by tennantId for cache class, using if was click on row table
    #  and cache doesn't contain datasource with this key (tennantid)
    #
    */
    public DataSource createDataSource(final Long id) throws Exception {

        LOG.info("createDataSourceByIdUrl");
        final OraUrl url = oraUrlRepository.findByIdAndDeleted(id, false);

        if (url == null)
            throw new Exception("Error, wrong tennantId");

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@" + url.getHost() + ":" + url.getPort() + ":" + url.getSid());
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setUsername(url.getLogin());
        dataSource.setPassword(url.getPass());

        return dataSource;
    }


    /*
    #
    #  Get content by filter and pageable for table on '/edit' endpoint
    #
    */
    @Transactional(transactionManager = "h2TransactionManager", readOnly = true)
    public ResponseData getOraUrl(final Map<String, String> filter, final Pageable pageNum) {

        LOG.info("getOraUrlResponseData");
        final ResponseData responseData = new ResponseData();
        final Map<String, String> properties = new HashMap<>();
        Page<OraUrl> page;

        if (filter.size() == 0)
            page = oraUrlRepository.findByDeleted(false, pageNum);
        else
            page = oraUrlRepository.findAll((Specification<OraUrl>) (root, criteriaQuery, criteriaBuilder) -> {

                final Predicate[] predicates = new Predicate[filter.size() + 1];
                predicates[0] = criteriaBuilder.equal(root.get("deleted"), false);

                if (filter.get("name") != null) {
                    final Join<OraUrl, OraMeta> join = root.join("oraMeta");
                    predicates[filter.size()] = criteriaBuilder.like(join.get("name"), "%" + filter.remove("name") + "%");
                }

                int indexArrayPredicates = 1;
                for (Map.Entry<String, String> entry : filter.entrySet())
                    predicates[indexArrayPredicates++] = criteriaBuilder.like(root.get(entry.getKey()), "%" + entry.getValue() + "%");
                return criteriaBuilder.and(predicates);

            }, pageNum);

        properties.put("pageNumber", String.valueOf(pageNum.getPageNumber()));

        if (page == null || page.isEmpty()) {
            properties.put("allRows", "0");
            responseData.setProperties(properties);
            return responseData;
        }

        properties.put("allRows", String.valueOf(page.getTotalElements()));
        responseData.setProperties(properties);
        responseData.setData(page.getContent());
        return responseData;
    }


    /*
    #
    #  Add new url connection for Oracle DB
    #
    */
    @Transactional(transactionManager = "h2TransactionManager")
    public String insertOraCredential(final Map<String, String> credential) {

        LOG.info("insertNewOraCredential");
        final OraUrl oraUrl = new OraUrl();
        final OraMeta oraMeta = new OraMeta();
        final String responseMessage = "row inserted successfully";

        oraMeta.setName(credential.remove("name"));

        oraUrl.setHost(credential.get("host"));
        oraUrl.setPort(credential.get("port"));
        oraUrl.setSid(credential.get("sid"));
        oraUrl.setLogin(credential.get("login"));
        oraUrl.setPass(credential.get("pass"));

        oraMetaRepository.save(oraMeta);
        oraUrl.setId(oraMeta.getId());

        oraUrlRepository.save(oraUrl);

        credential.clear();
        credential.put("id", String.valueOf(oraMeta.getId()));

        return responseMessage;
    }


    /*
    #
    #  Edit Oracle url connection
    #
    */
    @Transactional(transactionManager = "h2TransactionManager")
    public String updateOraCredential(final Map<String, String> credential) throws Exception {

        LOG.info("updateOraCredential");
        final Optional<OraUrl> optionalOraUrl = oraUrlRepository.findById(Long.parseLong(credential.get("id")));

        if (!optionalOraUrl.isPresent())
            throw new Exception("Unable upade ora credential, not found row");

        final OraUrl url = optionalOraUrl.get();
        url.getOraMeta().setName(credential.get("name"));
        if (credential.get("pass").matches("=*"))
            credential.put("pass", url.getPass());

        url.setHost(credential.get("host"));
        url.setPort(credential.get("port"));
        url.setSid(credential.get("sid"));
        url.setLogin(credential.get("login"));
        url.setPass(credential.get("pass"));

        oraUrlRepository.save(url);

        return "row updated successfully";
    }


    /*
    #
    #  After update or insert save Oracle host OS and Oracle DB version
    #
    */
    public void updateOraMeta(final Map<String, String> credential) throws Exception {

        LOG.info("updateOraMeta");
        final Optional<OraMeta> optionalOraMeta = oraMetaRepository.findById(Long.parseLong(credential.get("id")));

        if (!optionalOraMeta.isPresent())
            throw new Exception("Error update Oracle OS and Version");

        final OraMeta meta = optionalOraMeta.get();

        meta.setOs(credential.get("os"));
        meta.setVersion(credential.get("version"));

        oraMetaRepository.save(meta);
    }


    /*
    #
    #  Set flag deleted true and change name, but row is save
    #
    */
    @Transactional(transactionManager = "h2TransactionManager")
    public String deleteOraCredentials(final List<Long> credentialId) {

        LOG.info("deleteOraCredentials");
        Iterable<OraUrl> oraUrlIterable = oraUrlRepository.findAllById(credentialId);

        oraUrlIterable.forEach(url -> {
            url.setDeleted(true);
            url.getOraMeta().setDeleted(true);
            url.getOraMeta().setName("~" + url.getOraMeta().getName() + url.getId()); //TODO forbid name with '~' in first
        });

        oraUrlRepository.saveAll(oraUrlIterable);

        return "attempt to delete " + credentialId.size() + " rows passed successfully";
    }


    /*
    #
    # Check unique name for table on '/edit' endpoint
    #
    */
    @Transactional(transactionManager = "h2TransactionManager", readOnly = true)
    public boolean checkNameExists(final String name) {

        LOG.info("checkNameExists");
        final Long count = oraMetaRepository.countByNameAndDeleted(name, false);
        return count != null && count != 0;
    }
}



