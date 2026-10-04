package com.aryaa.sdm.controller;
import com.aryaa.sdm.dto.StatsDto; import com.aryaa.sdm.service.StatsService; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/stats") public class StatsController {private final StatsService service; public StatsController(StatsService s){service=s;} @GetMapping public StatsDto getStats(){return service.getStats();}}