package aar92_22.library;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import aar92_22.library.Activities.AddBookActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4ClassRunner.class)
public class AddBookActivityTest {

    private static final String TITLE = "Harry Potter";
    private static final String LAST_NAME = "Rowling";
    private static final String FIRST_NAME = "Joanne";

    @Rule
    public IntentsTestRule<AddBookActivity> mActivityRule = new IntentsTestRule<>(AddBookActivity.class);

    @Before
    public void stubAllExternalIntents() {
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void clickAddNewAuthorAndRemoveAuthorTest (){
        //Adds authors layout
        onView(withId(R.id.add_author)).perform(click());
        onView(withId(R.id.author_layout2)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.add_author)).perform(click());
        onView(withId(R.id.author_layout3)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        //Removes authors layout
        onView(withId(R.id.remove_author3)).perform(click());
        onView(withId(R.id.author_layout3)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(withId(R.id.remove_author2)).perform(click());
        onView(withId(R.id.author_layout2)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

    }

    @Test
    public void clickSaveBookTest(){
        onView(withId(R.id.title_input)).perform(typeText(TITLE), closeSoftKeyboard());
        onView(withId(R.id.last_name_input)).perform(replaceText(LAST_NAME));
        onView(withId(R.id.first_name_input)).perform(replaceText(FIRST_NAME));
        onView(withId(R.id.action_done)).perform(click());
    }



    @Test
    public void clickTakePhotoTest(){
        onView(withId(R.id.add_book_scroll_view)).perform(swipeUp());
        onView(withId(R.id.take_photo)).perform(click());
        onView(withId(R.id.select_image)).check(matches(isDisplayed()));
        onView(withId(R.id.take_photo)).check(matches(isDisplayed()));
    }

}
