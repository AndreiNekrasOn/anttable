var messageApi = Vue.resource('/message{/id}');

Vue.component('generate-btn')

Vue.component('message-row', {
    props: ['message'],
    template: '<div><i>{{ message.id }}</i> {{ message.text }}</div>'
});

Vue.component('schedule-class', {
   props: ['activity'],
    template: `
        <div  v-if="activity && activity.group">{{activity.subject}} -- ({{activity.teacher}})</div>
        <div v-else>
            <div v-if="activity">{{activity}}</div> <!-- class number or day -->
            <div v-else> - </div>
        </div>
    `
});

Vue.component('schedule-table', {
    props: ['activities'],
    template: `
        <div>
            <table border="1px"> 
                <tr v-for="activityRow in activities">
                   <td v-for="activity in activityRow"><schedule-class :activity="activity"/></td>
                </tr>
            </table>
        </div>
    `,
    created: function () {
        const shift = ["8:15 - 8:55", "9:05 - 9:50", "10:00 - 11:45",
            "12:30 - 13:15", "14:00 - 14:45"];
        const days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
        let activities = this.$props.activities;
        let formated = [
            ["Пара\\День", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"],
            ["1", "", "", "", "", "", ""],
            ["2", "", "", "", "", "", ""],
            ["3", "", "", "", "", "", ""],
            ["4", "", "", "", "", "", ""],
            ["5", "", "", "", "", "", ""],
            ["6", "", "", "", "", "", ""],
        ];
        activities.forEach(activity => {
           formated[days.indexOf(activity.day) + 1][shift.indexOf(activity.time) + 1] = activity;
        });
        this.$props.activities = formated;
    }
});

Vue.component("schedule-groups", {
    props: ['timetable'],
    template: `
        <div>
            <h2>Расписание</h2>
            <h3>{{timetable.institute}}</h3>
            <div v-for="groupActivities in timetable">
                <h4>{{ groupActivities[0]['group'] }}</h4>
                <schedule-table :activities="groupActivities"/>
            </div>
        </div>
    `,
    created: function () {
        let activities = this.$props.timetable.activities;
        let groupBy = function(xs, key) {
            return xs.reduce(function(rv, x) {
                (rv[x[key]] = rv[x[key]] || []).push(x);
                return rv;
            }, {});
        };
        this.$props.timetable = groupBy(activities, 'group');
    }
})


let app = new Vue({
    el: '#app',
    template: `<div><schedule-table :activities="messages.activities"/>
        <schedule-groups :timetable="messages"/></div>    
    `,
    data: {
        messages: JSON.parse('{"institute":"ИИС","activities":[{"day":"Thu","group":"ПМИ","subject":"Тигры","teacher":"Прохоров","time":"9:05 - 9:50"},{"day":"Fri","group":"ПМИ","subject":"Тигры","teacher":"Прохоров","time":"10:00 - 11:45"},{"day":"Thu","group":"ПМИ","subject":"ЧМы","teacher":"Антипова","time":"14:00 - 14:45"},{"day":"Mon","group":"ПМИ","subject":"ЧМы","teacher":"Антипова","time":"14:00 - 14:45"},{"day":"Wed","group":"ПМИ","subject":"ЧМы","teacher":"Антипова","time":"10:00 - 11:45"},{"day":"Tue","group":"ПМИ","subject":"Физкультура","teacher":"Физкультура","time":"8:15 - 8:55"},{"day":"Wed","group":"ПМИ","subject":"Англ","teacher":"Англ","time":"9:05 - 9:50"},{"day":"Tue","group":"ПМИ","subject":"МЛ","teacher":"Глазков","time":"12:30 - 13:15"},{"day":"Fri","group":"ПМИ","subject":"МЛ","teacher":"Глазков","time":"9:05 - 9:50"},{"day":"Thu","group":"ПМИ","subject":"Нейро","teacher":"Бодров","time":"8:15 - 8:55"},{"day":"Wed","group":"ПМИ","subject":"Нейро","teacher":"Бодров","time":"12:30 - 13:15"},{"day":"Tue","group":"ПМИ","subject":"Фин","teacher":"Мишин","time":"10:00 - 11:45"},{"day":"Mon","group":"ПМИ","subject":"Фин","teacher":"Мишин","time":"9:05 - 9:50"},{"day":"Mon","group":"Бизнес-информатика-1","subject":"ТАИС","teacher":"Орешина","time":"8:15 - 8:55"},{"day":"Mon","group":"Бизнес-информатика-1","subject":"ТАИС","teacher":"Орешина","time":"14:00 - 14:45"},{"day":"Wed","group":"Бизнес-информатика-1","subject":"ТАИС","teacher":"Орешина","time":"8:15 - 8:55"},{"day":"Mon","group":"Бизнес-информатика-1","subject":"Англ","teacher":"Англ","time":"9:05 - 9:50"},{"day":"Fri","group":"Бизнес-информатика-1","subject":"Англ","teacher":"Англ","time":"12:30 - 13:15"},{"day":"Tue","group":"Бизнес-информатика-1","subject":"Экономика фирмы","teacher":"Данилина","time":"14:00 - 14:45"},{"day":"Thu","group":"Бизнес-информатика-1","subject":"Экономика фирмы","teacher":"Данилина","time":"8:15 - 8:55"},{"day":"Thu","group":"Бизнес-информатика-1","subject":"Экономика фирмы","teacher":"Данилина","time":"14:00 - 14:45"},{"day":"Sat","group":"Бизнес-информатика-1","subject":"Sap","teacher":"Белоусова","time":"14:00 - 14:45"},{"day":"Fri","group":"Бизнес-информатика-1","subject":"Sap","teacher":"Белоусова","time":"8:15 - 8:55"},{"day":"Fri","group":"Бизнес-информатика-1","subject":"Sap","teacher":"Белоусова","time":"14:00 - 14:45"},{"day":"Thu","group":"Бизнес-информатика-1","subject":"Финансовая математика","teacher":"Мишин","time":"10:00 - 11:45"},{"day":"Wed","group":"Бизнес-информатика-1","subject":"Финансовая математика","teacher":"Мишин","time":"9:05 - 9:50"},{"day":"Sat","group":"Бизнес-информатика-2","subject":"ТАИС","teacher":"Орешина","time":"10:00 - 11:45"},{"day":"Tue","group":"Бизнес-информатика-2","subject":"ТАИС","teacher":"Орешина","time":"9:05 - 9:50"},{"day":"Wed","group":"Бизнес-информатика-2","subject":"ТАИС","teacher":"Орешина","time":"12:30 - 13:15"},{"day":"Thu","group":"Бизнес-информатика-2","subject":"Англ","teacher":"Англ","time":"9:05 - 9:50"},{"day":"Thu","group":"Бизнес-информатика-2","subject":"Англ","teacher":"Англ","time":"12:30 - 13:15"},{"day":"Mon","group":"Бизнес-информатика-2","subject":"Экономика фирмы","teacher":"Данилина","time":"9:05 - 9:50"},{"day":"Wed","group":"Бизнес-информатика-2","subject":"Экономика фирмы","teacher":"Данилина","time":"10:00 - 11:45"},{"day":"Mon","group":"Бизнес-информатика-2","subject":"Экономика фирмы","teacher":"Данилина","time":"12:30 - 13:15"},{"day":"Fri","group":"Бизнес-информатика-2","subject":"Sap","teacher":"Белоусова","time":"12:30 - 13:15"},{"day":"Sat","group":"Бизнес-информатика-2","subject":"Sap","teacher":"Белоусова","time":"8:15 - 8:55"},{"day":"Tue","group":"Бизнес-информатика-2","subject":"Sap","teacher":"Белоусова","time":"8:15 - 8:55"},{"day":"Wed","group":"Бизнес-информатика-2","subject":"Финансовая математика","teacher":"Мишин","time":"8:15 - 8:55"},{"day":"Fri","group":"Бизнес-информатика-2","subject":"Финансовая математика","teacher":"Мишин","time":"9:05 - 9:50"}]}')
    }
});