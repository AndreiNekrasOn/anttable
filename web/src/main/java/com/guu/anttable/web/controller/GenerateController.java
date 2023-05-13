package com.guu.anttable.web.controller;

import static com.guu.anttable.Main.generateTimeslots;
import static com.guu.anttable.Main.parseGroupData;
import static com.guu.anttable.Main.transformGroupsToActivities;

import java.io.IOException;

import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guu.anttable.Main;
import com.guu.anttable.alg.GAJenetics;
import com.guu.anttable.utils.Timetable;


/**
 * @author nekrasov
 * Date: 26.12.2022
 */
@RestController
public class GenerateController {

    @GetMapping("/generate")
    @ResponseBody
    public Timetable generate() throws IOException, JSONException {
        var groups = parseGroupData("/plan_8_semester.java");
        GAJenetics engine = new GAJenetics(generateTimeslots(Main.MAX_CLASSES, Main.DAYS_IN_WEEK), transformGroupsToActivities(groups));
        return engine.run();
    }

}
