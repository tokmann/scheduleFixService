package app.service;

import app.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class ScheduleService {

    @Autowired
    BadSpaceFinder badSpaceFinder;

    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final String baseURL = "https://schedule-of.mirea.ru/schedule/api/search";

    public final ConcurrentHashMap<String, Future<List<BadSpace>>> tasks = new ConcurrentHashMap<>();
    public final ExecutorService executor = Executors.newFixedThreadPool(5);

    public String startBadSpaceSearch(String criteria) {
        String taskId = UUID.randomUUID().toString();

        Future<List<BadSpace>> future = executor.submit(() -> badSpaceFinder.findBadSpaces(criteria));

        tasks.put(taskId, future);

        return taskId;

    }

    public String startAllBadSpaceSearch() {
        String taskId = UUID.randomUUID().toString();

        Future<List<BadSpace>> future = executor.submit(() -> badSpaceFinder.findAllBadSpaces());

        tasks.put(taskId, future);

        return taskId;
    }

    public ResponseEntity<?> getTaskStatus(String taskId) {
        Future<List<BadSpace>> future = tasks.get(taskId);

        if (future == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        if (!future.isDone()) {
            return ResponseEntity.ok("Status: in progress");
        }

        try {
            return ResponseEntity.ok(future.get());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing task");
        }
    }


    @PreDestroy
    public void shutdownExecutor() {
        executor.shutdown();
    }
}
