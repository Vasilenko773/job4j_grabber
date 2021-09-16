package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        try {
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", store);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(7)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (SchedulerException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            try (PreparedStatement statement = getConnection().prepareStatement("insert into rabbit(create_date) values (?)")) {
                statement.setString(1, "10.01.2022");
                statement.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void execute(JobExecutionContext context) {
                try {
                List<Connection> store = (List<Connection>) context.getJobDetail().getJobDataMap().get("store");
                store.add(Rabbit.getConnection());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static Connection getConnection() throws Exception {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://127.0.0.1:5432/schema";
            String login = "postgres";
            String password = "password";
            return DriverManager.getConnection(url, login, password);
        }
    }
}


