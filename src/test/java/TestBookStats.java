import com.patientping.Book;
import com.patientping.BookStats;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBookStats {
    private final ByteArrayOutputStream stdErr = new ByteArrayOutputStream();
    private final PrintStream originalStdErr = System.err;

    public void captureStdErr() {
        System.setErr(new PrintStream(stdErr));
    }

    public void resetStdErr() {
        System.setErr(originalStdErr);
    }

    @Test
    public void testLoadCsv() throws IOException {
        String inputData =
                "bookID,title,authors,average_rating,isbn,isbn13,language_code,# num_pages,ratings_count,text_reviews_count\n" +
                "14428,The Inheritors,William Golding,3.53,0156443791,9780156443791,en-US,240,2681,257\n"
                + "2386,Moby Dick,Herman Melville-William Hootkins,3.49,9626343583,9789626343586,eng,25,66,17\n";

        InputStream inputStream = new ByteArrayInputStream(inputData.getBytes(Charset.forName("UTF-8")));
        BookStats stats = new BookStats();
        stats.loadCsv(inputStream);

        List<Book> books = stats.getBooks();

        // We should expect 2 books in the list
        assertEquals(2, books.size());

        // check all the properties of the first book to verify all data is parsed correctly
        assertEquals(14428, books.get(0).getId());
        assertEquals("The Inheritors", books.get(0).getTitle());
        assertEquals("William Golding", books.get(0).getAuthors());
        assertEquals(3.53, books.get(0).getAverageRating(), .001);
        assertEquals("0156443791", books.get(0).getIsbn());
        assertEquals("9780156443791", books.get(0).getIsbn13());
        assertEquals("en-US", books.get(0).getLanguage());
        assertEquals(240, books.get(0).getNumPages());
        assertEquals(2681, books.get(0).getRatingCount());
        assertEquals(257, books.get(0).getTextReviewsCount());

        // check that the second book was parsed (no need to recheck all the properties)
        assertEquals(2386, books.get(1).getId());
    }

    @Test
    public void testLoadCsvWithBadTypeConversion() throws IOException {
        String inputData =
                "bookID,title,authors,average_rating,isbn,isbn13,language_code,# num_pages,ratings_count,text_reviews_count\n" +
                "not-a-number,The Inheritors,William Golding,3.53,0156443791,9780156443791,en-US,240,2681,257\n"
                + "2386,Moby Dick,Herman Melville-William Hootkins,3.49,9626343583,9789626343586,eng,25,66,17\n";
        InputStream inputStream = new ByteArrayInputStream(inputData.getBytes(Charset.forName("UTF-8")));
        BookStats stats = new BookStats();

        captureStdErr();
        stats.loadCsv(inputStream);
        resetStdErr();

        List<Book> books = stats.getBooks();

        assertEquals(1, books.size());

        // check that only the second book was parsed (first one skipped due to bad id)
        assertEquals(2386, books.get(0).getId());

        // check that we print out the exception to stderr
        assertEquals("java.lang.NumberFormatException: For input string: \"not-a-number\"\n", stdErr.toString());
    }

    @Test
    public void testFilterToEnglishBooks() {
        List<Book> books = new ArrayList<Book>();
        books.add(Book.builder().title("British English").language("en-GB").build());
        books.add(Book.builder().title("Japanese").language("jpn").build());
        books.add(Book.builder().title("American English").language("en-US").build());
        books.add(Book.builder().title("English").language("eng").build());

        BookStats stats = new BookStats();

        List<Book> englishBooks = stats.filterToEnglishBooks(books);

        // should only be 3 english books
        assertEquals(3, englishBooks.size());

        // check that the correct 3 books were selected
        assertEquals("en-GB", englishBooks.get(0).getLanguage());
        assertEquals("en-US", englishBooks.get(1).getLanguage());
        assertEquals("eng", englishBooks.get(2).getLanguage());
    }

    @Test
    public void testIndexByAuthor() {
        List<Book> books = new ArrayList<Book>();
        books.add(Book.builder().authors("Arthur C. Clarke").build());
        books.add(Book.builder().authors("Arthur C. Clarke-Gentry Lee").build());
        books.add(Book.builder().authors("Gene Wolfe").build());
        books.add(Book.builder().authors("Gene Wolfe").build());
        books.add(Book.builder().authors("Stanislaw Lem").build());

        BookStats stats = new BookStats();

        Map<String, List<Book>> byAuthor = stats.indexByAuthor(books);

        assertEquals(2, byAuthor.get("Gene Wolfe").size());
        assertEquals(2, byAuthor.get("Arthur C. Clarke").size());
        assertEquals(1, byAuthor.get("Stanislaw Lem").size());
        assertEquals(1, byAuthor.get("Gentry Lee").size());
    }

    @Test
    public void testFindMostBooksByAuthor() {
      List<Book> wolfe = new ArrayList<Book>();
      wolfe.add(Book.builder().authors("Gene Wolfe").build());
      wolfe.add(Book.builder().authors("Gene Wolfe").build());

      List<Book> clarke = new ArrayList<Book>();
      clarke.add(Book.builder().authors("Arthur C. Clarke").build());

      Map<String, List<Book>> byAuthor = new HashMap<String, List<Book>>();
      byAuthor.put("Gene Wolfe", wolfe);
      byAuthor.put("Arthur C. Clarke", clarke);

      BookStats stats = new BookStats();

      Map.Entry<String, Integer> mostBooks = stats.findMostBooksByAuthor(byAuthor);

      assertEquals("Gene Wolfe", mostBooks.getKey());
      assertEquals(2, mostBooks.getValue());
    }

    @Test
    public void testFindMostBooksByAuthorEmptyList() {
      Map<String, List<Book>> byAuthor = new HashMap<String, List<Book>>();

      BookStats stats = new BookStats();

      Map.Entry<String, Integer> mostBooks = stats.findMostBooksByAuthor(byAuthor);

      assertEquals(null, mostBooks);
    }

    @Test
    public void testFindAuthorWithHighestAverageRating() {
      List<Book> wolfe = new ArrayList<Book>();
      wolfe.add(Book.builder().authors("Gene Wolfe").averageRating(3).build());
      wolfe.add(Book.builder().authors("Gene Wolfe").averageRating(5).build());

      List<Book> clarke = new ArrayList<Book>();
      clarke.add(Book.builder().authors("Arthur C. Clarke").averageRating(3).build());

      Map<String, List<Book>> byAuthor = new HashMap<String, List<Book>>();
      byAuthor.put("Gene Wolfe", wolfe);
      byAuthor.put("Arthur C. Clarke", clarke);

      BookStats stats = new BookStats();

      Map.Entry<String, Double> highestAverage = stats.findAuthorWithHighestAverageRating(byAuthor);

      assertEquals("Gene Wolfe", highestAverage.getKey());
      assertEquals(4.00, highestAverage.getValue());
    }

    @Test
    public void testFindAuthorWithHighestAverageRatingEmptyList() {
      Map<String, List<Book>> byAuthor = new HashMap<String, List<Book>>();

      BookStats stats = new BookStats();

      Map.Entry<String, Double> highestAverage = stats.findAuthorWithHighestAverageRating(byAuthor);

      assertEquals(null, highestAverage);
    }

    @Test
    public void testFindHighestRatedBook() {
      List<Book> books = new ArrayList<Book>();
      books.add(Book.builder().averageRating(4.27f).ratingCount(99).title("Rendezvous With Rama").build());
      books.add(Book.builder().averageRating(4.95f).ratingCount(25).title("Book of the New Sun").build());
      books.add(Book.builder().averageRating(4.45f).ratingCount(33).title("The Star Diaries").build());

      BookStats stats = new BookStats();
      Book highestRating = stats.findHighestRatedBook(books);

      assertEquals("Book of the New Sun", highestRating.getTitle());
    }

    @Test
    public void testFindHighestRatedBookSkipsCountLessThan25() {
      List<Book> books = new ArrayList<Book>();
      books.add(Book.builder().averageRating(4.27f).ratingCount(99).title("Rendezvous With Rama").build());
      books.add(Book.builder().averageRating(4.95f).ratingCount(24).title("Book of the New Sun").build());
      books.add(Book.builder().averageRating(4.45f).ratingCount(33).title("The Star Diaries").build());

      BookStats stats = new BookStats();
      Book highestRating = stats.findHighestRatedBook(books);

      assertEquals("The Star Diaries", highestRating.getTitle());
    }

    @Test
    public void testfindHighestRatioBook() {
      List<Book> books = new ArrayList<Book>();
      books.add(Book.builder().ratingCount(35).textReviewsCount(31).title("Rendezvous With Rama").build());
      books.add(Book.builder().ratingCount(25).textReviewsCount(1).title("Book of the New Sun").build());
      books.add(Book.builder().ratingCount(33).textReviewsCount(25).title("The Star Diaries").build());

      BookStats stats = new BookStats();
      Book highestRatio = stats.findHighestRatioBook(books);

      assertEquals("Book of the New Sun", highestRatio.getTitle());
    }

    @Test
    public void testfindHighestRatioBookSkipsCountsLessThan25() {
      List<Book> books = new ArrayList<Book>();
      books.add(Book.builder().ratingCount(35).textReviewsCount(31).title("Rendezvous With Rama").build());
      books.add(Book.builder().ratingCount(24).textReviewsCount(1).title("Book of the New Sun").build());
      books.add(Book.builder().ratingCount(33).textReviewsCount(25).title("The Star Diaries").build());

      BookStats stats = new BookStats();
      Book highestRatio = stats.findHighestRatioBook(books);

      assertEquals("The Star Diaries", highestRatio.getTitle());
    }

    @Test
    public void testfindHighestRatioBookDivideByZero() {
        List<Book> books = new ArrayList<Book>();
        books.add(Book.builder().ratingCount(35).textReviewsCount(31).title("Rendezvous With Rama").build());
        books.add(Book.builder().ratingCount(25).textReviewsCount(0).title("Book of the New Sun").build());
        books.add(Book.builder().ratingCount(33).textReviewsCount(25).title("The Star Diaries").build());

        BookStats stats = new BookStats();
        Book highestRatio = stats.findHighestRatioBook(books);

        assertEquals("The Star Diaries", highestRatio.getTitle());
    }
}

