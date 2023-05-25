package com.guu.anttable.web.studyplan;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.guu.anttable.genetic.Parser;
import com.guu.anttable.genetic.alg.GAJenetics;
import com.guu.anttable.genetic.utils.Timeslot;
import com.guu.anttable.web.timetable.Timetable;


@Controller
public class StudyPlanController {

    @GetMapping("/studyplan")
    public String jsonStudyPlanForm(Model model) {
        model.addAttribute("studyplan", new StudyPlan());
        return "studyplan";
    }

    @PostMapping("/generate")
    public String jsonStudyPlanSubmit(@ModelAttribute StudyPlan studyPlan, Model model) {
        List<Timeslot> timeslots = Parser.generateTimeslots(Parser.MAX_CLASSES, Parser.DAYS_IN_WEEK);
        List<com.guu.anttable.genetic.utils.Activity> activities = Parser.transformGroupsToActivities(Parser.parseGroupData(studyPlan.getJsonStudyPlan()));
        GAJenetics engine = new GAJenetics(timeslots, activities);
        com.guu.anttable.genetic.utils.Timetable result = engine.run();
        Timetable timetable = new Timetable();
        timetable.setJsonRepr(Parser.formatTimteableMatrix(Parser.toMatrix(result)));
        model.addAttribute("timetable", timetable);
        return "timetable";
    }

}


