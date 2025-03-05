package app.model;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class ScheduleConstant {

    public static final LocalTime LATE_START_THRESHOLD =
            LocalTime.of(12, 10);

    public static final LocalDate TODAY_DATE = LocalDate.now();


}
