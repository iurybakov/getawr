package app.web.controller;

import app.web.json.Request;
import app.web.json.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GetAwrRestController {

    @Autowired
    private RequestHandler requestHandler;


    @PostMapping(value = "/home", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    Response homeRest(@RequestBody final Request request) {

        switch (request.getType()) {
            case "content":
                return requestHandler.handleHomeGetContentOraMetaRequest(request);
            case "periods":
                return requestHandler.handleGetOraclePeriodRequest(request);
            case "awr":
                return requestHandler.handleGetOracleAwrRequest(request);
        }
        return Response.createErrorResponse(request, "Error, unknown request type '" + request.getType() + "'");
    }


    @PostMapping(value = "/history", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    Response historyRest(@RequestBody final Request request) {

        switch (request.getType()) {
            case "content":
                return requestHandler.handleHistoryGetContent(request);
            case "awr":
                return requestHandler.handleHistoryGetAwr(request);
        }
        return Response.createErrorResponse(request, "Error, unknown request type '" + request.getType() + "'");
    }


    @PostMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    Response editRest(@RequestBody final Request request) {

        switch (request.getType()) {
            case "content":
                return requestHandler.handleGetEditContentOraUrlRequest(request);
            case "insert":
                return requestHandler.handleInsertOraCredentialRequest(request);
            case "update":
                return requestHandler.handleUpdateOraCredentialRequest(request);
            case "delete":
                return requestHandler.handleDeleteContentOraUrlRequest(request);
        }
        return Response.createErrorResponse(request, "Error, unknown request type '" + request.getType() + "'");
    }


    @PostMapping(value = "/edit/admin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    Response adminRest(@RequestBody final Request request) {

        switch (request.getType()) {
            case "content":
                return requestHandler.getUsers(request);
            case "operate":
                return requestHandler.operateUser(request);
            case "insert":
                return requestHandler.insertUser(request);
        }
        return Response.createErrorResponse(request, "Error, unknown request type '" + request.getType() + "'");
    }
}