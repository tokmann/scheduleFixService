package app.model;


import jakarta.persistence.*;

@Entity
public class BadSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "first_entry_id")
    private ScheduleEntry firstEntry;

    @ManyToOne
    @JoinColumn(name = "second_entry_id")
    private ScheduleEntry secondEntry;

    private String victim;

    private String descpription;




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
