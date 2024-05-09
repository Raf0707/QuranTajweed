package raf.tabiin.qurantajweed.model;

import java.util.List;

public class PageData {
    private int page;
    private List<String> suras;
    private boolean isBookmarked;

    // Геттеры и сеттеры для всех полей
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<String> getSuras() {
        return suras;
    }

    public void setSuras(List<String> suras) {
        this.suras = suras;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }
}
