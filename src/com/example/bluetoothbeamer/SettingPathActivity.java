package com.example.bluetoothbeamer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingPathActivity extends Activity {
	
	private ArrayAdapter<String> adapter;
	private AutoCompleteTextView name;

	 
//	 EditText name;
	 String FILENAME = "fileList";
	 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 
        // Set up the window layout
       
        setContentView(R.layout.setting);
               
        
        name =   (AutoCompleteTextView) findViewById(R.id.autoFilename);
        		
		
        boolean state= isExternalStorageWritable();
        if(!state)
        {
        	Toast.makeText(getApplicationContext(), "No SD card is Available to select file", Toast.LENGTH_SHORT).show();
        	return;
        }
                
        String filePath = Environment.getExternalStorageDirectory().toString();
        final File f = new File ( filePath );
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,f.list());
        name.setText("");
        name.setAdapter(adapter);
        name.setThreshold(1);
        
    
        String temp="";      
        final Button set=(Button) findViewById(R.id.btnSet);
        final Button done=(Button) findViewById(R.id.btnDone);
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
        name.setText(temp);
        name.dismissDropDown();
        done.setEnabled(false);
        name.setEnabled(false);
        set.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				name.setText("");
				name.setEnabled(true);
				name.showDropDown();
		        done.setEnabled(true);
		        set.setEnabled(false);
				
			}
		});
        
	
  done.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String fname =name.getText().toString() ;	
				
				String filePath = Environment.getExternalStorageDirectory().toString() + "/" + fname;
		        File f = new File ( filePath );
		        
		        if(fname.equals(""))
		        {
		        	Toast.makeText(getApplicationContext(), "Set Path", Toast.LENGTH_SHORT).show();
	            	name.requestFocus();
		        }
		        		            
		        else if(!(f.exists()))
	            {	
	            	Toast.makeText(getApplicationContext(), "Folder/file unavailable", Toast.LENGTH_SHORT).show();
	            	name.requestFocus();
	            }
	            
	            else if ( !f.canRead() )
				{
	            	Toast.makeText(getApplicationContext(), "Folder/file is not readable", Toast.LENGTH_SHORT).show();
	            	name.requestFocus();
				}
	            else
	            {
				
				
				try
				{
				FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
				fos.write(fname.getBytes());
				fos.close();
				}
				catch(Exception e)
				{
					
				}
							
				Intent intentMain=new Intent(getApplicationContext(),MainActivity.class);
				startActivity(intentMain);
				finish();
				
			}
			}
		});
        
    }
	
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	
	
	
	
	 @Override
	protected void onPause() {
		super.onPause();
		Intent intentMain=new Intent(getApplicationContext(),MainActivity.class);
		startActivity(intentMain);
		finish();
	}
	   
	       
}
