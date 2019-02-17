package app.db.services.h2;

import app.db.mappings.h2.ormt.HistoryAwr;
import app.db.mappings.h2.ormt.HistoryAwrDate;
import app.db.repositories.h2.HistoryAwrDateRepository;
import app.db.repositories.h2.HistoryAwrRepository;
import app.web.json.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class HistoryAwrH2Service {

    private final int MAX_HISTORY_ROWS = 30;

    @Autowired
    private HistoryAwrRepository historyAwrRepository;
    @Autowired
    private HistoryAwrDateRepository historyAwrDateRepository;


    public char[] getSavedAwr(final Map<String, Long> credentials) {

        final List<HistoryAwr> historyAwrList = historyAwrRepository.findAllByTennantIdAndSnapFromAndSnapTo(credentials.get("tennantId"),
                credentials.get("snapFrom"),
                credentials.get("snapTo"));

        if (historyAwrList == null || historyAwrList.size() == 0)
            return null;

        return historyAwrList.get(0).getReport();
    }


    public char[] getSavedAwr(final Long credentialId) {

        final Optional<HistoryAwr> historyAwr = historyAwrRepository.findById(credentialId);

        if(!historyAwr.isPresent())
            return null;

        return historyAwr.get().getReport();
    }


    public ResponseData getHistoryList() {

        final ArrayList<HistoryAwrDate> list = new ArrayList<>();

        historyAwrDateRepository.findAll().forEach(list::add);

        return new ResponseData().setData(list);
    }


    public void saveReport(final String dbName, final Map<String, Long> credentials, final char[] awrReport) throws Exception {

        if (historyAwrRepository.countByTennantIdAndSnapFromAndSnapTo(credentials.get("tennantId"),
                credentials.get("fromSnap"),
                credentials.get("toSnap")) > 0)
            return;

        final HistoryAwr historyAwr = new HistoryAwr();
        final HistoryAwrDate historyAwrDate = new HistoryAwrDate();

        if (historyAwrDateRepository.count() > MAX_HISTORY_ROWS)
            if (historyAwrDateRepository.clearHistory() == 0)
                throw new Exception("Unable clean history awr");

        historyAwrDate.setBeginIntervalTime(new Timestamp(credentials.get("fromDate")));
        historyAwrDate.setEndIntervalTime(new Timestamp(credentials.get("toDate")));
        historyAwrDate.setNameOraDb(dbName);

        historyAwrDateRepository.save(historyAwrDate);

        historyAwr.setId(historyAwrDate.getId());
        historyAwr.setTennantId(credentials.get("tennantId"));
        historyAwr.setSnapFrom(credentials.get("fromSnap"));
        historyAwr.setSnapTo(credentials.get("toSnap"));
        historyAwr.setReport(awrReport);

        historyAwrRepository.save(historyAwr);
    }
}
