package com.example.home.flipcardndroid;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import card.CardView;
import constants.Constants;
import networkManager.NetworkManager;

public class MainActivity extends AppCompatActivity {
    CardView cardViews[];
    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imfVw1 = findViewById(R.id.cardView1);
        ImageView imfVw2 = findViewById(R.id.cardView2);
        cardViews = new CardView[Constants.cardCount];
        cardViews[0] = new CardView(imfVw1, MainActivity.this);
        cardViews[1] = new CardView(imfVw2, MainActivity.this);
        NetworkManager.getSharedInstance().getCards(cardViews, MainActivity.this);
    }

    public void onClickRequestCard(View v) {
        NetworkManager.getSharedInstance().getCards(cardViews, MainActivity.this);
        for (CardView card : cardViews) card.flipBack();
    }

    public void onClickChangeDealer(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter dealer ip:");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(NetworkManager.getSharedInstance().getUrl());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                NetworkManager.getSharedInstance().setUrl(m_Text);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
