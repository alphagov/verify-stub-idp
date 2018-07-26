package uk.gov.ida;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.java.archives.Attributes;
import org.gradle.api.java.archives.Manifest;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class IdaJarPlugin implements Plugin<Project> {

    public IdaJarPlugin(){
        System.out.println("Creating plugin instance");
    }

    @Override
    public void apply(Project project) {

        Action<Task> action = new Action<Task>() {
            @Override
            public void execute(Task task) {
                Project project = task.getProject();
                System.out.println("IDA jar plugin");
                if (project.hasProperty("mainclass")) {
                    Manifest manifest = (Manifest) task.property("manifest");
                    Attributes attributes = manifest.getAttributes();
                    attributes.put("Main-Class", project.property("mainclass"));
                    StringBuilder sb = new StringBuilder();
                    for (File part : project.getConfigurations().getByName("runtime").getFiles()) {
                        sb.append("lib/");
                        sb.append(part.getName());
                        sb.append(" ");
                    }
                    System.out.println("Setting up class path, build number etc");
                    attributes.put("Class-Path", sb.toString());

                    String build_number = System.getenv("BUILD_NUMBER");
                    String git_commit = System.getenv("GIT_COMMIT");
                    attributes.put("Build-Number", (build_number != null ? build_number : "" ));
                    attributes.put("Git-Commit", (git_commit != null ? git_commit : "" ));

                    TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                    dateFormat.setTimeZone(utcTimeZone);
                    String isoFormat = dateFormat.format(new Date());
                    attributes.put("Build-Timestamp", isoFormat);

                    task.setProperty("archiveName", task.property("baseName") + ".jar");
                    task.setProperty("destinationDir", new File(project.getBuildDir(), "output"));
                }
            }
        };

        project.getTasks().getByName("jar").doFirst(action);
    }
}
