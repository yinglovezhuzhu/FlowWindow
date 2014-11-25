package com.xiaoying.flowwindow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.btn_show).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FlowWindow.getInstance(MainActivity.this).showFlowView();
				
			}
		});
		
		findViewById(R.id.btn_hide).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FlowWindow.getInstance(MainActivity.this).hideFlowView();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		FlowWindow.getInstance(MainActivity.this).hideFlowView();
		super.onDestroy();
	}
}
