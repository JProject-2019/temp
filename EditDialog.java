package com.riderskeeper.user;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class EditDialog {
    private static Context context;

    public EditDialog(Context context) {
        this.context = context;
    }

    public static void callFunction() {

//dialog settings - layout, size
        final Dialog dlg = new Dialog(context);
        LinearLayout ll = findViewById(R.id.tst);
        dlg.setContentView(ll);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dlg.getWindow().getAttributes());
        lp.width = 1200;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dlg.show();
        Window window = dlg.getWindow();
        window.setAttributes(lp);

        final EditText id = dlg.findViewById(R.id.ID_input); //ID input
        final Button addButton = dlg.findViewById(R.id.enter_ID); //ADD

        //TextView tv = new TextView(context);
        //tv.setText("HIHIHI:):)");
        //(ll).addView(tv);

//ADD Button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send to server, check DB

                if (true) { //valid ID


                }else { //wrong input
                    Toast toast = Toast.makeText(context, "\"" + id.getText() + "\" is not a valid ID", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
                dlg.dismiss();
            }
        });
    }
}