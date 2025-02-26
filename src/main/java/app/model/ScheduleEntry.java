package app.model;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class ScheduleEntry {
    private String name;

    private String readableTimeStart;

    private String readableTimeEnd;

    private LocalDateTime startTime;

    private LocalDateTime  endTime;

    private String classroom;

    private String teacher;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReadableTimeStart() {
        return readableTimeStart;
    }

    public void setReadableTimeStart(String readableTimeStart) {
        this.readableTimeStart = readableTimeStart;
    }

    public String getReadableTimeEnd() {
        return readableTimeEnd;
    }

    public void setReadableTimeEnd(String readableTimeEnd) {
        this.readableTimeEnd = readableTimeEnd;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return "ScheduleEntry{" +
                "name='" + name + '\'' +
                ", readableTimeStart='" + readableTimeStart + '\'' +
                ", readableTimeEnd='" + readableTimeEnd + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", classroom='" + classroom + '\'' +
                ", teacher='" + teacher + '\'' +
                '}';
    }
}
