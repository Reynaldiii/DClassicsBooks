package com.example.dclassicsbooks;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.dclassicsbooks.data.AppData;
import com.example.dclassicsbooks.model.Book;
import com.example.dclassicsbooks.model.Store;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final long TOP_CAROUSEL_DELAY = 3300L;
    private static final long STORE_CAROUSEL_DELAY = 4200L;
    private static final float BOOK_COVER_ZOOM = 1.0f;

    private final Handler carouselHandler = new Handler(Looper.getMainLooper());

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private ImageButton btnMenu;
    private ImageButton btnPrevTop;
    private ImageButton btnNextTop;
    private ImageButton btnPrevStore;
    private ImageButton btnNextStore;
    private TextView tvGreetingName;
    private FrameLayout topBookStage;
    private ViewFlipper storeFlipper;
    private LinearLayout topBookDots;
    private LinearLayout storeDots;
    private LinearLayout featuredBooksList;
    private final List<MaterialCardView> topBookCards = new ArrayList<>();

    private int topBookIndex = 0;
    private int storeIndex = 0;

    private final int[] topBookImages = AppData.getTopBookImages();
    private final Book[] featuredBooks = AppData.getFeaturedBooks();
    private final Store[] stores = AppData.getStores();

    private final Runnable topBookAutoSlide = new Runnable() {
        @Override
        public void run() {
            moveTopCarousel(true);
            carouselHandler.postDelayed(this, TOP_CAROUSEL_DELAY);
        }
    };

    private final Runnable storeAutoSlide = new Runnable() {
        @Override
        public void run() {
            moveStoreCarousel(true);
            carouselHandler.postDelayed(this, STORE_CAROUSEL_DELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bindViews();
        setupGreeting();
        setupDrawer();
        setupTopCarousel();
        setupFeaturedBooks();
        setupStoreCarousel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAutoCarousels();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoCarousels();
    }

    private void bindViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navView);
        btnMenu = findViewById(R.id.btnMenu);
        btnPrevTop = findViewById(R.id.btnPrevTop);
        btnNextTop = findViewById(R.id.btnNextTop);
        btnPrevStore = findViewById(R.id.btnPrevStore);
        btnNextStore = findViewById(R.id.btnNextStore);
        tvGreetingName = findViewById(R.id.tvGreetingName);
        topBookStage = findViewById(R.id.topBookStage);
        storeFlipper = findViewById(R.id.storeFlipper);
        topBookDots = findViewById(R.id.topBookDots);
        storeDots = findViewById(R.id.storeDots);
        featuredBooksList = findViewById(R.id.featuredBooksList);
    }

    private void setupGreeting() {
        SharedPreferences sharedPreferences = getSharedPreferences("GlobalVars", MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", "Book Lover");
        tvGreetingName.setText(username);
    }

    private void setupDrawer() {
        SharedPreferences sharedPreferences = getSharedPreferences("GlobalVars", MODE_PRIVATE);

        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        View headerView = navView.getHeaderView(0);
        ImageButton btnCloseDrawer = headerView.findViewById(R.id.btnCloseDrawer);
        btnCloseDrawer.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.nav_all_books) {
                startActivity(new Intent(HomeActivity.this, BooksActivity.class));
            } else if (id == R.id.nav_store) {
                startActivity(new Intent(HomeActivity.this, StoresActivity.class));
            } else if (id == R.id.nav_logout) {
                sharedPreferences.edit().clear().apply();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupTopCarousel() {
        topBookStage.removeAllViews();
        topBookCards.clear();

        for (int topBookImage : topBookImages) {
            MaterialCardView coverCard = createTopBookCard(topBookImage);
            topBookCards.add(coverCard);
            topBookStage.addView(coverCard);
        }

        positionTopCards(false);
        bindDots(topBookDots, topBookImages.length, topBookIndex, false);

        btnNextTop.setOnClickListener(v -> {
            moveTopCarousel(true);
            restartTopCarousel();
        });

        btnPrevTop.setOnClickListener(v -> {
            moveTopCarousel(false);
            restartTopCarousel();
        });
    }

    private MaterialCardView createTopBookCard(int imageRes) {
        MaterialCardView cardView = new MaterialCardView(this);
        FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(dp(124), dp(176));
        cardParams.gravity = Gravity.CENTER;

        cardView.setLayoutParams(cardParams);
        cardView.setRadius(dp(12));
        cardView.setCardElevation(dp(8));
        cardView.setUseCompatPadding(false);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(imageRes);
        applyBookCoverZoom(imageView);
        cardView.addView(imageView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        return cardView;
    }

    private void positionTopCards(boolean animate) {
        for (int i = 0; i < topBookCards.size(); i++) {
            MaterialCardView coverCard = topBookCards.get(i);
            int offset = getCircularOffset(i, topBookIndex, topBookCards.size());
            applyTopBookSlot(coverCard, offset, animate);
        }

        topBookCards.get(topBookIndex).bringToFront();
    }

    private void applyTopBookSlot(MaterialCardView coverCard, int offset, boolean animate) {
        float targetX = dp(topBookSlotX(offset));
        float targetScale = topBookSlotScale(offset);
        float targetAlpha = topBookSlotAlpha(offset);
        float targetZ = dp(topBookSlotDepth(offset));

        coverCard.setVisibility(View.VISIBLE);
        coverCard.setTranslationZ(targetZ);

        if (!animate) {
            coverCard.setTranslationX(targetX);
            coverCard.setScaleX(targetScale);
            coverCard.setScaleY(targetScale);
            coverCard.setAlpha(targetAlpha);
            return;
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(coverCard, View.TRANSLATION_X, targetX),
                ObjectAnimator.ofFloat(coverCard, View.SCALE_X, targetScale),
                ObjectAnimator.ofFloat(coverCard, View.SCALE_Y, targetScale),
                ObjectAnimator.ofFloat(coverCard, View.ALPHA, targetAlpha)
        );
        animatorSet.setDuration(480L);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    private int getCircularOffset(int itemIndex, int activeIndex, int total) {
        int offset = itemIndex - activeIndex;
        int half = total / 2;

        if (offset > half) {
            offset -= total;
        } else if (offset < -half) {
            offset += total;
        }

        return offset;
    }

    private int topBookSlotX(int offset) {
        switch (offset) {
            case -2:
                return -134;
            case -1:
                return -74;
            case 1:
                return 74;
            case 2:
                return 134;
            default:
                return 0;
        }
    }

    private float topBookSlotScale(int offset) {
        switch (Math.abs(offset)) {
            case 1:
                return 0.78f;
            case 2:
                return 0.62f;
            default:
                return 1f;
        }
    }

    private float topBookSlotAlpha(int offset) {
        switch (Math.abs(offset)) {
            case 1:
                return 0.78f;
            case 2:
                return 0.42f;
            default:
                return 1f;
        }
    }

    private int topBookSlotDepth(int offset) {
        switch (Math.abs(offset)) {
            case 1:
                return 5;
            case 2:
                return 2;
            default:
                return 12;
        }
    }

    private void setupFeaturedBooks() {
        featuredBooksList.removeAllViews();
        TextView tvViewAll = findViewById(R.id.tvViewAll);
        tvViewAll.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, BooksActivity.class)));

        for (int i = 0; i < featuredBooks.length; i += 2) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.TOP);

            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            rowParams.bottomMargin = dp(16);
            featuredBooksList.addView(row, rowParams);

            row.addView(createBookItem(featuredBooks[i], true));

            if (i + 1 < featuredBooks.length) {
                row.addView(createBookItem(featuredBooks[i + 1], false));
            } else {
                SpaceView spacer = new SpaceView(this);
                row.addView(spacer, new LinearLayout.LayoutParams(0, 1, 1f));
            }
        }
    }

    private View createBookItem(Book book, boolean leftColumn) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        itemParams.setMarginEnd(leftColumn ? dp(13) : 0);
        itemParams.setMarginStart(leftColumn ? 0 : dp(13));
        item.setLayoutParams(itemParams);

        MaterialCardView coverCard = new MaterialCardView(this);
        coverCard.setRadius(dp(8));
        coverCard.setCardElevation(dp(2));
        coverCard.setStrokeWidth(dp(1));
        coverCard.setStrokeColor(Color.rgb(209, 178, 105));

        ImageView cover = new ImageView(this);
        cover.setImageResource(book.imageRes);
        applyBookCoverZoom(cover);
        coverCard.addView(cover, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        LinearLayout.LayoutParams coverParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(190)
        );
        item.addView(coverCard, coverParams);

        TextView title = createText(book.title, 12, R.font.noto_serif_semi_bold, Color.rgb(0, 12, 30));
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setMaxLines(1);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        titleParams.topMargin = dp(7);
        item.addView(title, titleParams);

        TextView author = createText(book.author, 10, R.font.manrope_regular, Color.rgb(130, 125, 118));
        author.setEllipsize(TextUtils.TruncateAt.END);
        author.setMaxLines(1);
        item.addView(author);

        TextView meta = createText(book.views + " views | " + book.rating, 9, R.font.manrope_regular, Color.rgb(154, 146, 132));
        LinearLayout.LayoutParams metaParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        metaParams.topMargin = dp(2);
        item.addView(meta, metaParams);

        return item;
    }

    private void setupStoreCarousel() {
        storeFlipper.removeAllViews();

        for (Store store : stores) {
            storeFlipper.addView(createStoreSlide(store));
        }

        bindDots(storeDots, stores.length, storeIndex, true);

        btnNextStore.setOnClickListener(v -> {
            moveStoreCarousel(true);
            restartStoreCarousel();
        });

        btnPrevStore.setOnClickListener(v -> {
            moveStoreCarousel(false);
            restartStoreCarousel();
        });
    }

    private View createStoreSlide(Store store) {
        FrameLayout slide = new FrameLayout(this);

        ImageView storeImage = new ImageView(this);
        storeImage.setImageResource(store.imageRes);
        storeImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        slide.addView(storeImage, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        View gradientOverlay = new View(this);
        gradientOverlay.setBackgroundResource(R.drawable.bg_store_gradient);
        slide.addView(gradientOverlay, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        LinearLayout textGroup = new LinearLayout(this);
        textGroup.setOrientation(LinearLayout.VERTICAL);
        textGroup.setPadding(dp(18), 0, dp(52), dp(18));

        TextView title = createText(store.name, 16, R.font.noto_serif_bold, Color.WHITE);
        title.setShadowLayer(4f, 0f, 1f, Color.rgb(0, 12, 30));
        title.setMaxLines(2);
        textGroup.addView(title);

        TextView address = createText(store.address, 10, R.font.manrope_regular, Color.rgb(232, 224, 211));
        address.setEllipsize(TextUtils.TruncateAt.END);
        address.setMaxLines(1);
        LinearLayout.LayoutParams addressParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        addressParams.topMargin = dp(3);
        textGroup.addView(address, addressParams);

        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textParams.gravity = Gravity.BOTTOM;
        slide.addView(textGroup, textParams);

        return slide;
    }

    private void moveTopCarousel(boolean forward) {
        if (forward) {
            topBookIndex = (topBookIndex + 1) % topBookImages.length;
        } else {
            topBookIndex = (topBookIndex - 1 + topBookImages.length) % topBookImages.length;
        }

        positionTopCards(true);
        bindDots(topBookDots, topBookImages.length, topBookIndex, false);
    }

    private void moveStoreCarousel(boolean forward) {
        applyCarouselAnimation(storeFlipper, forward);

        if (forward) {
            storeFlipper.showNext();
        } else {
            storeFlipper.showPrevious();
        }

        storeIndex = storeFlipper.getDisplayedChild();
        bindDots(storeDots, stores.length, storeIndex, true);
    }

    private void applyCarouselAnimation(ViewFlipper flipper, boolean forward) {
        float inFromX = forward ? 1f : -1f;
        float outToX = forward ? -1f : 1f;

        flipper.setInAnimation(createSlideAnimation(inFromX, 0f, 0f, 1f));
        flipper.setOutAnimation(createSlideAnimation(0f, outToX, 1f, 0f));
    }

    private Animation createSlideAnimation(float fromX, float toX, float fromAlpha, float toAlpha) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setDuration(360L);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, fromX,
                Animation.RELATIVE_TO_PARENT, toX,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f
        );
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);

        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);

        return animationSet;
    }

    private void bindDots(LinearLayout container, int count, int activeIndex, boolean pillActive) {
        container.removeAllViews();

        for (int i = 0; i < count; i++) {
            boolean active = i == activeIndex;
            View dot = new View(this);
            GradientDrawable dotShape = new GradientDrawable();
            dotShape.setColor(active ? Color.rgb(119, 90, 25) : Color.rgb(224, 218, 205));
            dotShape.setShape(pillActive && active ? GradientDrawable.RECTANGLE : GradientDrawable.OVAL);
            dotShape.setCornerRadius(dp(4));
            dot.setBackground(dotShape);

            int width = pillActive && active ? dp(22) : dp(7);
            int height = pillActive ? dp(5) : dp(8);

            LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(width, height);
            dotParams.setMargins(dp(3), dp(4), dp(3), dp(4));
            container.addView(dot, dotParams);
        }
    }

    private TextView createText(String text, int textSizeSp, int fontRes, int color) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextColor(color);
        textView.setTextSize(textSizeSp);
        Typeface typeface = ResourcesCompat.getFont(this, fontRes);
        textView.setTypeface(typeface);
        textView.setIncludeFontPadding(false);
        return textView;
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

    private void startAutoCarousels() {
        stopAutoCarousels();
        carouselHandler.postDelayed(topBookAutoSlide, TOP_CAROUSEL_DELAY);
        carouselHandler.postDelayed(storeAutoSlide, STORE_CAROUSEL_DELAY);
    }

    private void stopAutoCarousels() {
        carouselHandler.removeCallbacks(topBookAutoSlide);
        carouselHandler.removeCallbacks(storeAutoSlide);
    }

    private void restartTopCarousel() {
        carouselHandler.removeCallbacks(topBookAutoSlide);
        carouselHandler.postDelayed(topBookAutoSlide, TOP_CAROUSEL_DELAY);
    }

    private void restartStoreCarousel() {
        carouselHandler.removeCallbacks(storeAutoSlide);
        carouselHandler.postDelayed(storeAutoSlide, STORE_CAROUSEL_DELAY);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static class SpaceView extends View {
        SpaceView(HomeActivity context) {
            super(context);
        }
    }
}
