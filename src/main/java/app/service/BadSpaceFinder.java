package app.service;

import app.model.BadSpace;
import app.model.ScheduleConstant;
import app.model.ScheduleEntry;
import app.utils.GroupParse;
import app.utils.TeacherParse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class BadSpaceFinder {

    @Autowired
    private ICalService iCalService;

    @Autowired
    private EntriesSortingService entriesSortingService;

    @Autowired
    private ICalParser iCalParser;

    @Autowired
    private ScheduleConstant scheduleConstant;

    @Autowired
    private TeacherParse teacherParse;

    @Autowired
    private GroupParse groupParse;

    private static final Logger logger = LoggerFactory.getLogger(BadSpaceFinder.class);

    public List<BadSpace> findBadSpaces(String criteria) throws Exception {
        List<BadSpace> badSpaces = new ArrayList<>();
        try {
            String iCalLink = iCalService.getICalLink(criteria);
            String iCalContent = iCalService.getICalContent(iCalLink);
            List<ScheduleEntry> entries = iCalParser.parseICalContent(iCalContent);
            badSpaces = getBadSpaces(entries, criteria);
        } catch (Exception e) {
            logger.error("Ошибка при поиске badSpaces для критерия {}: {}", criteria, e.getMessage());
        }

        return badSpaces;
    }

    public List<BadSpace> findAllBadSpaces() throws Exception {
        List<BadSpace> badSpaces = new ArrayList<>();

        for (String group : groupParse.groups) {
            if (group.length() == 10) {
                try {
                    String iCalLink = iCalService.getICalLink(group);
                    String iCalContent = iCalService.getICalContent(iCalLink);
                    List<ScheduleEntry> entries = iCalParser.parseICalContent(iCalContent);
                    badSpaces.addAll(getBadSpaces(entries, group));
                } catch (Exception e) {
                    logger.error("Ошибка при обработке группы {}: {}", group, e.getMessage());
                }
            }
        }

        for (String teacher : teacherParse.teachersList) {
            try {
                String iCalLink = iCalService.getICalLink(teacher);
                String iCalContent = iCalService.getICalContent(iCalLink);
                List<ScheduleEntry> entries = iCalParser.parseICalContent(iCalContent);
                badSpaces.addAll(getBadSpaces(entries, teacher));
            } catch (Exception e) {
                logger.error("Ошибка при обработке преподавателя {}: {}", teacher, e.getMessage());
            }
        }

        return badSpaces;
    }

    private static BadSpace createBadSpace(ScheduleEntry entry1, ScheduleEntry entry2,
                                           String victim, String description) {
        BadSpace badSpace = new BadSpace();
        badSpace.setFirstEntry(entry1);
        badSpace.setSecondEntry(entry2);
        badSpace.setVictim(victim);
        badSpace.setDescription(description);

        return badSpace;
    }

    public List<BadSpace> getBadSpaces(List<ScheduleEntry> unsortedEntries, String victim) {
        List<BadSpace> badSpaces = new ArrayList<>();

        List<List<ScheduleEntry>> entriesDays = entriesSortingService.sortEntries(unsortedEntries);

        logger.info("Поиск badSpaces...");

        for (List<ScheduleEntry> entryAtThisDay : entriesDays) {
            if (entryAtThisDay.getFirst().getStartTime().toLocalDate().isAfter(scheduleConstant.TODAY_DATE)) {

                if (entryAtThisDay.getFirst().getStartTime().toLocalTime().
                        isAfter(scheduleConstant.LATE_START_THRESHOLD)) {
                    String description = "Позднее начало пар в этот день: " +
                            entryAtThisDay.getFirst().getStartTime().toLocalTime();
                    BadSpace badSpace = createBadSpace(entryAtThisDay.getFirst(), null,
                            victim, description);
                    badSpaces.add(badSpace);
                }


                if (entryAtThisDay.size() == 1) {
                    String description = "Одна пара в этот день";
                    BadSpace badSpace = createBadSpace(entryAtThisDay.getFirst(), null, victim, description);
                    badSpaces.add(badSpace);
                }


                for (int i = 0; i < entryAtThisDay.size() - 1; i++) {

                    ScheduleEntry current = entryAtThisDay.get(i);
                    ScheduleEntry next = entryAtThisDay.get(i + 1);

                    Duration gap = Duration.between(current.getEndTime(), next.getStartTime());
                    long gapTime = gap.toMinutes();

                    String currentName = current.getName();
                    String nextName = next.getName();

                    String currentCampus = null;
                    String nextCampus = null;
                    if (current.getClassroom() != null) {
                        currentCampus = current.getClassroom().split(" ")[1];

                    }
                    if (next.getClassroom() != null) {
                        nextCampus = next.getClassroom().split(" ")[1];
                    }


                    if (currentName.contains("Физическая культура и спорт") && gapTime < 30) {
                        String description = "Маленький перерыв после ФИЗО";
                        BadSpace badSpace = createBadSpace(current, next, victim, description);
                        badSpaces.add(badSpace);
                    }
                    if (gapTime > 100) {
                        String description = "Длинное окно: " + gapTime + " минут";
                        BadSpace badSpace = createBadSpace(current, next, victim, description);
                        badSpaces.add(badSpace);
                    }
                    if (currentCampus != null && nextCampus != null && !nextCampus.equals("СДО")
                            && !currentCampus.equals("СДО")) {
                        if (!currentCampus.equals(nextCampus)) {
                            String description = "Переход между корпусами";
                            BadSpace badSpace = createBadSpace(current, next, victim, description);
                            badSpaces.add(badSpace);
                        }
                    }
                    if (nextName.contains("Физическая культура и спорт") && gapTime < 30) {
                        String description = "Маленький перерыв после физо";
                        BadSpace badSpace = createBadSpace(current, next, victim, description);
                        badSpaces.add(badSpace);
                    }
                }
            }
        }
        logger.info("Поиск badSpaces завершен");
        return badSpaces;
    }

}
