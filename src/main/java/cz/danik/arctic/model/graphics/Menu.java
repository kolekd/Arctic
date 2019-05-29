package cz.danik.arctic.model.graphics;

import java.util.List;

public class Menu {

    public static class Builder {

        private String title;
        private String subTitle;
        private List<String> lines;

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withSubTitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        public Builder withLines(List<String> lines) {
            this.lines = lines;
            return this;
        }

        public Menu build() {
            Menu menu = new Menu();

            menu.title = this.title;
            menu.subTitle = this.subTitle;
            menu.lines = this.lines;

            return menu;
        }
    }

    private String title;
    private String subTitle;
    private List<String> lines;

    private Menu() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }
}
