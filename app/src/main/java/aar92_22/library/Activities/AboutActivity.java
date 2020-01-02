package aar92_22.library.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.app.ShareCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import aar92_22.library.R;

public class AboutActivity extends AppCompatActivity {


    private static final String APP_URL = "http://play.google.com/store/apps/details?id=aar92_22.library";
    private static final String ADDRESS = "alfredo.alcantara.r@gmail.com";
    private static final String SUBJECT = "Feedback: My Library";

    Button shareButton;
    Button rateButton;
    Button contactButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null ){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        shareButton = findViewById(R.id.share_button);
        rateButton = findViewById(R.id.rate_button);
        contactButton = findViewById(R.id.contact_button);



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return true;

    }


    public void shareButtonClick(View view) {

        String mimeType = "text/plain";


        ShareCompat.IntentBuilder.from(AboutActivity.this)
                .setChooserTitle("Share: " + getString(R.string.app_name))
                .setType(mimeType)
                .setText(APP_URL)
                .startChooser();

    }

    public void rateButtonClick(View view) {
        Intent rateLink = new Intent(Intent.ACTION_VIEW, Uri.parse(APP_URL));
        startActivity(rateLink);


    }


    public void contactButtonClick(View view) {

        Intent contactIntent = new Intent(Intent.ACTION_SENDTO);
        contactIntent.setData(Uri.parse("mailto:"+ ADDRESS ));
        contactIntent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT);


        if(contactIntent.resolveActivity(getPackageManager()) != null){
            startActivity(contactIntent);
        }else{
            Toast.makeText(this,getString(R.string.email_error), Toast.LENGTH_LONG).show();
        }

    }


}
