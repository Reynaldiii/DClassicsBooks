package com.example.dclassicsbooks.data;

import com.example.dclassicsbooks.R;
import com.example.dclassicsbooks.model.Book;
import com.example.dclassicsbooks.model.Store;

public final class AppData {

    private static final int[] TOP_BOOK_IMAGES = {
            R.drawable.img_wuthering,
            R.drawable.img_the_great,
            R.drawable.img_crime,
            R.drawable.img_the_picture,
            R.drawable.img_pride
    };

    private static final Book[] ALL_BOOKS = {
            new Book(R.drawable.img_laskar, "Laskar Pelangi", "Andrea Hirata", "44.3k", "4.9", Book.CATEGORY_FICTION),
            new Book(R.drawable.img_moby_dick, "Moby-Dick", "Herman Melville", "30.1k", "4.9", Book.CATEGORY_FICTION),
            new Book(R.drawable.img_mockingbird, "To Kill a Mockingbird", "Harper Lee", "39.3k", "4.7", Book.CATEGORY_FICTION),
            new Book(R.drawable.img_pride_and_prejudice, "Pride and Prejudice", "Jane Austen", "40.5k", "4.6", Book.CATEGORY_FICTION),
            new Book(R.drawable.img_the_catcher, "The Catcher in the Rye", "J. D. Salinger", "31.7k", "4.4", Book.CATEGORY_FICTION),
            new Book(R.drawable.img_norwegian, "Norwegian Wood", "Haruki Murakami", "30.9k", "4.3", Book.CATEGORY_FICTION),
            new Book(R.drawable.img_atomic_habits, "Atomic Habits", "James Clear", "54.1k", "4.9", Book.CATEGORY_NON_FICTION),
            new Book(R.drawable.img_rich, "Rich Dad Poor Dad", "Robert Kiyosaki", "40.3k", "4.6", Book.CATEGORY_NON_FICTION),
            new Book(R.drawable.img_filosofi, "Filosofi Teras", "Henry Manampiring", "59.3k", "4.9", Book.CATEGORY_NON_FICTION),
            new Book(R.drawable.img_educated, "Educated", "Tara Westover", "30.5k", "4.3", Book.CATEGORY_NON_FICTION),
            new Book(R.drawable.img_thinking, "Thinking, Fast and Slow", "Daniel Kahneman", "31.8k", "4.5", Book.CATEGORY_NON_FICTION),
            new Book(R.drawable.img_psychology, "The Psychology of Money", "Morgan Housel", "49.9k", "4.7", Book.CATEGORY_NON_FICTION)
    };

    private static final Store[] STORES = {
            new Store(R.drawable.img_store_royal, "The Royal Library, London", "24 Regent St, London W1B 5TR, UK", "Open - Close at 21.00", "Mon - Fri: 08.00 - 21.00", "Sat - Sun: 10.00 - 20.00"),
            new Store(R.drawable.img_store_british, "The British Library, London", "96 Euston Rd, London NW1 2DB, UK", "Open - Close at 20.00", "Mon - Fri: 08.00 - 20.00", "Sat - Sun: 10.00 - 20.00"),
            new Store(R.drawable.img_store_bodleian, "Bodleian Library, Oxford", "Broad St, Oxford OX1 3BG, UK", "Open - Close at 21.00", "Mon - Sat: 08.00 - 21.00", "Sun: Closed"),
            new Store(R.drawable.img_store_trinity, "Trinity College Library, Dublin", "College Green, Dublin 2, D02 PN40, Ireland", "Open - Close at 22.00", "Mon - Fri: 10.00 - 22.00", "Sat - Sun: 10.00 - 20.00"),
            new Store(R.drawable.img_store_ny, "NY Public Library, New York", "476 5th Ave, New York, NY 10018, USA", "Open - Close at 21.00", "Mon - Fri: 09.00 - 21.00", "Sat - Sun: 10.00 - 21.00")
    };

    private AppData() {
    }

    public static int[] getTopBookImages() {
        return TOP_BOOK_IMAGES;
    }

    public static Book[] getFeaturedBooks() {
        return new Book[] {
                new Book(R.drawable.img_the_great_gatsby2, "The Great Gatsby", "F. Scott Fitzgerald", "24.3k", "4.8", Book.CATEGORY_FICTION),
                new Book(R.drawable.img_moby_dick, "Moby-Dick", "Herman Melville", "30.1k", "4.9", Book.CATEGORY_FICTION),
                new Book(R.drawable.img_1984, "1984", "George Orwell", "29.3k", "4.7", Book.CATEGORY_FICTION),
                new Book(R.drawable.img_pride_and_prejudice, "Pride and Prejudice", "Jane Austen", "40.5k", "4.6", Book.CATEGORY_FICTION)
        };
    }

    public static Book[] getAllBooks() {
        return ALL_BOOKS;
    }

    public static Book[] getFictionBooks() {
        return filterBooksByCategory(Book.CATEGORY_FICTION);
    }

    public static Book[] getNonFictionBooks() {
        return filterBooksByCategory(Book.CATEGORY_NON_FICTION);
    }

    public static Store[] getStores() {
        return STORES;
    }

    private static Book[] filterBooksByCategory(String category) {
        int count = 0;
        for (Book book : ALL_BOOKS) {
            if (book.category.equals(category)) {
                count++;
            }
        }

        Book[] filteredBooks = new Book[count];
        int index = 0;
        for (Book book : ALL_BOOKS) {
            if (book.category.equals(category)) {
                filteredBooks[index] = book;
                index++;
            }
        }

        return filteredBooks;
    }
}
