package com.patientping;

import com.patientping.BookStats;
import com.patientping.DisplayBookStats;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        BookStats bookStats = new BookStats();
        bookStats.load("/books.csv");

        DisplayBookStats printer = new DisplayBookStats(bookStats);

        printer.printNumBooksLoaded();
        printer.printNumEnglishBooks();
        printer.printAuthorWithMostEnglishBooks();
        printer.printHighestRatedBookAuthor();
        printer.printHighestRatioBookAuthor();
        printer.printAuthorWithHighestAverageRating();
    }
}