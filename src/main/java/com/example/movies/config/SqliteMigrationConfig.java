package com.example.movies.config;

import com.example.movies.listener.MovieListener;
import com.example.movies.model.MovieEntity;
import com.example.movies.model.dtos.SqliteMovieDto;
import com.example.movies.processor.SqliteMovieProcessor;
import com.example.movies.service.MovieService;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@RequiredArgsConstructor
@Configuration
public class SqliteMigrationConfig {

    private static String SQL_MOVIE_STATMENT = "Select guid, metadata_type, title, originally_available_at as release_date, rating, summary, tags_genre ,\n" +
            "        tags_director, tags_writer, tags_star, year,media_items.duration as duration,\n" +
            "        GROUP_CONCAT(DISTINCT CASE WHEN stream_types.id = 1 THEN media_streams.language END) as audio,\n" +
            "        GROUP_CONCAT(DISTINCT CASE WHEN stream_types.id = 3 THEN media_streams.language END) as subtitle\n" +
            "from metadata_items\n" +
            "left join media_items on metadata_items.id = media_items.metadata_item_id\n" +
            "left join  media_streams on media_streams.media_item_id = metadata_items.id\n" +
            "left join  stream_types on media_streams.stream_type_id = stream_types.id AND media_streams.stream_type_id In(1,3)\n" +
            "where metadata_type = 1\n" +
            "group by metadata_item_id";

    private final SqliteMovieProcessor processor;
    private final MovieService movieService;
    private final MovieListener listener;


    @Bean
    public Job ioMigrationJob(JobRepository jobRepository) {
        return new JobBuilder("ioSimpleJob", jobRepository)
                .start(fromSqliteToPostgres(jobRepository, movieService, listener))
                .build();
    }

    @Bean
    public Step fromSqliteToPostgres(JobRepository jobRepository, MovieService movieService, MovieListener movieListener) {
        return new StepBuilder("from db to db", jobRepository)
                .<SqliteMovieDto, MovieEntity>chunk(100)
                .reader(itemReader(null))
                .processor(processor)
                .writer(movieWriter(movieService))
                .listener(movieListener)
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<SqliteMovieDto> itemReader(@Value("#{jobParameters['sqliteFileName']}") String fileName) {

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("sqliteFileName job parameter must not be null or empty");
        }

        String jdbcUrl = "jdbc:sqlite:" + fileName;

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl(jdbcUrl);

        return new JdbcCursorItemReaderBuilder<SqliteMovieDto>()
                .dataSource(dataSource)
                .name("movieReader")
                .sql(SQL_MOVIE_STATMENT)
                .rowMapper(new DataClassRowMapper<>(SqliteMovieDto.class))
                .verifyCursorPosition(false)
                .build();
    }

    @Bean
    public ItemWriter<MovieEntity> movieWriter(MovieService movieService) {
        return chunk -> {
            for (MovieEntity e : chunk) {
                movieService.createMovie(e);
            }
        };
    }

}
