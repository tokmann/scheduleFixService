package app.model;
import org.springframework.stereotype.Component;

@Component
public class BadSpace {

    private ScheduleEntry firstEntry;

    private ScheduleEntry secondEntry;

    private String victim;

    private String description;

    public String getVictim() {
        return victim;
    }

    public void setVictim(String victim) {
        this.victim = victim;
    }

    public ScheduleEntry getFirstEntry() {
        return firstEntry;
    }

    public void setFirstEntry(ScheduleEntry firstEntry) {
        this.firstEntry = firstEntry;
    }

    public ScheduleEntry getSecondEntry() {
        return secondEntry;
    }

    public void setSecondEntry(ScheduleEntry secondEntry) {
        this.secondEntry = secondEntry;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descpription) {
        this.description = descpription;
    }
}
