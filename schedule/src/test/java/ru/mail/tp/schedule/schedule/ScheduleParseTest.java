package ru.mail.tp.schedule.schedule;

import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

public class ScheduleParseTest {
    @Test
    public void testScheduleParse() throws Exception {
        File file = new File(ClassLoader.getSystemResource("data.json").getFile());
        StringBuilder sb = new StringBuilder();
        for (String line : Files.readAllLines(file.toPath())) {
            sb.append(line);
        }


        JSONObject jsonObject = new JSONObject(sb.toString());

        ScheduleJSONProcessor scheduleJSONProcessor = new ScheduleJSONProcessor(jsonObject);
    }
}
