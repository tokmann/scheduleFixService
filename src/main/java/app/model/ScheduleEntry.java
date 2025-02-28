package app.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@Data
public class ScheduleEntry {
    private String name;

    private String readableTimeStart;

    private String readableTimeEnd;

    private LocalDateTime startTime;

    private LocalDateTime  endTime;

    private String classroom;

    private String teacher;
}
