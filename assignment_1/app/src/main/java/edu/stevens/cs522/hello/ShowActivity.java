package edu.stevens.cs522.hello;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ShowActivity extends Activity {
    public final static String MESSAGE_KEY = "message";

    private void setDisplayText(String name){
        final String displayText = "Hello " + name;
        final TextView textView = (TextView) findViewById(R.id.show_activity_text);
        textView.setText(displayText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        final String message = getIntent().getStringExtra(MESSAGE_KEY);
        this.setDisplayText(message);
    }
}