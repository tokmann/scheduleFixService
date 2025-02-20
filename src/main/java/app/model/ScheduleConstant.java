package app.model;

import org.springframework.stereotype.Component;
import java.time.LocalTime;

@Component
public class ScheduleConstant {

    public static final LocalTime LATE_START_THRESHOLD =
            LocalTime.of(12, 10);

}
