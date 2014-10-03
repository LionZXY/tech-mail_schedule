package ru.mail.tp.schedule.schedule;

import java.io.Serializable;
import java.util.ArrayList;

import ru.mail.tp.schedule.schedule.entities.ScheduleItem;
import ru.mail.tp.schedule.schedule.filter.ScheduleFilter;

/**
 * author: grigory51
 * date: 27/09/14
 */
public class ScheduleBuilder implements Serializable {
    private final ArrayList<ScheduleItem> list;

    public ScheduleBuilder(ArrayList<ScheduleItem> list) {
        this.list = list;
    }

    public ArrayList<ScheduleItem> getScheduleItems() {
        return this.getScheduleItems(new ScheduleFilter());
    }

    public ArrayList<ScheduleItem> getScheduleItems(ScheduleFilter filter) {
        if (filter != null) {
            ArrayList<ScheduleItem> result = new ArrayList<ScheduleItem>();

            for (ScheduleItem item : this.list) {
                if (item.isFilterMatch(filter)) {
                    result.add(item);
                }
            }
            return result;
        } else {
            return this.list;
        }
    }
}
