package com.example.dclassicsbooks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.dclassicsbooks.data.AppData;
import com.example.dclassicsbooks.model.Book;
import com.google.android.material.navigation.NavigationView;

public class BooksActivity extends AppCompatActivity {

    private static final float BOOK_COVER_ZOOM = 1.0f;
    private static final long TOP_BOOK_CAROUSEL_DELAY = 3600L;

    private final Handler carouselHandler = new Handler(Looper.getMainLooper());

    private DrawerLayout drawerLayout;
    private ImageButton btnMenu;
    private LinearLayout booksGrid;
    private LinearLayout topBookDots;
    private NavigationView navView;
    private TextView fictionTab;
    private TextView nonFictionTab;
    private TextView tvGreetingName;
    private ViewFlipper topBookFlipper;

    private boolean showingFiction = true;
    private int topBookIndex = 0;

    private final Book[] topBooks = {
            new Book(R.drawable.img_the_picture, "The Picture of Dorian Gray", "Oscar Wilde", "17.4k", "4.4", Book.CATEGORY_FICTION),
            new Book(R.drawable.img_moby_dick, "Moby-Dick", "Herman Melville", "30.1k", "4.9", Book.CATEGORY_FICTION),
            new Book(R.drawable.img_atomic_habits, "Atomic Habits", "James Clear", "54.1k", "4.9", Book.CATEGORY_NON_FICTION)
    };

    private final Runnable topBookAutoSlide = new Runnable() {
        @Override
        public void run() {
            moveTopBookCarousel(true);
            carouselHandler.postDelayed(this, TOP_BOOK_CAROUSEL_DELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        bindViews();
        setupHeader();
        setupDrawer();
        setupTopBookCarousel();
        setupTabs();
        renderSelectedTab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTopBookCarousel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTopBookCarousel();
    }

    private void bindViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        btnMenu = findViewById(R.id.btnMenu);
        booksGrid = findViewById(R.id.booksGrid);
        topBookDots = findViewById(R.id.topBookDots);
        navView = findViewById(R.id.navView);
        fictionTab = findViewById(R.id.tabFiction);
        nonFictionTab = findViewById(R.id.tabNonFiction);
        tvGreetingName = findViewById(R.id.tvGreetingName);
        topBookFlipper = findViewById(R.id.topBookFlipper);
    }

    private void setupHeader() {
        SharedPreferences sharedPreferences = getSharedPreferences("GlobalVars", MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "Book Lover");
        tvGreetingName.setText(username);

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupDrawer() {
        View headerView = navView.getHeaderView(0);
        ImageButton closeButton = headerView.findViewById(R.id.btnCloseDrawer);
        closeButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(BooksActivity.this, HomeActivity.class));
                finish();
            } else if (id == R.id.nav_store) {
                startActivity(new Intent(BooksActivity.this, StoresActivity.class));
            } else if (id == R.id.nav_logout) {
                getSharedPreferences("GlobalVars", MODE_PRIVATE).edit().clear().apply();
                startActivity(new Intent(BooksActivity.this, LoginActivity.class));
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupTopBookCarousel() {
        topBookFlipper.removeAllViews();
        topBookFlipper.setInAnimation(this, android.R.anim.fade_in);
        topBookFlipper.setOutAnimation(this, android.R.anim.fade_out);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Book topBook : topBooks) {
            View slide = inflater.inflate(R.layout.item_top_book, topBookFlipper, false);
            bindTopBookSlide(slide, topBook);
            topBookFlipper.addView(slide);
        }

        bindTopBookDots();
    }

    private void bindTopBookSlide(View slide, Book topBook) {
        ImageView cover = slide.findViewById(R.id.imgTopBookCover);
        TextView title = slide.findViewById(R.id.tvTopBookTitle);
        TextView author = slide.findViewById(R.id.tvTopBookAuthor);
        TextView description = slide.findViewById(R.id.tvTopBookDescription);

        cover.setImageResource(topBook.imageRes);
        applyBookCoverZoom(cover);
        title.setText(topBook.title);
        author.setText(topBook.author);
        description.setText(getTopBookDescription(topBook));
        slide.setOnClickListener(v -> openBookDetail(topBook));
    }

    private String getTopBookDescription(Book book) {
        if (book.title.equals("The Picture of Dorian Gray")) {
            return "The Picture of Dorian Gray is a novel about a young man who stays forever young while a mysterious portrait ages and reveals his secret.";
        } else if (book.title.equals("Moby-Dick")) {
            return "A sailor joins Captain Ahab's hunt for the white whale and discovers obsession, danger, and the sea's dark pull.";
        } else if (book.title.equals("Atomic Habits")) {
            return "Atomic Habits shares small practical changes that help build better routines and break unhelpful habits.";
        }

        return book.title + " is one of the highlighted books in D'Classics Books.";
    }

    private void setupTabs() {
        fictionTab.setOnClickListener(v -> {
            showingFiction = true;
            renderSelectedTab();
        });

        nonFictionTab.setOnClickListener(v -> {
            showingFiction = false;
            renderSelectedTab();
        });
    }

    private void renderSelectedTab() {
        updateTabStyle(fictionTab, showingFiction);
        updateTabStyle(nonFictionTab, !showingFiction);

        Book[] selectedBooks = showingFiction ? AppData.getFictionBooks() : AppData.getNonFictionBooks();
        renderBookGrid(selectedBooks);
    }

    private void updateTabStyle(TextView tab, boolean active) {
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(dp(8));
        background.setColor(active ? getColor(R.color.primary_color) : Color.rgb(246, 243, 234));
        tab.setBackground(background);
        tab.setTextColor(active ? Color.WHITE : getColor(R.color.dark_text));
    }

    private void renderBookGrid(Book[] books) {
        booksGrid.removeAllViews();

        for (int i = 0; i < books.length; i += 2) {
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.TOP);
            row.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            rowParams.bottomMargin = dp(22);
            booksGrid.addView(row, rowParams);

            row.addView(createBookItem(books[i], true));
            if (i + 1 < books.length) {
                row.addView(createBookItem(books[i + 1], false));
            } else {
                View spacer = new View(this);
                row.addView(spacer, new LinearLayout.LayoutParams(0, 1, 1f));
            }
        }
    }

    private View createBookItem(Book book, boolean leftColumn) {
        View item = LayoutInflater.from(this).inflate(R.layout.item_book_card, booksGrid, false);

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        itemParams.setMarginEnd(leftColumn ? dp(13) : 0);
        itemParams.setMarginStart(leftColumn ? 0 : dp(13));
        item.setLayoutParams(itemParams);

        ImageView cover = item.findViewById(R.id.imgBookCover);
        TextView title = item.findViewById(R.id.tvBookTitle);
        TextView author = item.findViewById(R.id.tvBookAuthor);
        TextView meta = item.findViewById(R.id.tvBookMeta);

        cover.setImageResource(book.imageRes);
        applyBookCoverZoom(cover);
        title.setText(book.title);
        author.setText(book.author);
        meta.setText(book.views + " views | " + book.rating);
        item.setOnClickListener(v -> openBookDetail(book));

        return item;
    }

    private void moveTopBookCarousel(boolean forward) {
        if (topBookFlipper == null || topBooks.length <= 1) {
            return;
        }

        topBookFlipper.setInAnimation(this, android.R.anim.fade_in);
        topBookFlipper.setOutAnimation(this, android.R.anim.fade_out);

        if (forward) {
            topBookFlipper.showNext();
        } else {
            topBookFlipper.showPrevious();
        }

        topBookIndex = topBookFlipper.getDisplayedChild();
        bindTopBookDots();
    }

    private void bindTopBookDots() {
        topBookDots.removeAllViews();

        for (int i = 0; i < topBooks.length; i++) {
            boolean active = i == topBookIndex;
            View dot = new View(this);
            GradientDrawable dotShape = new GradientDrawable();
            dotShape.setColor(active ? getColor(R.color.secondary_color) : Color.rgb(226, 222, 212));
            dotShape.setCornerRadius(dp(4));
            dot.setBackground(dotShape);

            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(
                    active ? dp(24) : dp(7),
                    dp(5)
            );
            dotParams.setMargins(dp(3), dp(4), dp(3), dp(4));
            topBookDots.addView(dot, dotParams);
        }
    }

    private void startTopBookCarousel() {
        stopTopBookCarousel();
        if (topBookFlipper != null && topBooks.length > 1) {
            carouselHandler.postDelayed(topBookAutoSlide, TOP_BOOK_CAROUSEL_DELAY);
        }
    }

    private void stopTopBookCarousel() {
        carouselHandler.removeCallbacks(topBookAutoSlide);
    }

    private void openBookDetail(Book book) {
        startActivity(BookDetailActivity.newIntent(this, book));
    }

    private void applyBookCoverZoom(ImageView imageView) {
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        imageView.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
                updateBookCoverMatrix(imageView)
        );
        imageView.post(() -> updateBookCoverMatrix(imageView));
    }

    private void updateBookCoverMatrix(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable == null || imageView.getWidth() == 0 || imageView.getHeight() == 0) {
            return;
        }

        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        if (drawableWidth <= 0 || drawableHeight <= 0) {
            return;
        }

        float viewWidth = imageView.getWidth();
        float viewHeight = imageView.getHeight();
        float scale = Math.max(viewWidth / drawableWidth, viewHeight / drawableHeight) * BOOK_COVER_ZOOM;
        float dx = (viewWidth - drawableWidth * scale) / 2f;
        float dy = (viewHeight - drawableHeight * scale) / 2f;

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, dy);
        imageView.setImageMatrix(matrix);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
