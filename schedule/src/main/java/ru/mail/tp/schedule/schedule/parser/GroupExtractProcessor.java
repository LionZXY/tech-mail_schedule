package ru.mail.tp.schedule.schedule.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.mail.tp.schedule.schedule.db.entities.Discipline;
import ru.mail.tp.schedule.schedule.db.entities.LessonType;
import ru.mail.tp.schedule.schedule.db.entities.Place;
import ru.mail.tp.schedule.schedule.db.entities.PlaceType;
import ru.mail.tp.schedule.schedule.db.entities.Subgroup;
import ru.mail.tp.schedule.utils.StringHelper;

public class GroupExtractProcessor {
    public static Map<String, Place> getAuditoriums(List<JSONObject> events) {
        final Map<String, Place> audioriums = new HashMap<>();

        for (JSONObject event : events) {
            String auditoriumKey;
            try {
                auditoriumKey = event.getString("auditorium_number");
            } catch (JSONException e) {
                continue;
            }

            String auditoriumTitle;
            try {
                auditoriumTitle = event.getString("auditorium_title");
            } catch (JSONException e) {
                auditoriumTitle = auditoriumKey;
            }

            audioriums.put(auditoriumKey, new Place(auditoriumKey, PlaceType.AUDITORY, auditoriumTitle));
        }
        return audioriums;
    }

    public static Map<String, Place> getPlaces(List<JSONObject> events) {
        final Map<String, Place> places = new HashMap<>();

        for (JSONObject event : events) {
            String placeKey;
            try {
                placeKey = event.getString("place_title");
            } catch (JSONException e) {
                continue;
            }

            places.put(placeKey, new Place(placeKey, PlaceType.PLACE, placeKey));
        }
        return places;
    }

    public static Map<String, Subgroup> getSubgrops(List<JSONObject> events) {
        final Map<String, Subgroup> subgroupMap = new HashMap<>();

        for (JSONObject event : events) {
            try {
                JSONArray subgroups;
                subgroups = event.getJSONArray("subgroups");
                for (int i = 0; i < subgroups.length(); i++) {
                    final String key = (String) subgroups.get(i);
                    subgroupMap.put(key, new Subgroup(key, key));
                }
            } catch (JSONException ignored) {

            }
        }
        return subgroupMap;
    }

    public static Map<String, Discipline> getDisciplines(List<JSONObject> events) {
        final Map<String, Discipline> disciplineMap = new HashMap<>();

        for (JSONObject event : events) {
            String disciplineTitle;
            try {
                disciplineTitle = StringHelper.quotesFormat(event.getString("title"));
            } catch (JSONException e) {
                continue;
            }

            String disciplineKey;
            try {
                disciplineKey = StringHelper.quotesFormat(event.getString("short_title"));
            } catch (JSONException e) {
                disciplineKey = disciplineTitle;
            }

            disciplineMap.put(disciplineKey, new Discipline(disciplineKey, disciplineTitle, disciplineKey));
        }
        return disciplineMap;
    }

    public static Map<String, LessonType> getLessons(List<JSONObject> events) {
        final Map<String, LessonType> lessonTypeMap = new HashMap<>();

        for (JSONObject event : events) {
            String lessonKey;
            try {
                lessonKey = event.getString("event_title");
            } catch (JSONException e) {
                continue;
            }

            String lessonTitle;
            try {
                lessonTitle = event.getString("type_title");
            } catch (JSONException e) {
                lessonTitle = lessonKey;
            }
            lessonTypeMap.put(lessonKey, new LessonType(lessonKey, lessonTitle));
        }
        return lessonTypeMap;
    }

}
