package ua.kpi.mishchenko.mentoringsystem.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageBO<T> {

    private List<T> content = new ArrayList<>();

    private int currentPageNumber;

    private int totalPages;

    public PageBO(int currentPageNumber, int totalPages) {
        this.currentPageNumber = currentPageNumber;
        this.totalPages = totalPages;
    }

    public boolean hasContent() {
        return !this.content.isEmpty();
    }

    public void addElement(T element) {
        content.add(element);
    }
}
