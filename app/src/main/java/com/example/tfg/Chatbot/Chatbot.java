package com.example.tfg.Chatbot;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg.MainActivity;
import com.example.tfg.R;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class Chatbot extends AppCompatActivity {
    private TextInputEditText queryEditText;
    private ImageView sendQuery,logo,appIcon;

    FloatingActionButton btnShowDialog;
    FloatingActionButton btnatras;
    private ProgressBar progressBar;
    private LinearLayout chatResponse;
    private ChatFutures chatModel;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.message_dialog);
        if(dialog.getWindow() !=null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        sendQuery = dialog.findViewById(R.id.sendMessage);
        queryEditText = dialog.findViewById(R.id.queryEditText);
        btnShowDialog = findViewById(R.id.showMessage);
        btnatras = findViewById(R.id.atras);
        progressBar = findViewById(R.id.progressBar);
        chatResponse = findViewById(R.id.chatResponse);
        chatModel = getChatModel();
        appIcon = findViewById(R.id.appIcon);


        btnShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        btnatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(intent);
            }
        });
        sendQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                appIcon.setVisibility(View.GONE);
                String query = queryEditText.getText().toString();

                queryEditText.setText("");
                chatbody("You",query,getDrawable(R.drawable.av));
                chatResponse.setGravity(Gravity.END);
                GeminiREsp.getResponse(chatModel, query, new ResponseCallBack() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        chatbody("AI",response,getDrawable(R.drawable.diabetapp));
                        chatResponse.setGravity(Gravity.START);
                    }

                    @Override
                    public void onError(String throwable) {
                        chatbody("AI","Please try again",getDrawable(R.drawable.diabetapp));
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
    private ChatFutures getChatModel(){
        GeminiREsp model = new GeminiREsp();
        GenerativeModelFutures modelFutures = model.getModel();
        return modelFutures.startChat();
    }
    private void chatbody(String username, String query, Drawable image) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_message,null);
        TextView name = view.findViewById(R.id.name);
        TextView message = view.findViewById(R.id.agentMessage);
        ImageView logo = view.findViewById(R.id.logo2);

        name.setText(username);

        message.setText(query);

        logo.setImageDrawable(image);


        chatResponse.addView(view);
        ScrollView scrollView = findViewById(R.id.scrollView2);
        scrollView.post(() -> scrollView.fullScroll(view.FOCUS_DOWN));


    }
}