import com.patientping.Book;
import com.patientping.BookStats;
import com.patientping.DisplayBookStats;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDisplayBookStats {
    private final ByteArrayOutputStream stdOut = new ByteArrayOutputStream();
    private final PrintStream originalStdOut = System.out;

    public void captureStdOut() {
        System.setOut(new PrintStream(stdOut));
    }

    public void resetStdOut() {
        System.setOut(originalStdOut);
    }

    @Test
    public void testPrintNumBooksLoaded() {
        // create a fake list of 2 books
        List<Book> twoBooks = Arrays.asList(Book.builder().build(), Book.builder().build());

        BookStats stats = Mockito.mock(BookStats.class);

        Mockito.doReturn(twoBooks).when(stats).getBooks();

        DisplayBookStats printer = new DisplayBookStats(stats);

        captureStdOut();
        printer.printNumBooksLoaded();
        resetStdOut();

        assertEquals("Loaded 2 books\n", stdOut.toString());
    }

    @Test
    public void testPrintNumEnglishBooks() {
        // create a fake list of 2 english books
        List<Book> twoEnglishBooks = Arrays.asList(Book.builder().build(), Book.builder().build());

        BookStats stats = Mockito.mock(BookStats.class);

        Mockito.doReturn(twoEnglishBooks).when(stats).filterToEnglishBooks();

        DisplayBookStats printer = new DisplayBookStats(stats);

        captureStdOut();
        printer.printNumEnglishBooks();
        resetStdOut();

        assertEquals("2 Books in English\n", stdOut.toString());
    }

    @Test
    public void testPrintAuthorWithMostEnglishBooks() {
        // create a fake list of 2 english book
        List<Book> twoEnglishBooks = Arrays.asList(Book.builder().build(), Book.builder().build());
        // create a fake index of books by author
        Map<String, List<Book>> indexed = new HashMap<String, List<Book>>();
        // add a fake entry into the fake index
        indexed.put("English Author", twoEnglishBooks);

        BookStats stats = Mockito.mock(BookStats.class);

        // mock filterToEnglishBooks to return fake list
        Mockito.when(stats.filterToEnglishBooks()).thenReturn(twoEnglishBooks);
        // mock indexByAuthor to return a fake index when passed the twoEnglishBooks list
        Mockito.when(stats.indexByAuthor(twoEnglishBooks)).thenReturn(indexed);
        // call the real method to return the author with the most english books from the mock index
        Mockito.when(stats.findMostBooksByAuthor(indexed)).thenCallRealMethod();

        DisplayBookStats printer = new DisplayBookStats(stats);

        captureStdOut();
        printer.printAuthorWithMostEnglishBooks();
        resetStdOut();

        assertEquals("Author with the most english books: English Author. 2 books.\n", stdOut.toString());
    }

    @Test
    public void testPrintAuthorWithMostEnglishBooksNotFound() {
        Map<String, List<Book>> indexed = new HashMap<String, List<Book>>();

        BookStats stats = Mockito.mock(BookStats.class);

        Mockito.when(stats.findMostBooksByAuthor(indexed)).thenCallRealMethod();

        DisplayBookStats printer = new DisplayBookStats(stats);

        captureStdOut();
        printer.printAuthorWithMostEnglishBooks();
        resetStdOut();

        assertEquals("", stdOut.toString());
    }

    @Test
    public void testprintHighestRatedBookAuthor() {
        // create a fake book of the highest rated author
        Book highestRated = Book.builder().authors("Highly Rated Author").averageRating(4.5f).build();

        BookStats stats = Mockito.mock(BookStats.class);

        // mock findHighestRatedBook to return a fake book
        Mockito.doReturn(highestRated).when(stats).findHighestRatedBook();

        DisplayBookStats printer = new DisplayBookStats(stats);

        captureStdOut();
        printer.printHighestRatedBookAuthor();
        resetStdOut();

        assertEquals("Author with the highest rating: Highly Rated Author. 4.50 stars.\n", stdOut.toString());
    }

    @Test
    public void testprintHighestRatedBookAuthorNotFound() {
        BookStats stats = Mockito.mock(BookStats.class);

        // mock findHighestRatedBook to return a fake book
        Mockito.doReturn(null).when(stats).findHighestRatedBook();

        DisplayBookStats printer = new DisplayBookStats(stats);

        captureStdOut();
        printer.printHighestRatedBookAuthor();
        resetStdOut();

        assertEquals("", stdOut.toString());
    }

    @Test
    public void testprintHighestRatioBookAuthor() {
        // create a fake book of the highest ratio author
        Book highestRatio = Book.builder().authors("High Ratio Author").ratingCount(40).textReviewsCount(20).build();

        BookStats stats = Mockito.mock(BookStats.class);

        // mock findHighestRatioBook to return a fake book
        Mockito.doReturn(highestRatio).when(stats).findHighestRatioBook();

        DisplayBookStats printer = new DisplayBookStats(stats);

        captureStdOut();
        printer.printHighestRatioBookAuthor();
        resetStdOut();

        assertEquals("Author with the highest ratio of star reviews to text reviews: High Ratio Author. 2.00\n", stdOut.toString());
    }

    @Test
    public void testprintHighestRatioBookAuthorNotFound() {
        BookStats stats = Mockito.mock(BookStats.class);

        // mock findHighestRatioBook to return a fake book
        Mockito.doReturn(null).when(stats).findHighestRatioBook();

        DisplayBookStats printer = new DisplayBookStats(stats);

        captureStdOut();
        printer.printHighestRatioBookAuthor();
        resetStdOut();

        assertEquals("", stdOut.toString());
    }

    @Test
    public void testprintAuthorWithHighestAverageRating() {
        // create a fake list of 2 books
        List<Book> oneBook = Arrays.asList(
          Book.builder().averageRating(5).build()
        );

        // create a fake index of books by author
        Map<String, List<Book>> indexed = new HashMap<String, List<Book>>();
        // add a fake entry into the fake index
        indexed.put("Highest Rated Author", oneBook);

        BookStats stats = Mockito.mock(BookStats.class);

        // mock indexByAuthor to return a mock index when passed the oneBook list
        Mockito.when(stats.indexByAuthor()).thenReturn(indexed);
        // call the real method to return the author with highest average from the mock index
        Mockito.when(stats.findAuthorWithHighestAverageRating(indexed)).thenCallRealMethod();

        DisplayBookStats printer = new DisplayBookStats(stats);

        captureStdOut();
        printer.printAuthorWithHighestAverageRating();
        resetStdOut();

        assertEquals("Author with the highest average rating of all their books: Highest Rated Author. 5.00\n", stdOut.toString());
    }

    @Test
    public void testprintAuthorWithHighestAverageRatingNotFound() {
        Map<String, List<Book>> indexed = new HashMap<String, List<Book>>();

        BookStats stats = Mockito.mock(BookStats.class);

        // call the real method to return the author with highest average from the mock index
        Mockito.when(stats.findAuthorWithHighestAverageRating(indexed)).thenCallRealMethod();

        DisplayBookStats printer = new DisplayBookStats(stats);

        captureStdOut();
        printer.printAuthorWithHighestAverageRating();
        resetStdOut();

        assertEquals("", stdOut.toString());
    }
}
