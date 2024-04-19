package com.example.mobilecwnew;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;

/*  Starter project for Mobile Platform Development in main diet 2023/2024
    You should use this project as the starting point for your assignment.
    This project simply reads the data from the required URL and displays the
    raw data in a TextField
*/

//
// Name                 Craig Adumuah_________________
// Student ID           S2026435_________________
// Programme of Study   Computing_________________
//

// UPDATE THE PACKAGE NAME to include your Student Identifier
//package com.example.resitcw_skeleton;


/*public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}*/
public class MainActivity extends AppCompatActivity implements OnClickListener
{
    private TextView rawDataDisplay;
    private Button startButton;

    private Handler mHandler;
    private ProgressBar mProgressBar;
    private String result;
    private String url1="";
    private String urlSource="https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2643123";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define the array of campus locations and associated institutions
        String[] selectedCity = {"Glasgow", "London", "New York", "Oman", "Mauritius", "Bangladesh"};

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinner_items,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String City = selectedCity[position];
                fetchWeatherDataFromRSS(City);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Use the 'googleMap' object to interact with the map
            }
        });

    }

    //
    // Now that you have the xml data you can parse it
    //
    private void fetchWeatherDataFromRSS(String city) {
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL("https://weather-service-thunder-broker.api.bbci.co.uk/en/observation/rss/"); // Your actual URL might differ
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(inputStream, null);

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && "item".equals(parser.getName())) {
                        if (parseTitle(parser).contains(city)) { // assuming the city is in the title
                            String description = parseDescription(parser);
                            runOnUiThread(() -> displayWeatherData(description));
                            break;
                        }
                    }
                    eventType = parser.next();
                }
                inputStream.close();
            } catch (Exception e) {
                Log.e("ERROR", "Failed to fetch or parse RSS feed", e);
            }
        });
        thread.start();
    }
    private void fetchWeatherData(String location) {
        Log.d("fetchWeatherData", "Fetching weather data for: " + location);
    }

    private String parseTitle(XmlPullParser parser) throws Exception {
        return parser.nextText();
    }

    private String parseDescription(XmlPullParser parser) throws Exception {
        return parser.nextText();
    }
    private void parseForecastData(String data) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(data));

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = xpp.getName();
                    if ("item".equals(tagName)) {
                        // Start of forecast item
                        String title = "";
                        String description = "";

                        while (eventType != XmlPullParser.END_TAG || !"item".equals(xpp.getName())) {
                            if (eventType == XmlPullParser.START_TAG) {
                                String innerTagName = xpp.getName();
                                if ("title".equals(innerTagName)) {
                                    // Extract forecast title
                                    title = xpp.nextText();
                                } else if ("description".equals(innerTagName)) {
                                    // Extract forecast description
                                    description = xpp.nextText();
                                }
                            }
                            eventType = xpp.next();
                        }

                        // Now you have the forecast information, you can update UI or store it in data structures
                        String forecastInfo = "Title: " + title + "\nDescription: " + description;
                        Log.d("ForecastInfo", forecastInfo);
                        // Update UI or store data as needed
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }


    private void displayWeatherData(String data) {
        TextView weatherInfoTextView = findViewById(R.id.weatherInfoTextView);
        weatherInfoTextView.setText(data);
    }

    private void fetchDetailedForecastData(String city) {
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL("https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2643123");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(inputStream, null);

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    // Implement logic to parse detailed forecast data
                    // Update UI with the forecast information
                    eventType = parser.next();
                }
                inputStream.close();
            } catch (Exception e) {
                Log.e("ERROR", "Failed to fetch or parse forecast RSS feed", e);
            }
        });
        thread.start();
    }



  //  public void onClick(View aview)
    {
        startProgress();
    }

    public void onClick(View v)
    {


        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            //get current date time with Date()
            Date date = new Date();
            System.out.println(dateFormat.format(date));
            String weatherInfo = "Pizza Delivery for " +"\nat " +dateFormat.format(date) +"\n";

            System.out.println(weatherInfo);

            //Toast.makeText(this,deliveryInfo,Toast.LENGTH_LONG).show();
            showweatherinfoDialog(weatherInfo);

        }

    }
    private void showweatherinfoDialog(String weatherInfo)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm weather details");
        builder.setMessage(weatherInfo);
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm ", confirmButtonListener);
        builder.setNegativeButton("Cancel", cancelButtonListener);
        AlertDialog alert = builder.create();
        alert.show();

    }


    private DialogInterface.OnClickListener confirmButtonListener = new DialogInterface.OnClickListener (){
        public void onClick(DialogInterface dialog, int id)
        {
            System.out.println("Button with id "+ id + " pressed");
            dialog.cancel();
        }
    };

    private DialogInterface.OnClickListener cancelButtonListener = new DialogInterface.OnClickListener ()
    {

        public void onClick(DialogInterface dialog, int id)
        {
            System.out.println("Button with id "+ id + " pressed");
            dialog.cancel();

        }
    };

    private void runProgress()
    {
        mHandler=new Handler();

        // Set up and display the progressBar
        mProgressBar= findViewById(R.id.bar1);
        mProgressBar.setMax(100);

        // Create Thread to handle the "long running task"
        // In this case it is a counter that will be used
        // represent progress via a progress bar
        new Thread(new Runnable()
        {
            @Override

            public void run()
            {
                for (int i = 0; i <= 100; i++)
                {
                    final int currentProgressCount = i;
                    try
                    {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    // Update the value background thread to UI thread
                    // This is done in a further short life length thread
                    // to ensure the is no blocking of the "calculation" thread
                    mHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mProgressBar.setProgress(currentProgressCount);
                        }
                    });
                }
            }
        }).start();
    }
    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(urlSource)).start();
    } //

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {
        private String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {

            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";


            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null)
                {
                    result = result + inputLine;
                    Log.e("MyTag",inputLine);

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }

            //Get rid of the first tag <?xml version="1.0" encoding="utf-8"?>
            int i = result.indexOf(">");
            result = result.substring(i+1);
            //Get rid of the 2nd tag <rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
            i = result.indexOf(">");
            result = result.substring(i+1);
            Log.e("MyTag - cleaned",result);



            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    rawDataDisplay.setText(result);
                }
            });
        }

    }

}