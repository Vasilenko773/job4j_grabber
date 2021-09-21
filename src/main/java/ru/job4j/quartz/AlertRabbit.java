package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {


    public static void main(String[] args) throws Exception {
        Properties ps = new Properties();

        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            ps.load(in);
            Class.forName(ps.getProperty("hibernate.connection.driver_class"));
            try (Connection cn = DriverManager.getConnection(ps.getProperty("hibernate.connection.url"),
                    ps.getProperty("hibernate.connection.username"),
                    ps.getProperty("hibernate.connection.password"))) {

                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                JobDataMap data = new JobDataMap();
                data.put("store", cn);
                JobDetail job = newJob(Rabbit.class)
                        .usingJobData(data)
                        .build();
                SimpleScheduleBuilder times = simpleSchedule()
                        .withIntervalInSeconds(Integer.parseInt(ps.getProperty("rabbit.interval")))
                        .repeatForever();
                Trigger trigger = newTrigger()
                        .startNow()
                        .withSchedule(times)
                        .build();
                scheduler.scheduleJob(job, trigger);
                Thread.sleep(1000);
                scheduler.shutdown();
            }
        }
    }

    private static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://127.0.0.1:5432/schema";
        String login = "postgres";
        String password = "password";

        return DriverManager.getConnection(url, login, password);
    }


    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            try (PreparedStatement st = AlertRabbit.getConnection().prepareStatement(
                    "insert into rabbit(create_date) values (?)")) {
                st.setString(1, "10.01.2022");
                st.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}