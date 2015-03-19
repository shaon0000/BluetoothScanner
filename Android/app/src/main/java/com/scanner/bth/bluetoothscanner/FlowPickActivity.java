package com.scanner.bth.bluetoothscanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sb.db.SqlCreateTableGenerator;


public class FlowPickActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_pick);

        Button newButton = (Button) findViewById(R.id.flow_pick_new_button);
        Button prevButton = (Button) findViewById(R.id.flow_pick_prev_log_button);
        Button syncButton = (Button) findViewById(R.id.flow_pick_sync);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FlowPickActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        SqlCreateTableGenerator gen = new SqlCreateTableGenerator();
    }
}
