package app.db.services.h2;

import app.db.mappings.h2.OraMeta;
import app.db.mappings.h2.OraUrl;
import app.db.repositories.h2.OraMetaRepository;
import app.db.repositories.h2.OraUrlRepository;
import app.web.json.ResponseData;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.*;


@Service
public class H2Service {

    @Autowired
    private OraUrlRepository oraUrlRepository;
    @Autowired
    private OraMetaRepository oraMetaRepository;

    @Transactional(transactionManager = "h2TransactionManager", readOnly = true)
    public ResponseData getOraMetaResponseData(final Map<String, String> filter, final Pageable pageNum)  {

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
        properties.put("allRows", String.valueOf(page.getTotalElements()));
        responseData.setProperties(properties);
        responseData.setData(page.getContent());
        return responseData;
    }

    public BasicDataSource createDataSourceByIdUrl(final Long id) throws Exception {

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
    @Transactional(transactionManager = "h2TransactionManager", readOnly = true)
    public ResponseData getOraUrlResponseData(final Map<String, String> filter, final Pageable pageNum) {

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
        properties.put("allRows", String.valueOf(page.getTotalElements()));
        responseData.setProperties(properties);
        responseData.setData(page.getContent());
        return responseData;
    }
    @Transactional(transactionManager = "h2TransactionManager")
    public String insertNewOraCredential(final Map<String, String> credential) {

        final OraUrl url = new OraUrl();
        final OraMeta meta = new OraMeta();

        String responseMessage = "row inserted successfully";

        meta.setName(credential.remove("name"));
        url.initUrlFields(credential);

        oraMetaRepository.save(meta);
        url.setId(meta.getId());

        oraUrlRepository.save(url);

        credential.clear();
        credential.put("id", String.valueOf(meta.getId()));

        return responseMessage;
    }
    @Transactional(transactionManager = "h2TransactionManager")
    public String updateOraCredential(Map<String, String> credential) {

        OraUrl oraUrl = oraUrlRepository.findById(Long.parseLong(credential.remove("id"))).get();
        oraUrl.getOraMeta().setName(credential.remove("name"));
        if (credential.get("pass").matches("=*"))
            credential.put("pass", oraUrl.getPass());
        oraUrl.initUrlFields(credential);
        oraUrlRepository.save(oraUrl);

        return "row updated successfully";
    }

    public void updateOraMeta(Map<String, String> credential) throws Exception {

        final OraMeta meta = oraMetaRepository.findById(Long.parseLong(credential.get("id"))).get();

        meta.setOs(credential.get("os"));
        meta.setVersion(credential.get("version"));

        oraMetaRepository.save(meta);
    }
    @Transactional(transactionManager = "h2TransactionManager")
    public String deleteOraCredentials(final List<Long> credentialId) {

        Iterable<OraUrl> oraUrlIterable = oraUrlRepository.findAllById(credentialId);

        oraUrlIterable.forEach(url -> {
            url.setDeleted(true);
            url.getOraMeta().setDeleted(true);
            url.getOraMeta().setName("~" + url.getOraMeta().getName() + url.getId()); //TODO запретить добавлять записи, где имя с тильдой в начеле
        });

        oraUrlRepository.saveAll(oraUrlIterable);

        return "attempt to delete " + credentialId.size() + " rows passed successfully";
    }
    @Transactional(transactionManager = "h2TransactionManager", readOnly = true)
    public boolean checkNameExists(final String name) {

        final Long count = oraMetaRepository.countByNameAndDeleted(name, false);
        return count != null && count != 0;
    }
}



