package com.example.bluetoothbeamer;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DeviceListActivity extends Activity {
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;
    String FILENAME = "fileList";
    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
        Button sendButton = (Button) findViewById(R.id.send);
   //     sendButton.setEnabled(false);
/*        sendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
               // doDiscovery();
                //v.setVisibility(View.GONE);
            }
        });
*/           
        // Initialize array adapter for newly discovered devices
         mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
       

        // Find and set up the ListView for newly discovered devices
        final ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        doDiscovery();
        
        sendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	ArrayList<String> fileList = null;
            	
                mBtAdapter.cancelDiscovery();
                setProgressBarIndeterminateVisibility(false);
            	
            	int totalDev=newDevicesListView.getCount();
            	 String temp=""; 
            	 try
         		{
         		FileInputStream fin = openFileInput(FILENAME);
         		int c;
         			if(fin.available()>0)
         			{
         				while( (c = fin.read()) != -1){
         					temp = temp + Character.toString((char)c);
         				}
         			}
         			fin.close();
         		}
         		
         		catch(Exception e)
         		{
         			
         		}
    			
            	
            	String filePath = Environment.getExternalStorageDirectory().toString() + "/" + temp;
        		//Toast.makeText(getApplicationContext(), "Filepath:" + filePath, Toast.LENGTH_SHORT).show();
            	File f = new File ( filePath );
    			           	
            	
    			if(f!=null && f.exists() && f.isDirectory())
                {
                	File[] files = f.listFiles();
        			
                	fileList = new ArrayList<String>();
                	System.out.println ( "Total no of file : " + files.length );
                	for ( int i = 0 ; i < files.length ; i++ )
                	{
                		fileList.add( files[i].toString() );
                		System.out.println ( "Files : " + files[i].toString() );
                	}

                	for(int i=0 ;i<totalDev ; i++)
                	{	
                		View v1=newDevicesListView.getChildAt(i);
            	
                		String info=((TextView) v1).getText().toString();
                		String address = info.substring(info.length() - 17);
                		String name=info.substring(0, info.length() - 17);
                		//	Toast.makeText(getApplicationContext(), "Add:"+ address +" Name:" + name, Toast.LENGTH_SHORT).show();
                		setTitle("Sending file to "+name);
                		try
                    	{
                    		for(int j=0;i<fileList.size();j++)
                    		{
                    			//Toast.makeText(getApplicationContext(), "File :"+ fileList.get(i), Toast.LENGTH_SHORT).show();
                    			new SendingThread( fileList.get(i).toString().trim(),address,name).start();
           
                    		}
                    	}
                    	catch(Exception e)
                    	{
                    		System.out.println("Error while sending " + e.toString());
                    	}
                	//	setTitle("File sent to "+name);
                		
            		}
    			
                }
    			else if(f!=null && f.exists() && f.isFile())
                {	
           				//new SendingThread( filePath,address,name).start();
    				
    				for(int i=0 ;i<totalDev ; i++)
                	{	
                		View v1=newDevicesListView.getChildAt(i);
            	
                		String info=((TextView) v1).getText().toString();
                		String address = info.substring(info.length() - 17);
                		String name=info.substring(0, info.length() - 17);
                		//	Toast.makeText(getApplicationContext(), "Add:"+ address +" Name:" + name, Toast.LENGTH_SHORT).show();
                		setTitle("Sending file to "+name);
                		new SendingThread( filePath,address,name).start();
                		
                		
                	}
 				
                }	
                
               }
        });
    
    }
    
    

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.
        unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }
    
    
    
    
    

    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
        	 ArrayList<String> fileList = null;

            mBtAdapter.cancelDiscovery();
            setProgressBarIndeterminateVisibility(false);

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            String name=info.substring(0, info.length() - 17);
            setTitle("Sending file to "+name);
           	String temp="";
            
       	 try
    		{
    		FileInputStream fin = openFileInput(FILENAME);
    		int c;
    			if(fin.available()>0)
    			{
    				while( (c = fin.read()) != -1){
    					temp = temp + Character.toString((char)c);
    				}
    			}
    			fin.close();
    		}
    		
    		catch(Exception e)
    		{
    			
    		}
            
            String filePath = Environment.getExternalStorageDirectory().toString() + "/" + temp;
            //Toast.makeText(getApplicationContext(), "FilePath="+ filePath, Toast.LENGTH_SHORT).show();
            File f = new File ( filePath );
            
			if(f!=null && f.exists() && f.isDirectory())
            {
            	File[] files = f.listFiles();
			
            	fileList = new ArrayList<String>();
            	System.out.println ( "Total no of file : " + files.length );
            	for ( int i = 0 ; i < files.length ; i++ )
            	{
            		fileList.add( files[i].toString() );
            		System.out.println ( "Files : " + files[i].toString() );
            	}

            	//setTitle("Sending file to "+name);
            	try
            	{
            		for(int i=0;i<fileList.size();i++)
            		{
            			//Toast.makeText(getApplicationContext(), "File :"+ fileList.get(i), Toast.LENGTH_SHORT).show();
            			new SendingThread( fileList.get(i).toString().trim(),address,name).start();
   
            		}
            	}
            	catch(Exception e)
            	{
            		System.out.println("Error while sending " + e.toString());
            	}
              // 	setTitle("File sent to "+name);
            }
            
            else if(f!=null && f.exists() && f.isFile())
            {	
            	//setTitle("Sending file to "+name);
        		new SendingThread( filePath,address,name).start();
        	//	setTitle("File sent to "+name);
        		
            }	
            
          }
	
    };
    
    
    
    	private class SendingThread extends Thread {
    		
    		
    		private String filePath; 
    		private String deviceAddress;
    		private String name;
    		
    		public SendingThread ( String filePath, String deviceAddress , String name)
    		{
    			this.filePath = filePath; 
    			this.deviceAddress = deviceAddress;
    			this.name = name;
    		}
    		
    		
    		public void run()
    		{
    		  		
    	
    			try
    			{
    				Looper.prepare();
    				Thread.sleep(3000);
    				//setTitle("File sent to "+name);
    				ContentValues values = new ContentValues();
					values.put(MyBluetoothShare.URI, Uri.fromFile(new File(filePath)).toString());
					values.put(MyBluetoothShare.DESTINATION, deviceAddress);
					values.put(MyBluetoothShare.DIRECTION, MyBluetoothShare.DIRECTION_OUTBOUND);
					Long ts = System.currentTimeMillis();
					values.put(MyBluetoothShare.TIMESTAMP, ts);
				    getContentResolver().insert(MyBluetoothShare.CONTENT_URI, values);
				    Log.e("send clicked","send"+filePath+""+deviceAddress);
				
			
			    }
				catch ( Exception e )
				{
								System.out.println ( "Error while sending file " + e.toString() );
				}
    		
		
    		}
    	}

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
      
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
                
            }
        }
    };

	

}