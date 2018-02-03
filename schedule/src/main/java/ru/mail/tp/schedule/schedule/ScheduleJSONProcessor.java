package ru.mail.tp.schedule.schedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ru.mail.tp.schedule.schedule.db.entities.Discipline;
import ru.mail.tp.schedule.schedule.db.entities.LessonType;
import ru.mail.tp.schedule.schedule.db.entities.Place;
import ru.mail.tp.schedule.schedule.db.entities.ScheduleItem;
import ru.mail.tp.schedule.schedule.db.entities.Subgroup;
import ru.mail.tp.schedule.schedule.parser.GroupExtractProcessor;
import ru.mail.tp.schedule.utils.StringHelper;


/**
 * author: grigory51
 * date: 04.07.14
 */
public class ScheduleJSONProcessor {
    private final Map<String, Place> auditoriums;
    private final Map<String, Place> places;

    private final Map<String, Subgroup> subgroups;
    private final Map<String, Discipline> disciplines;
    private final Map<String, LessonType> lessonTypes;

    private JSONArray timetable;

    public ScheduleJSONProcessor(JSONObject json) throws JSONException {
        final JSONArray schedule = json.getJSONArray("aSchedule");
        final List<JSONObject> events = new ArrayList<>();
        for (int i = 0; i < schedule.length(); i++) {
            JSONObject event = (JSONObject) schedule.get(i);
            events.add(event);
        }

        auditoriums = GroupExtractProcessor.getAuditoriums(events);
        places = GroupExtractProcessor.getPlaces(events);
        subgroups = GroupExtractProcessor.getSubgrops(events);
        disciplines = GroupExtractProcessor.getDisciplines(events);
        lessonTypes = GroupExtractProcessor.getLessons(events);

        this.timetable = schedule;
    }

    public ArrayList<ScheduleItem> getScheduleItems() throws JSONException {
        ArrayList<ScheduleItem> result = new ArrayList<>();
        for(int i = 0; i < timetable.length(); i++) {
            JSONObject item = this.timetable.getJSONObject(i);
            result.add(this.getScheduleItemFromJSON(item));
        }

        Collections.sort(result, new Comparator<ScheduleItem>() {
            public int compare(ScheduleItem a, ScheduleItem b) {
                if (a.getTimeStart().getTime() == b.getTimeStart().getTime()) {
                    return 0;
                } else {
                    return a.getTimeStart().getTime() - b.getTimeStart().getTime() > 0 ? 1 : -1;
                }
            }
        });

        return result;
    }

    private ScheduleItem getScheduleItemFromJSON(JSONObject item) throws JSONException {
        int id = item.getInt("id");
        long timeStart = item.getLong("start");
        long timeEnd = item.getLong("end");
        String title;

        String eventType = item.getString("type_entity");
        if (eventType.equals("event")) {
            title = StringHelper.quotesFormat(item.getString("title"));
            Place place = null;
            try {
                place = places.get(item.getString("place_title"));
            } catch (JSONException e) {
                place = auditoriums.get(item.getString("auditorium_number"));
            }
            return new ScheduleItem(id, timeStart, timeEnd, title, place);
        } else if (eventType.equals("lesson")) {
            Discipline discipline = this.disciplines.get(item.getString("short_title"));
            LessonType lessonType = this.lessonTypes.get(item.getString("event_title"));
            int number = item.getInt("number");

            JSONArray subgroupsIds = item.getJSONArray("subgroups");
            ArrayList<Subgroup> subgroups = new ArrayList<>();
            HashSet<String> uniqueIds = new HashSet<>(); //фиксит баг в API (дублирующиеся id учебных групп), временный костыль

            for (int i = 0; i < subgroupsIds.length(); i++) {
                final String key = (String) subgroupsIds.get(i);
                if (!uniqueIds.contains(key)) {
                    uniqueIds.add(key);
                    Subgroup subgroup = this.subgroups.get(key);
                    if (subgroup != null) {
                        subgroups.add(subgroup);
                    }
                }
            }

            Place place = null;
            try {
                place = places.get(item.getString("place_title"));
            } catch (JSONException e) {
                place = auditoriums.get(item.getString("auditorium_number"));
            }

            return new ScheduleItem(id, timeStart, timeEnd, place, subgroups, discipline, lessonType, number);
        }
        return null;
    }

    public ArrayList<Place> getPlaces() {
        final ArrayList<Place> placesList = new ArrayList<Place>(this.places.values());
        final ArrayList<Place> auditoriumList = new ArrayList<Place>(this.auditoriums.values());

        return new ArrayList<Place>() {{
            addAll(placesList);
            addAll(auditoriumList);
        }};
    }

    public ArrayList<Subgroup> getSubgroups() {
        return new ArrayList<Subgroup>(this.subgroups.values());
    }

    public ArrayList<Discipline> getDisciplines() {
        return new ArrayList<Discipline>(this.disciplines.values());
    }

    public ArrayList<LessonType> getLessonTypes() {
        return new ArrayList<LessonType>(this.lessonTypes.values());
    }
}