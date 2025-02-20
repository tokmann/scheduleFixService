package app.model;



public class BadSpace {
    private String victim;
    private ScheduleEntry firstEntry;
    private ScheduleEntry secondEntry;
    private String descpription;

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

    public String getDescpription() {
        return descpription;
    }

    public void setDescpription(String descpription) {
        this.descpription = descpription;
    }
}
