package by.fc.bot.repository.blocking;

import by.sf.bot.jooq.tables.pojos.Buttons;
import by.sf.bot.jooq.tables.pojos.Users;
import com.fs.call_models.jooq.tables.pojos.CallRequests;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static by.sf.bot.jooq.tables.Users.USERS;
import static com.fs.call_models.jooq.tables.CallRequests.CALL_REQUESTS;


@Repository
public class CallRequestRepository {

    private final DSLContext dsl;

    public CallRequestRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<String> getClientNotCompletedRequests(){
        List<String> resultList = new ArrayList<>();
        List<CallRequests> callRequestsList = dsl.select(CALL_REQUESTS.asterisk())
                .from(CALL_REQUESTS)
                .where(CALL_REQUESTS.IS_ACTIVE.eq(Boolean.TRUE))
                .fetchInto(CallRequests.class);

        callRequestsList.forEach(it->{
            resultList.add(String.format("""
                    ID заявки: %s.
                    Имя клиента: %s.
                    Номер: %s.
                    Удобное время звонка: %s.
                    Заявка была оставлена: %s в %s
                    """,
                    it.getId(),
                    it.getName(), it.getPhoneNum(), it.getCallTime(),
                    it.getDateCreated().toLocalDate().toString(),
                    it.getDateCreated().toLocalTime().toString()));
                }
        );
        return resultList;
    }

    public List<String> getClientRequestsByPeriod(int daysQuantity){
        List<String> resultList = new ArrayList<>();
        List<CallRequests> callRequestsList = dsl.select(CALL_REQUESTS.asterisk())
                .from(CALL_REQUESTS)
                .where(CALL_REQUESTS.DATE_CREATED.ge(LocalDateTime.now().minusDays(daysQuantity)))
                .fetchInto(CallRequests.class);

        callRequestsList.forEach(it->{
            String issueStatus = it.getIsActive()? "Не обработана": "Обработана";
                    resultList.add(String.format("""
                    ID заявки: %s.
                    Имя клиента: %s.
                    Номер: %s.
                    Удобное время звонка: %s.
                    Заявка была оставлена: %s в %s.
                    Статус заявки: %s.
                    """,
                            it.getId(), it.getName(), it.getPhoneNum(), it.getCallTime(),
                            it.getDateCreated().toLocalDate().toString(),
                            it.getDateCreated().toLocalTime().toString(),
                            issueStatus));
                }
        );
        return resultList;
    }

    public boolean completeRequest(Long id) {
        return dsl.update(CALL_REQUESTS)
                .set(CALL_REQUESTS.IS_ACTIVE, Boolean.FALSE)
                .where(CALL_REQUESTS.ID.eq(id))
                .execute() > 0;
    }
}
