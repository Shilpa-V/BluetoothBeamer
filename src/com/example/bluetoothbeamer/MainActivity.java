package com.example.bluetoothbeamer;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	private TextView mTitle;
	Button scan; 
	private BluetoothAdapter mBluetoothAdapter = null;
	private static final int REQUEST_ENABLE_BT = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 
        // Set up the window layout
       
        setContentView(R.layout.activity_main);
       
        scan=(Button) findViewById(R.id.scan);
		Button settings=(Button) findViewById(R.id.settings);
		//scan.setEnabled(false);
			
		scan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					String state=scan.getText().toString();
							if(state.equals("Scan"))
							{
								Intent scanIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
								startActivity(scanIntent);
	
							
							}
							else
							{
								scan.setText("Make Bluetooth ON");
								Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
								startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
							
							}
				}
			
			
		
			
		});
		
		settings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent setIntent = new Intent(getApplicationContext(), SettingPathActivity.class);
	            startActivity(setIntent);
				finish();
			}
		});
			
	    
		
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        } 
        
        else if(!mBluetoothAdapter.isEnabled())
        {
        	scan.setText("Make Bluetooth ON");
        }
        else if(mBluetoothAdapter.isEnabled())
        {
        	scan.setText("Scan");
        }
               		
	}
	

	@Override
	protected void onStart() {
		super.onStart();
		
		// If BT is not on, request that it be enabled.
        
		if (!mBluetoothAdapter.isEnabled()) {
			
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
					
		}
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent
	data)
	{
	    if (requestCode == REQUEST_ENABLE_BT)
	    {
	        if(resultCode == RESULT_OK)
	        {
	        	scan=(Button) findViewById(R.id.scan);
	         	scan.setText("Scan");
	        	
	        }
	        else
	        {
	        	scan=(Button) findViewById(R.id.scan);
	        	scan.setText("Make Bluetooth ON");
	        }
	    }
	}
	
}
