package com.agno.wpconvo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.hbb20.CountryCodePicker;

import java.net.URI;
import java.net.URISyntaxException;


public class MainActivity extends AppCompatActivity {
    Button lt;
    TextView connection;
    EditText ed,ed2;
    CountryCodePicker ccp;
    ImageButton clear,info;
    String query,st,encoded="";
    WebView open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        info = findViewById(R.id.info);
        lt = findViewById(R.id.button);
        open = findViewById(R.id.web);
        ccp = findViewById(R.id.ccp);
        ed2 = findViewById(R.id.text);
        ed = findViewById(R.id.ed);
        clear = findViewById(R.id.clear);
        connection = findViewById(R.id.connection);
        ccp.registerCarrierNumberEditText(ed);


        // MAIN FUNCTIONALITY (1. GET AND MERGE THE PHONE NUMBER WITH THE RIGHT COUNTRY CODE.   ## USED CCP COUNTRY CODE PICKER LIBRARY.
        //                     2. PASS THE ENCODED FORMAT OF ASCIISTIRING.
        //                     3. CHECK THE VALIDITY OF THE INPUT PHONE NUMBER.
        //                     4. OPEN WHATSAPP IF THE NUMBER IS CORRECT.)
        lt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                st = ccp.getFullNumber();
                query = ed2.getText().toString();
                try {
                    URI uri = new URI(
                            "http",
                            "wa.me",
                            "/" + st + "/",
                            "text=" + query,
                            null
                    );
                    encoded = uri.toASCIIString();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                if (!isConnectedToInternet(getApplicationContext())) {
                    connection.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                           connection.setVisibility(View.INVISIBLE);
                        }
                    }, 3000);
                } else {
                    connection.setVisibility(View.INVISIBLE);
                    if (!ccp.isValidFullNumber()) {
                        Toast.makeText(MainActivity.this, "Enter a valid number!", Toast.LENGTH_SHORT).show();
                    } else {
                        open.loadUrl(encoded);
                    }
                }
            }
        });
        // CLEAR THE MESSAGE EDIT-TEXT.
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed2.setText("");
            }
        });

        //NEW HANDLER
        final Handler handler = new Handler();


        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, aboutapp.class);
                startActivity(intent);

            }
        });
    }

    // EXIT FUNCTIONALITY
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (! hasFocus) {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    public boolean  isConnectedToInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
