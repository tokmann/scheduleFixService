package app.service;

import app.model.*;
import app.utils.GroupParse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import app.model.ScheduleEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import app.model.ScheduleEntry;

@Service
public class ScheduleService {

    @Autowired
    private HttpClientService httpClientService;

    @Autowired
    private ICalService iCalService;

    @Autowired
    private EntriesSortingService entriesSortingService;

    @Autowired
    private ICalParser iCalParser;

    @Autowired
    private ScheduleConstant scheduleConstant;


    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseURL = "https://schedule-of.mirea.ru/schedule/api/search";


    public String getICalLink(String criteria) throws Exception {

        String url = baseURL + "?limit=15&match=" + criteria;

        String json = httpClientService.getJson(url);

        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode dataNode = rootNode.path("data");
        String iCalLink = "";
        if (dataNode.isArray() && !dataNode.isEmpty()) {
            iCalLink = dataNode.get(0).path("iCalLink").asText();
        } else {
            return null;
        }
        return iCalLink;
    }

    public List<BadSpace> findBadSpaces(String criteria) throws Exception {
        String iCalLink = getICalLink(criteria);
        System.out.println("Получена iCalLink");

        String iCalContent = iCalService.getICalContent(iCalLink);
        System.out.println("Получен iCalContent");

        List<ScheduleEntry> entries = iCalParser.parseICalContent(iCalContent);
        System.out.println("Получен список ScheduleEntry");

        return getBadSpaces(entries, criteria);
    }

    public List<BadSpace> findAllBadSpaces() {
        List<BadSpace> badSpaces = new ArrayList<>();
        List<String> groups = Arrays.asList(GroupParse.groups);
        for (String group : groups) {
            if (group.length() == 10) {
                try {
                    String iCalLink = getICalLink(group);
                    System.out.println("Получена iCalLink");

                    String iCalContent = iCalService.getICalContent(iCalLink);
                    System.out.println("Получен iCalContent");

                    List<ScheduleEntry> entries = iCalParser.parseICalContent(iCalContent);
                    System.out.println("Получен список ScheduleEntry");

                    badSpaces.addAll(getBadSpaces(entries, group));
                    System.out.println("Список badSpaces пополнен");
                } catch (Exception e) {
                    System.err.println("Ошибка при поиске группы " + group + ": " + e.getMessage());
                }
            }
        }
        System.out.println("Поиск badSpaces завершен");
        return badSpaces;
    }

    public List<BadSpace> getBadSpaces(List<ScheduleEntry> unsortedEntries, String victim) {
        List<BadSpace> badSpaces = new ArrayList<>();

        List<List<ScheduleEntry>> entriesDays = entriesSortingService.sortEntries(unsortedEntries);
        System.out.println("Список ScheduleEntries отсортирован");

        List<List<String>> foundBadSpaces = new ArrayList<>();

        System.out.println("Поиск badSpaces...");
        for (List<ScheduleEntry> entryAtThisDay : entriesDays) {
            if (entryAtThisDay.getFirst().getStartTime().toLocalTime().
                    isAfter(scheduleConstant.LATE_START_THRESHOLD)) {
                String description = "Позднее начало пар в этот день: " +
                        entryAtThisDay.getFirst().getStartTime().toLocalTime();
                BadSpace badSpace = createBadSpace(entryAtThisDay.getFirst(), null,
                        victim, description); //Новый badSpace
                if (!checkForExistenceLateStart(badSpace, foundBadSpaces, "Позднее начало пар в этот день")) { //Проверка на уже существование
                    badSpaces.add(badSpace);
                    addNewBadSpace(badSpace, foundBadSpaces);
                }
            }

            if (entryAtThisDay.size() == 1) {
                String description = "Одна пара в этот день";
                BadSpace badSpace = createBadSpace(entryAtThisDay.getFirst(), null, victim, description);
                if (!checkForExistence(badSpace, foundBadSpaces)) {
                    badSpaces.add(badSpace);
                    addNewBadSpace(badSpace, foundBadSpaces);
                }
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
                    BadSpace badSpace = createBadSpace(current, next, victim, description); //Новый badSpace
                    if (!checkForExistence(badSpace, foundBadSpaces)) { //Проверка на уже существование
                        badSpaces.add(badSpace);
                        addNewBadSpace(badSpace, foundBadSpaces);
                    }
                }
                if (gapTime > 100) {
                    String description = "Длинное окно: " + gapTime + " минут";
                    BadSpace badSpace = createBadSpace(current, next, victim, description); //Новый badSpace
                    if (!checkForExistence(badSpace, foundBadSpaces)) { //Проверка на уже существование
                        badSpaces.add(badSpace);
                        addNewBadSpace(badSpace, foundBadSpaces);
                    }
                }
                if (currentCampus != null && nextCampus != null && !nextCampus.equals("СДО")
                        && !currentCampus.equals("СДО")) {
                    if (!currentCampus.equals(nextCampus)) {
                        String description = "Переход между корпусами";
                        BadSpace badSpace = createBadSpace(current, next, victim, description);  //Новый badSpace
                        if (!checkForExistence(badSpace, foundBadSpaces)) { //Проверка на уже существование
                            badSpaces.add(badSpace);
                            addNewBadSpace(badSpace, foundBadSpaces);
                        }
                    }
                }
                if (nextName.contains("Физическая культура и спорт") && gapTime < 30) {
                    String description = "Маленький перерыв после физо";
                    BadSpace badSpace = createBadSpace(current, next, victim, description);
                    if (!checkForExistence(badSpace, foundBadSpaces)) {
                        badSpaces.add(badSpace);
                        addNewBadSpace(badSpace, foundBadSpaces);
                    }
                }
            }
        }
        System.out.println("Поиск badSpaces завершен");
        return badSpaces;
    }

    private static BadSpace createBadSpace(ScheduleEntry entry1, ScheduleEntry entry2,
                                           String victim, String description) {
        BadSpace badSpace = new BadSpace();
        badSpace.setFirstEntry(entry1);
        badSpace.setSecondEntry(entry2);
        badSpace.setVictim(victim);
        badSpace.setDescpription(description);
        return badSpace;
    }

    private static boolean checkForExistence(BadSpace badSpace, List<List<String>> foundBadSpaces) {
        String firstName = "", secondName = "";
        if (badSpace.getFirstEntry() != null) {
            firstName = badSpace.getFirstEntry().getName() == null ? "" : badSpace.getFirstEntry().getName();

        }
        if (badSpace.getSecondEntry() != null) {
            secondName = badSpace.getSecondEntry().getName() == null ? "" : badSpace.getSecondEntry().getName();
        }
        String description = badSpace.getDescpription();
        for (List<String> foundBadSpace : foundBadSpaces) {
            if (firstName.equals(foundBadSpace.get(0)) && secondName.equals(foundBadSpace.get(1)) && description.equals(foundBadSpace.get(2))) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkForExistenceLateStart(BadSpace badSpace, List<List<String>> foundBadSpaces, String n) {
        String firstName = "", secondName = "";
        if (badSpace.getFirstEntry() != null) {
            firstName = badSpace.getFirstEntry().getName() == null ? "" : badSpace.getFirstEntry().getName();

        }
        if (badSpace.getSecondEntry() != null) {
            secondName = badSpace.getSecondEntry().getName() == null ? "" : badSpace.getSecondEntry().getName();
        }
        String description = n;
        for (List<String> foundBadSpace : foundBadSpaces) {
            if (firstName.equals(foundBadSpace.get(0)) && secondName.equals(foundBadSpace.get(1)) && description.equals(foundBadSpace.get(2))) {
                return true;
            }
        }
        return false;
    }

    private static void addNewBadSpace(BadSpace badSpace, List<List<String>> foundBadSpaces) {
        String firstName = "", secondName = "";
        if (badSpace.getFirstEntry() != null) {
            firstName = badSpace.getFirstEntry().getName() == null ? "" : badSpace.getFirstEntry().getName();

        }
        if (badSpace.getSecondEntry() != null) {
            secondName = badSpace.getSecondEntry().getName() == null ? "" : badSpace.getSecondEntry().getName();
        }
        String description = badSpace.getDescpription();
        List<String> newBadSpace = new ArrayList<>();
        newBadSpace.add(firstName); newBadSpace.add(secondName);
        if (description.contains("Позднее начало пар в этот день")) {
            newBadSpace.add("Позднее начало пар в этот день");
        } else {
            newBadSpace.add(description);
        }
        foundBadSpaces.add(newBadSpace);
    }
}
