package com.example.layarkaryadev.Model;

public class MovieUploadDetails {
    String movie_slide, movie_type, movie_thumbnail, movie_url, movie_name, movie_description, movie_category;

    public MovieUploadDetails(String movie_slide, String movie_type, String movie_thumbnail,
                              String movie_url, String movie_name, String movie_description,
                              String movie_category) {
        this.movie_slide = movie_slide;
        this.movie_type = movie_type;
        this.movie_thumbnail = movie_thumbnail;
        this.movie_url = movie_url;
        this.movie_name = movie_name;
        this.movie_description = movie_description;
        this.movie_category = movie_category;
    }

    public MovieUploadDetails() {
    }

    public String getMovie_slide() {
        return movie_slide;
    }

    public void setMovie_slide(String movie_slide) {
        this.movie_slide = movie_slide;
    }

    public String getMovie_type() {
        return movie_type;
    }

    public void setMovie_type(String movie_type) {
        this.movie_type = movie_type;
    }

    public String getMovie_thumbnail() {
        return movie_thumbnail;
    }

    public void setMovie_thumbnail(String movie_thumbnail) {
        this.movie_thumbnail = movie_thumbnail;
    }

    public String getMovie_url() {
        return movie_url;
    }

    public void setMovie_url(String movie_url) {
        this.movie_url = movie_url;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    public String getMovie_description() {
        return movie_description;
    }

    public void setMovie_description(String movie_description) {
        this.movie_description = movie_description;
    }

    public String getMovie_category() {
        return movie_category;
    }

    public void setMovie_category(String movie_category) {
        this.movie_category = movie_category;
    }
}
