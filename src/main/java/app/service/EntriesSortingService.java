package app.service;

import app.model.ScheduleEntry;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;

@Service
public class EntriesSortingService {

    public List<List<ScheduleEntry>> sortEntries(List<ScheduleEntry> entries) {


        Map<LocalDate, List<ScheduleEntry>> entriesByDay = entries.stream()
                .collect(Collectors.groupingBy(entry -> entry.getStartTime().toLocalDate()));


        entriesByDay.forEach((day, dailyEntries) -> {
            dailyEntries.sort(Comparator.comparing(ScheduleEntry::getStartTime));
        });


        List<LocalDate> sortedDays = new ArrayList<>(entriesByDay.keySet());
        Collections.sort(sortedDays);


        List<List<ScheduleEntry>> sortedEntriesByDay = new ArrayList<>();
        for (LocalDate day : sortedDays) {
            sortedEntriesByDay.add(entriesByDay.get(day));
        }
        return sortedEntriesByDay;
    }
}
