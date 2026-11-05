package com.example.movies.page_filters;

import com.example.movies.model.dtos.MovieDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

import static com.example.movies.constants.StringBundle.ExceptionStrings.SORT_INVALID_ARGS;

@RequiredArgsConstructor
@Component
public class MoviePageProcessor {

    private final String SORT_ORDER_DESC = "DESC";
    private final MovieComparator movieComparator;

    public Page<MovieDto> process(List<MovieDto> dtoList, int page, int size, String filter, String sort) {

        Comparator<MovieDto> comparator;
        if (isReversedOrder(sort)) {
            comparator = movieComparator.buildComparator(sort).reversed();
        } else {
            comparator = movieComparator.buildComparator(sort);
        }

        List<MovieDto> filteredContent = filterPageContent(dtoList, filter);
        List<MovieDto> sortedContent = sortPageContent(filteredContent, comparator);
        PageRequest pageRequest = PageRequest.of(page, size);

        int start = page * size;
        int end = Math.min(start + size, sortedContent.size());

        if (start >= sortedContent.size()) {
            return new PageImpl<>(List.of(), pageRequest, sortedContent.size());
        }

        List<MovieDto> pageContent = extractPageElements(sortedContent, start, end);

        return new PageImpl<>(pageContent, pageRequest, pageContent.size());
    }

    private List<MovieDto> sortPageContent(List<MovieDto> filteredContent, Comparator<MovieDto> comparator) {
        return filteredContent.stream()
                .sorted(comparator)
                .toList();
    }

    private List<MovieDto> extractPageElements(List<MovieDto> contentForPage, int start, int end) {
        return contentForPage.stream()
                .toList()
                .subList(start, end);
    }

    private List<MovieDto> filterPageContent(List<MovieDto> content, String filter) {
        return content.stream()
                .filter(f -> f.getTitle().contains(filter))
                .toList();
    }

    private boolean isReversedOrder(String sort) {
        if (sort == null) {
            return false;
        }
        String order;
        try {
            order = sort.split(",")[1];
        } catch (Exception e) {
            throw new IllegalArgumentException(SORT_INVALID_ARGS);
        }
        if (order == null || order.isEmpty()) {
            return false;
        }
        return order.equals(SORT_ORDER_DESC);
    }
}
