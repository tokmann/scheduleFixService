package app.controller;

import app.model.BadSpace;
import app.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/schedule-bad-spaces")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/match/{criteria}")
    public List<BadSpace> getBadSpaces(@PathVariable("criteria") String criteria) throws Exception {
        System.out.println("Запрос отправлен");
        return scheduleService.findBadSpaces(criteria);
    }

    @GetMapping("/all")
    public List<BadSpace> getAllBadSpaces() throws Exception {
        System.out.println("Запрос отправлен");
        return scheduleService.findAllBadSpaces();
    }

}
