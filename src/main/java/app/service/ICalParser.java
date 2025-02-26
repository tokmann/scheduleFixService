package app.service;

import app.model.BadSpace;
import app.model.ScheduleEntry;
import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import biweekly.property.*;
import biweekly.util.ICalDate;
import biweekly.util.Recurrence;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@EnableAsync
public class ICalParser {

    public List<ScheduleEntry> parseICalContent(String iCalData) {
        List<ScheduleEntry> entries = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime oneMonthLater = today.plusMonths(1);
        /*Здесь можно задать длительность
        для генерации дней начиная от сегодняшнего дня.
        По умолчанию стоит 1 мес. */

        try {
            List<ICalendar> calendars = Biweekly.parse(iCalData).all();
            if (calendars.isEmpty()) {
                System.out.println("Ошибка: iCalendar файл пуст или неверного формата.");
                return null;
            }

            for (ICalendar calendar : calendars) {
                for (VEvent event : calendar.getEvents()) {
                    if (event.getSummary().getValue().contains("Все занятия в дистанционном формате") ||
                            event.getSummary().getValue().contains("неделя")) continue;


                    LocalDateTime startTime = convertToLocalDateTime(event.getDateStart());
                    LocalDateTime endTime = convertToLocalDateTime(event.getDateEnd());
                    String teacher = (event.getDescription() != null) ? extractTeacherName(event.getDescription().getValue()) : null;
                    String location = (event.getLocation() != null) ? event.getLocation().getValue() : null;
                    String name = event.getSummary().getValue();


                    Set<LocalDateTime> excludedDates = new HashSet<>();
                    List<ExceptionDates> exdates = event.getProperties(ExceptionDates.class);

                    if (exdates != null) {
                        for (ExceptionDates exDate : exdates) {
                            for (ICalDate date : exDate.getValues()) {
                                excludedDates.add(convertDateToLocalDateTime(date));
                            }
                        }
                    }


                    Recurrence rrule = (event.getRecurrenceRule() != null) ? event.getRecurrenceRule().getValue() : null;
                    if (rrule == null) {
                        if (!excludedDates.contains(startTime) && startTime.isBefore(oneMonthLater)) {
                            entries.add(createScheduleEntry(name, teacher, location, startTime, endTime));
                        }
                        continue;
                    }


                    int interval = rrule.getInterval();
                    ChronoUnit unit = ChronoUnit.WEEKS;

                    if (rrule != null) {
                        String frequency = String.valueOf(rrule.getFrequency());
                        if ("DAILY".equals(frequency)) {
                            unit = ChronoUnit.DAYS;
                        } else if ("MONTHLY".equals(frequency)) {
                            unit = ChronoUnit.MONTHS;
                        } else if ("YEARLY".equals(frequency)) {
                            unit = ChronoUnit.YEARS;
                        }
                    }


                    LocalDateTime currentStart = startTime;
                    LocalDateTime currentEnd = endTime;

                    while (currentStart.isBefore(oneMonthLater)) {
                        if (!excludedDates.contains(currentStart)) {
                            entries.add(createScheduleEntry(name, teacher, location, currentStart, currentEnd));
                        }
                        currentStart = currentStart.plus(interval, unit);
                        currentEnd = currentEnd.plus(interval, unit);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка парсинга ICAL: " + e.getMessage());
        }

        return entries;
    }

    private static ScheduleEntry createScheduleEntry(String name, String teacher, String location, LocalDateTime start,
                                                     LocalDateTime end) {
        ScheduleEntry entry = new ScheduleEntry();

        entry.setName(name);
        entry.setTeacher(teacher);
        entry.setClassroom(location);
        entry.setStartTime(start);
        entry.setEndTime(end);
        entry.setReadableTimeStart(translateToRussian(String.valueOf(start)));
        entry.setReadableTimeEnd(translateToRussian(String.valueOf(end)));
        return entry;
    }

    public static String extractTeacherName(String input) {
        String regex = "Преподаватель:\\s*([А-ЯЁа-яё]+\\s[А-ЯЁа-яё]+\\s[А-ЯЁа-яё]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);


        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return null;
    }

    private static LocalDateTime convertToLocalDateTime(DateStart dateStart) {
        return convertDateToLocalDateTime(dateStart.getValue());
    }

    private static LocalDateTime convertToLocalDateTime(DateEnd dateEnd) {
        return convertDateToLocalDateTime(dateEnd.getValue());
    }

    private static LocalDateTime convertDateToLocalDateTime(Date date) {
        if (date == null) return null;
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.of("Europe/Moscow"))
                .toLocalDateTime();
    }

    public static String translateToRussian(String inputDate) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime localDateTime = LocalDateTime.parse(inputDate, inputFormatter);

        // Конвертируем в ZonedDateTime с учётом часового пояса MSK (UTC+3)
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Europe/Moscow"));

        // Форматируем в требуемый вид
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.forLanguageTag("ru"));
        String formattedDate = zonedDateTime.format(outputFormatter);
        return formattedDate;
    }


}
