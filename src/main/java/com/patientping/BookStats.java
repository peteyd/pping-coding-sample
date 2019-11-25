package com.patientping;

import com.patientping.CSVParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap;
import lombok.Getter;

@Getter
public class BookStats {
    private List<Book> books;

    public void loadCsv(InputStream in) throws IOException {
        books = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        // read the header line to created the index of column names
        CSVParser.readHeader(reader.readLine());

        Map<String, String> record = CSVParser.parseLine(reader.readLine());
        while (record != null) {
            if (record.size() > 0) {
              try {
                  // using the builder pattern, build up a book from the parsed record
                  // returned from the CSVParser
                  Book b = Book.builder()
                      .id(Integer.parseInt(record.get("bookID")))
                      .title(record.get("title"))
                      .authors(record.get("authors"))
                      .averageRating(Float.parseFloat(record.get("average_rating")))
                      .isbn(record.get("isbn"))
                      .isbn13(record.get("isbn13"))
                      .language(record.get("language_code"))
                      .numPages(Integer.parseInt(record.get("# num_pages")))
                      .ratingCount(Integer.parseInt(record.get("ratings_count")))
                      .textReviewsCount(Integer.parseInt(record.get("text_reviews_count")))
                      .build();

                  books.add(b);
              }
              catch (NumberFormatException e) {
                  System.err.println(e);
              }
            }

            record = CSVParser.parseLine(reader.readLine());
        }
    }

    public List<Book> filterToEnglishBooks() {
      return filterToEnglishBooks(books);
    }

    // return a sub list of books that match the 3 english language codes
    public List<Book> filterToEnglishBooks(List<Book> books) {
        List<String> languages = Arrays.asList("eng", "en-US", "en-GB");
        List<Book> englishBooks = new ArrayList<>();

        for (Book b : books) {
            if (languages.contains(b.getLanguage())) {
                englishBooks.add(b);
            }
        }
        return englishBooks;
    }

    public Map<String, List<Book>> indexByAuthor() {
      return indexByAuthor(books);
    }

    public Map<String, List<Book>> indexByAuthor(List<Book> books) {
        Map<String, List<Book>> booksByAuthor = new HashMap<>();
        for (Book b : books) {
            // split up the authors with the - delimiter
            String[] authors = b.getAuthors().split("-");
            for (String author : authors) {
                // initialize a new list if the author is not found in the index
                if (!booksByAuthor.containsKey(author)) {
                  booksByAuthor.put(author, new ArrayList<>());
                }
                booksByAuthor.get(author).add(b);
            }
        }
        return booksByAuthor;
    }

    // given an index of books by author, find the author that has written the most books
    // and return an Entry of <Author, Count>
    public Map.Entry<String, Integer> findMostBooksByAuthor(Map<String, List<Book>> booksByAuthor) {
        int mostCount = 0;
        String mostAuthor = null;
        for (Map.Entry<String, List<Book>> entry: booksByAuthor.entrySet()) {
            int count = entry.getValue().size();

            if (count > mostCount) {
                mostCount = count;
                mostAuthor = entry.getKey();
            }
        }

        if (mostAuthor != null) {
            return new AbstractMap.SimpleEntry<String, Integer>(mostAuthor, mostCount);
        }
        else {
            return null;
        }
    }

    // given an index of books by author, find the author that has the highest average rating
    // across all their books and return an entry of <Author, AverageRating>
    public Map.Entry<String, Double> findAuthorWithHighestAverageRating(Map<String, List<Book>> booksByAuthor) {
        double highestAverage = 0.0;
        String bestAuthor = null;
        for (Map.Entry<String, List<Book>> entry: booksByAuthor.entrySet()) {
            double average = entry.getValue()
                .stream()
                .mapToDouble(a -> a.getAverageRating())
                .average()
                .getAsDouble();

            if (average > highestAverage) {
                highestAverage = average;
                bestAuthor = entry.getKey();
            }
        }

        if (bestAuthor != null) {
            return new AbstractMap.SimpleEntry<String, Double>(bestAuthor, highestAverage);
        }
        else {
            return null;
        }
    }

    public Book findHighestRatedBook() {
      return findHighestRatedBook(books);
    }

    public Book findHighestRatedBook(List<Book> books) {
        float highestRating = 0f;
        Book highestRatedBook = null;
        for (Book b : books) {
            if (b.getRatingCount() < 25) {
                continue;
            }

            float rating = b.getAverageRating();

            if (rating > highestRating) {
                highestRating = rating;
                highestRatedBook = b;
            }
        }

        return highestRatedBook;
    }

    public Book findHighestRatioBook() {
      return findHighestRatioBook(books);
    }

    public Book findHighestRatioBook(List<Book> books) {
        float highestRatio = 0f;
        Book highestRatioBook = null;
        for (Book b : books) {
            if (b.getRatingCount() < 25 && b.getTextReviewsCount() < 25) {
                continue;
            }
            // Dividing by 0 is undefined, skip these books
            if (b.getTextReviewsCount() == 0) {
                continue;
            }

            if (b.ratingsReviewsRatio() > highestRatio) {
                highestRatio = b.ratingsReviewsRatio();
                highestRatioBook = b;
            }
        }

        return highestRatioBook;
    }

    public void load(String filepath) throws IOException {
        InputStream in = BookStats.class.getResourceAsStream(filepath);
        loadCsv(in);
    }
}
