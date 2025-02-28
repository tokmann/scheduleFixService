package app.controller;

import app.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule-bad-spaces")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/match-async/{criteria}")
    public String startBadSpaceSearch(@PathVariable("criteria") String criteria) throws Exception {
        return scheduleService.startBadSpaceSearch(criteria);
    }

    @PostMapping("/find-all-async")
    public String startBadSpaceSearch() throws Exception {
        return scheduleService.startAllBadSpaceSearch();
    }

    @GetMapping("/status/{taskId}")
    public ResponseEntity<?> getTaskStatus(@PathVariable("taskId") String taskId) throws Exception {
        return scheduleService.getTaskStatus(taskId);
    }
}
