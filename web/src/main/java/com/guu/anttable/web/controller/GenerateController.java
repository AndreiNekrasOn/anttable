package com.guu.anttable.web.controller;

import static com.guu.anttable.Main.generateTimeslots;
import static com.guu.anttable.Main.parseGroupData;
import static com.guu.anttable.Main.transformGroupsToActivities;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.guu.anttable.alg.GAJenetics;
import com.guu.anttable.utils.Group;
import com.guu.anttable.web.Timetable;


/**
 * @author nekrasov
 * Date: 26.12.2022
 */
@RestController
public class GenerateController {

    @GetMapping("/generate")
    @ResponseBody
    public Timetable generate() throws IOException, JSONException {
        List<Group> groups;
        List<String> groupsNames = List.of("ПМИ", "Бизнес-информатика-1", "Бизнес-информатика-2");
        groups = parseGroupData("", groupsNames);
        GAJenetics.initialize(generateTimeslots(5, 6), transformGroupsToActivities(groups));
        return new Timetable(GAJenetics.run());
    }

}
