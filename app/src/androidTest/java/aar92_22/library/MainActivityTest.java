package aar92_22.library;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.ActivityResultFunction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import aar92_22.library.Activities.BookDetailActivity;
import aar92_22.library.Activities.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {

    private static final Integer BOOK_POSITION = 1;

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void clickAddButtonTest (){
        //Opens Add Book Activity
        onView(withId(R.id.add_book_button)).perform(click());
        intended(hasComponent(hasShortClassName(".AddBookActivity")));

    }

    @Test
    public void clickFilterButtonTest (){
        onView(withId(R.id.filter_menu)).perform(click());
        intending(hasComponent(hasShortClassName(".FilterActivity"))).respondWith(
                new Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        );

    }

   /* @Test
    public void clickBestSellersTest (){
        onView(withId(R.id.best_sellers_button)).perform(click());
        intending(hasComponent(hasShortClassName(".BestSellers"))).respondWith(
                new Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        );
    }*/

    /*@Test
    public void openBookDetailFromRecyclerViewTest(){
        //Must run AddBookActivityTest FIRST to run this test
        onView(withId(R.id.list_books_recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(BOOK_POSITION, click())
        );

        intending(allOf(hasComponent(hasShortClassName(".BookDetailActivity")),
                hasExtraWithKey(BookDetailActivity.EXTRA_ID))).respondWith(
                        new Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        );

    }*/


}
