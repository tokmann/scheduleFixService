package app.model;


import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class BadSpace {

    private ScheduleEntry firstEntry;

    private ScheduleEntry secondEntry;

    private String victim;

    private String description;

}
