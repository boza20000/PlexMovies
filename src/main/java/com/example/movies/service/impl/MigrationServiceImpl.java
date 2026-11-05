package com.example.movies.service.impl;

import com.example.movies.constants.StringBundle;
import com.example.movies.service.MigrationService;
import lombok.SneakyThrows;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MigrationServiceImpl implements MigrationService {
    private final JobOperator jobOperator;
    private final Job job;

    public MigrationServiceImpl(JobOperator jobOperator, Job job) {
        this.jobOperator = jobOperator;
        this.job = job;
    }


    @SneakyThrows
    @Override
    public String migrateSqliteToPostgresMovies() {
        StringBuilder result = new StringBuilder();
        for (String file : StringBundle.FileConstants.sqliteFiles) {

            JobParameters parms = new JobParametersBuilder()
                    .addLocalDateTime("time", LocalDateTime.now())
                    .addString("sqliteFileName", file)
                    .toJobParameters();
            var jobExe = jobOperator.start(job, parms);

            result.append("Status: ")
                    .append(jobExe.getStatus())
                    .append("\n")
                    .append("For file:")
                    .append(file);
        }
        return result.toString();
    }
}
