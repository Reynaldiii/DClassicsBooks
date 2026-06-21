package com.example.dclassicsbooks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dclassicsbooks.model.Book;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class BookDetailActivity extends AppCompatActivity {

    private static final String EXTRA_IMAGE_RES = "extra_image_res";
    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_AUTHOR = "extra_author";
    private static final String EXTRA_VIEWS = "extra_views";
    private static final String EXTRA_RATING = "extra_rating";
    private static final String EXTRA_CATEGORY = "extra_category";

    private ImageView imgHeroBook;
    private ImageView imgBookCover;
    private TextView tvGreetingName;
    private TextView tvBookTitle;
    private TextView tvBookAuthor;
    private TextView tvBookMeta;
    private TextView tvBookDescription;
    private TextInputEditText etAddress;
    private TextInputEditText etPhone;
    private MaterialButton btnBuy;

    public static Intent newIntent(Context context, Book book) {
        Intent intent = new Intent(context, BookDetailActivity.class);
        intent.putExtra(EXTRA_IMAGE_RES, book.imageRes);
        intent.putExtra(EXTRA_TITLE, book.title);
        intent.putExtra(EXTRA_AUTHOR, book.author);
        intent.putExtra(EXTRA_VIEWS, book.views);
        intent.putExtra(EXTRA_RATING, book.rating);
        intent.putExtra(EXTRA_CATEGORY, book.category);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        bindViews();
        setupHeader();
        bindBookDetail();
        btnBuy.setOnClickListener(v -> processPurchase());
    }

    private void bindViews() {
        imgHeroBook = findViewById(R.id.imgHeroBook);
        imgBookCover = findViewById(R.id.imgBookCover);
        tvGreetingName = findViewById(R.id.tvGreetingName);
        tvBookTitle = findViewById(R.id.tvBookTitle);
        tvBookAuthor = findViewById(R.id.tvBookAuthor);
        tvBookMeta = findViewById(R.id.tvBookMeta);
        tvBookDescription = findViewById(R.id.tvBookDescription);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        btnBuy = findViewById(R.id.btnBuy);
    }

    private void setupHeader() {
        SharedPreferences sharedPreferences = getSharedPreferences("GlobalVars", MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "Book Lover");
        tvGreetingName.setText(username);

        ImageButton btnMenu = findViewById(R.id.btnMenu);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnMenu.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());
    }

    private void bindBookDetail() {
        Intent intent = getIntent();
        int imageRes = intent.getIntExtra(EXTRA_IMAGE_RES, R.drawable.img_the_great_gatsby2);
        String title = intent.getStringExtra(EXTRA_TITLE);
        String author = intent.getStringExtra(EXTRA_AUTHOR);
        String views = intent.getStringExtra(EXTRA_VIEWS);
        String rating = intent.getStringExtra(EXTRA_RATING);
        String category = intent.getStringExtra(EXTRA_CATEGORY);

        if (title == null) {
            title = "The Great Gatsby";
        }
        if (author == null) {
            author = "F. Scott Fitzgerald";
        }
        if (views == null) {
            views = "24.3k";
        }
        if (rating == null) {
            rating = "4.8";
        }
        if (category == null) {
            category = Book.CATEGORY_FICTION;
        }

        imgHeroBook.setImageResource(imageRes);
        imgBookCover.setImageResource(imageRes);
        tvBookTitle.setText(title);
        tvBookAuthor.setText(author);
        tvBookMeta.setText(views + " views | " + rating);
        tvBookDescription.setText(getBookDescription(title, author, category));
    }

    private void processPurchase() {
        String address = getTextValue(etAddress);
        String phone = getTextValue(etPhone);
        String normalizedPhone = phone.replace(" ", "").replace("-", "");
        if (normalizedPhone.startsWith("+")) {
            normalizedPhone = normalizedPhone.substring(1);
        }

        if (address.isEmpty()) {
            showErrorDialog("Address Required", "Address must be filled.");
            return;
        }

        if (phone.isEmpty()) {
            showErrorDialog("Phone Number Required", "Phone number must be filled.");
            return;
        }

        if (!normalizedPhone.matches("[0-9]+")) {
            showErrorDialog("Invalid Phone Number", "Phone number must be numeric.");
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Purchase Submitted")
                .setMessage("A confirmation email has been sent to your email.")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(BookDetailActivity.this, BooksActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    private String getTextValue(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }

    private void showErrorDialog(String title, String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private String getBookDescription(String title, String author, String category) {
        if (title.equals("Rich Dad Poor Dad")) {
            return "The financial philosophies of two father figures reveal practical lessons about money, education, work, and building wealth over time.";
        } else if (title.equals("Atomic Habits")) {
            return "A practical guide to building better habits through small changes, clear systems, and consistent progress every day.";
        } else if (title.equals("Moby-Dick")) {
            return "A sailor joins Captain Ahab's hunt for the white whale and discovers obsession, danger, and the deep pull of the sea.";
        } else if (title.equals("Pride and Prejudice")) {
            return "A classic story about love, family, manners, and first impressions in a society shaped by reputation and expectation.";
        }

        return title + " by " + author + " is a selected " + category.toLowerCase() + " book from D'Classics Books for readers who enjoy memorable literature.";
    }
}
