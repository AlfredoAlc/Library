package aar92_22.library;


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.runner.AndroidJUnit4;
import androidx.test.espresso.intent.rule.IntentsTestRule;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import aar92_22.library.Activities.AddBookActivity;
import aar92_22.library.Activities.MainActivity;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasPackageName;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasPackage;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagKey;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void clickAddButtonTest (){
        //Opens Add Book Activity
        onView(withId(R.id.add_book_button)).perform(click());
        intending(hasComponent(hasShortClassName(".AddBookActivity"))).respondWith(
                new Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        );

    }

    @Test
    public void clickFilterButtonTest (){
        onView(withId(R.id.filter_menu)).perform(click());
        intending(hasComponent(hasShortClassName(".FilterActivity"))).respondWith(
                new Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        );

    }

    @Test
    public void clickBestSellersTest (){
        onView(withId(R.id.best_sellers_button)).perform(click());
        intending(hasComponent(hasShortClassName(".BestSellers"))).respondWith(
                new Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        );
    }
}
