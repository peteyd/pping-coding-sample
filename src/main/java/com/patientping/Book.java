package com.patientping;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Book {
    private final int id;
    private final String title;
    private final String authors;
    private final float averageRating;
    private final String isbn;
    private final String isbn13;
    private final String language;
    private final int numPages;
    private final int ratingCount;
    private final int textReviewsCount;

    public float ratingsReviewsRatio() {
      return ((float) this.getRatingCount()) / ((float) this.getTextReviewsCount());
    }
}
