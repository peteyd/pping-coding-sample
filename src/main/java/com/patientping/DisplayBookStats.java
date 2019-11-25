package com.patientping;

import com.patientping.BookStats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayBookStats {
    private BookStats stats;

    public DisplayBookStats(BookStats stats) {
      this.stats = stats;
    }

    public void printNumBooksLoaded() {
        System.out.println("Loaded " + stats.getBooks().size() + " books");
    }

    public void printNumEnglishBooks() {
        List<Book> englishBooks = stats.filterToEnglishBooks();
        System.out.println("" + englishBooks.size() + " Books in English");
    }

    public void printAuthorWithMostEnglishBooks() {
        // get a list of just the english books
        List<Book> englishBooks = stats.filterToEnglishBooks();
        // index those english books by author name
        Map<String, List<Book>> indexed = stats.indexByAuthor(englishBooks);
        // from that index, find the author who wrote the most books
        Map.Entry<String, Integer> mostEnglish = stats.findMostBooksByAuthor(indexed);

        if (mostEnglish != null) {
            System.out.println(
                String.format(
                    "Author with the most english books: %s. %d books.",
                    mostEnglish.getKey(),
                    mostEnglish.getValue()
                )
            );
        }
    }

    public void printHighestRatedBookAuthor() {
      Book highestRated = stats.findHighestRatedBook();

      if (highestRated != null) {
          System.out.println(
              String.format(
                  "Author with the highest rating: %s. %.2f stars.",
                  highestRated.getAuthors(),
                  highestRated.getAverageRating()
              )
          );
      }
    }

    public void printHighestRatioBookAuthor() {
      Book highestRatio = stats.findHighestRatioBook();

      if (highestRatio != null) {
          System.out.println(
              String.format(
                  "Author with the highest ratio of star reviews to text reviews: %s. %.2f",
                  highestRatio.getAuthors(),
                  highestRatio.ratingsReviewsRatio()
              )
          );
      }
    }

    public void printAuthorWithHighestAverageRating() {
        // index all the books by author
        Map<String, List<Book>> indexed = stats.indexByAuthor();
        // from that index find the author with the highest average rating
        Map.Entry<String, Double> highestAverageRating = stats.findAuthorWithHighestAverageRating(indexed);

        if (highestAverageRating != null) {
            System.out.println(
                String.format(
                    "Author with the highest average rating of all their books: %s. %.2f",
                    highestAverageRating.getKey(),
                    highestAverageRating.getValue()
                )
            );
        }
    }
}
