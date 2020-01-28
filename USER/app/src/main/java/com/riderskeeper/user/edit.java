package com.riderskeeper.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;

public class edit extends Activity {

//onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.edit_layout);
        this.setFinishOnTouchOutside(false);

//getIntent()
        Intent editIntent = getIntent();
        final HashSet<String> idList = (HashSet<String>) editIntent.getSerializableExtra("list");
        final int[] conn = (int[]) editIntent.getSerializableExtra("connection");

//deleteButton OnClickListener
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = view.getTag().toString();
                String s2 = s.replace("button", "");

                Toast toast = Toast.makeText(edit.this, "\"" + s2 + "\" deleted", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();

                Intent intent = new Intent();
                intent.putExtra("id", s2);
                setResult(2, intent);
                finish();
            }
        };

//ON/OFF button OnClickListner
        Button.OnClickListener onClickListener2 = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = view.getTag().toString();
                String s2 = s.replace("button2", "");

                Button b2 = (Button) view;
                String stat = b2.getText().toString();

                if(stat == "OFF"){ //OFF > ON
                    //server

                    Toast toast = Toast.makeText(edit.this, "\"" + s2 + "\" connected", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();

                    Intent intent = new Intent();
                    intent.putExtra("id", s2);
                    intent.putExtra("color", 1);
                    setResult(3, intent);
                    finish();
                }
                else{ //ON > OFF
                    //server

                    Toast toast = Toast.makeText(edit.this, "\"" + s2 + "\" disconnected", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();

                    Intent intent = new Intent();
                    intent.putExtra("id", s2);
                    intent.putExtra("color", 0);
                    setResult(3, intent);
                    finish();
                }
            }
        };

//scrollView
        int count = 0;
        for (String id : idList) {
            appendList(id);

            //layouts - edit_xml
            LinearLayout ll = findViewById(R.id.scrollLinear);
            LinearLayout layer = ll.findViewWithTag("layer" + id);

            //delete button
            Button b = layer.findViewWithTag("button" + id);
            b.setOnClickListener(onClickListener);

            //ON/OFF button
            Button b2 = layer.findViewWithTag("button2" + id);
            b2.setOnClickListener(onClickListener2);

            if (conn[count] == 1){ //green
                b2.setText("ON");
            }else { //red
                b2.setText("OFF");
            }
            count++;
        }

//EditText
        final EditText inputID = findViewById(R.id.ID_input);

//addButton
        Button addButton = findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //already exists
                if (idList.contains(inputID.getText().toString())) {
                    Toast toast = Toast.makeText(edit.this, "\"" + inputID.getText() + "\" is already added", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();

                    Intent intent = new Intent();
                    intent.putExtra("id", inputID.getText().toString());
                    setResult(0, intent);
                    finish();
                } else {
                    //send to Server, check DB

                    if (true) { //valid ID

                        //toast message
                        Toast toast = Toast.makeText(edit.this, "\"" + inputID.getText() + "\" added", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();

                        Intent intent = new Intent();
                        intent.putExtra("id", inputID.getText().toString());
                        setResult(1, intent);
                        finish();
                    } else { //invalid ID

                        //toast message
                        Toast toast = Toast.makeText(edit.this, "\"" + inputID.getText() + "\" is not a valid ID", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();

                        finish();
                    }
                }
            }
        });

//cancelButton
        Button cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 Intent intent = new Intent();
                 intent.putExtra("id", inputID.getText().toString());
                 setResult(0, intent);
                 finish();
             }
         });
    }
//End of onCreate



    public void appendList(String id) {

        LinearLayout ll = findViewById(R.id.scrollLinear);

        LinearLayout linear = new LinearLayout(this);
        linear.setTag("layer" + id);
        linear.setOrientation(LinearLayout.HORIZONTAL);
        linear.setGravity(Gravity.RIGHT);
        ll.addView(linear);
        LinearLayout.LayoutParams llparams = (LinearLayout.LayoutParams) linear.getLayoutParams();
        llparams.rightMargin = 80;
        linear.setLayoutParams(llparams);

        TextView text = new TextView(this);
        text.setTag(id);
        text.setText(id + "  ");
        linear.addView(text);

        Button button = new Button(this);
        button.setTag("button" + id);
        button.setText("Delete");
        linear.addView(button);
        ViewGroup.LayoutParams params = button.getLayoutParams();
        params.width = 300;
        params.height = 150;
        button.setLayoutParams(params);

        Button button2 = new Button(this);
        button2.setTag("button2" + id);
        linear.addView(button2);
        ViewGroup.LayoutParams params2 = button2.getLayoutParams();
        params2.width = 200;
        params2.height = 150;
        button2.setLayoutParams(params2);


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}