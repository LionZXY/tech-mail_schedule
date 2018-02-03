package ru.mail.tp.schedule.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TimeUtils;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import ru.mail.tp.schedule.R;
import ru.mail.tp.schedule.tasks.ITaskResultReceiver;
import ru.mail.tp.schedule.tasks.TaskResult;
import ru.mail.tp.schedule.tasks.scheduleFetch.ScheduleFetchTask;
import ru.mail.tp.schedule.tasks.scheduleFetch.ScheduleFetchTaskResult;

public class SplashActivity extends Activity implements ITaskResultReceiver {
    public static final String TAG_FETCH_RESULT = "fetchResult";
    public static final int DAY_IN_YEAR = 365;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/pfisopro_regular.ttf");
        TextView myTextView = (TextView) findViewById(R.id.a_splash__titleTextView);
        myTextView.setTypeface(myTypeface);
    }

    @Override
    public void onStart() {
        super.onStart();
        ScheduleFetchTask task = new ScheduleFetchTask(this, getString(R.string.dataSourceUrl,
                System.currentTimeMillis() / 1000L + TimeUnit.DAYS.toSeconds(DAY_IN_YEAR)));
        task.execute();
    }

    @Override
    public void onPostExecute(TaskResult taskResult) {
        ScheduleFetchTaskResult result = (ScheduleFetchTaskResult) taskResult;
        startActivity(new Intent(this, ScheduleActivity.class).putExtra(TAG_FETCH_RESULT, result));
        finish();
    }
}