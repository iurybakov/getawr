package app.web.controller;

import app.web.apihandle.AdminApiRequestHandler;
import app.web.apihandle.AwrHistoryApiRequestHandler;
import app.web.apihandle.OraContentApiRequestHandler;
import app.web.apihandle.OracleAwrApiRequestHandler;
import app.web.json.Request;
import app.web.json.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GetAwrRestController {

    private static final Logger LOG = LoggerFactory.getLogger(GetAwrRestController.class);

    @Autowired
    private AdminApiRequestHandler admin;
    @Autowired
    private AwrHistoryApiRequestHandler awrHistory;
    @Autowired
    private OracleAwrApiRequestHandler oracleAwr;
    @Autowired
    private OraContentApiRequestHandler oraContent;


    @PostMapping(value = "/home", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    Response homeRest(@RequestBody final Request request) {

        switch (request.getType()) {
            case "content":
                return oraContent.getContentOraMeta(request);
            case "periods":
                return oracleAwr.getMinMaxPeriod(request);
            case "awr":
                return oracleAwr.getAwr(request);
        }
        return Response.createErrorResponse(request, "Error, unknown request type");
    }


    @PostMapping(value = "/history", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    Response historyRest(@RequestBody final Request request) {

        switch (request.getType()) {
            case "content":
                return awrHistory.getContent(request);
            case "awr":
                return awrHistory.getAwr(request);
        }
        return Response.createErrorResponse(request, "Error, unknown request type");
    }


    @PostMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    Response editRest(@RequestBody final Request request) {

        switch (request.getType()) {
            case "content":
                return oraContent.getContentOraUrl(request);
            case "insert":
                return oraContent.insertOraCredential(request);
            case "update":
                return oraContent.updateOraCredential(request);
            case "delete":
                return oraContent.deleteOraUrl(request);
        }
        return Response.createErrorResponse(request, "Error, unknown request type");
    }


    @PostMapping(value = "/edit/admin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    Response adminRest(@RequestBody final Request request) {

        switch (request.getType()) {
            case "content":
                return admin.getUsers(request);
            case "operate":
                return admin.operateUser(request);
            case "insert":
                return admin.insertUser(request);
        }
        return Response.createErrorResponse(request, "Error, unknown request type");
    }
}