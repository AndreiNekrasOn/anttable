package com.guu.anttable.web.controller;

import com.guu.anttable.alg.GAJenetics;
import com.guu.anttable.utils.DayFormat;
import com.guu.anttable.utils.Group;
import com.guu.anttable.utils.Timetable;
import com.guu.anttable.web.TimetablePojo;
import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.guu.anttable.Main.*;


/**
 * @author nekrasov
 * Date: 26.12.2022
 */
@RestController
public class GenerateController {

    @GetMapping("/generate")
    @ResponseBody
    public TimetablePojo generate() throws IOException, JSONException {
        DayFormat firstShift = new DayFormat(new ArrayList<>(
                List.of("8:15 - 8:55", "9:05 - 9:50", "10:00 - 11:45",
                        "12:30 - 13:15", "14:00 - 14:45")));
        List<Group> groups;

        List<String> groupsNames = List.of(
                "ПМИ", "Бизнес-информатика-1", "Бизнес-информатика-2");
        groups = parseGroupData("", groupsNames);

        GAJenetics.initialize(
                generateTimeslots(5, 6),
                transformGroupsToActivities(groups), firstShift);
        Timetable bestTimetable = GAJenetics.run();
        System.out.println(bestTimetable);

        return new TimetablePojo(bestTimetable);
    }

}
