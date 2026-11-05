package com.example.movies.listener;

import com.example.movies.processor.SqliteMovieProcessor;
import com.example.movies.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieListener implements StepExecutionListener {

    private final MovieRepository movieRepository;
    private final SqliteMovieProcessor sqliteMovieProcessor;


    @Override
    public void beforeStep(StepExecution stepExecution) {
        List<String> existingImdbs = movieRepository.findAllImdbs();
        sqliteMovieProcessor.fillExistingMovies(existingImdbs);
    }


}
