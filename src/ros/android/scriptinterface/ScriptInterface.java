/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ros.android.scriptinterface;

import org.ros.exception.RemoteException;
import ros.android.activity.AppManager;
import ros.android.activity.RosAppActivity;
import android.os.Bundle;
import org.ros.node.Node;
import android.view.Window;
import android.view.WindowManager;
import android.util.Log;
import org.ros.node.service.ServiceClient;
import org.ros.node.topic.Publisher;
import org.ros.service.app_manager.StartApp;
import org.ros.node.service.ServiceResponseListener;
import android.widget.Toast;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import org.ros.service.std_srvs.Empty;
import org.ros.message.trajectory_msgs.JointTrajectory;
import org.ros.message.trajectory_msgs.JointTrajectoryPoint;
import java.util.ArrayList;
import org.ros.message.Duration;
import android.content.Intent;
import java.lang.Class;

import android.content.res.Resources;
import android.widget.TabHost;
import android.content.DialogInterface;
import android.app.TabActivity; 
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.app.Dialog;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import java.lang.String;
import java.util.Timer;
import java.util.TimerTask;
import android.widget.Button;
import android.widget.EditText;

import org.ros.message.program_queue.Program;

import org.ros.message.program_queue.ProgramInfo;
import org.ros.message.program_queue.Output;
import org.ros.service.program_queue.GetProgram;
import org.ros.service.program_queue.GetMyPrograms;
import org.ros.service.program_queue.GetPrograms;
import org.ros.service.program_queue.Login;
import org.ros.service.program_queue.Logout;
import org.ros.service.program_queue.ClearQueue;
import org.ros.service.program_queue.CreateUser;
import org.ros.service.program_queue.CreateProgram;
import org.ros.service.program_queue.DequeueProgram;
import org.ros.service.program_queue.GetOutput;
import org.ros.service.program_queue.GetQueue;
import org.ros.service.program_queue.QueueProgram;
import org.ros.service.program_queue.RunProgram;
import org.ros.service.program_queue.UpdateProgram;

/**
 * @author damonkohler@google.com (Damon Kohler)
 * @author pratkanis@willowgarage.com (Tony Pratkanis)
 */
public class ScriptInterface extends RosAppActivity {
  
  private static final int token = null;
  private static final int EXISTING_PROGRAM_DIALOG = 1;
  private static final int PYTHON = 1;
  private static final int PUPPETSCRIPT = 0;
  private ProgressDialog progress;
  private ArrayList<ProgramInfo> my_programs;
  private EditText name_field;
  private Spinner spinner;
  private EditText program_field;
  private Program current_program = null;
  private String username;
  private int type;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {

    Intent startingIntent = getIntent();
    if (startingIntent.hasExtra("token")) {
      token = startingIntent.getIntExtra("token");
    } else {
      finish();
    }
    if (startingIntent.hasExtra("stop")) {
      finish();
    }
    if (startingIntent.hasExtra("username")) {
      username = startingIntent.getStringExtra("username");
    }

    if (startingIntent.hasExtra("is_admin")) {
      is_admin = startingIntent.getBooleanExtra("is_admin");
    }
    setDefaultAppName("pr2_props_app/pr2_props");
    setDashboardResource(R.id.top_bar);
    setMainWindowResource(R.layout.main);
    super.onCreate(savedInstanceState);

    name_field = (EditText) findViewById(R.id.name_field);
    program_field = (EditText) findViewById(R.id.program_field);
        Button edit_btn = (Button) findViewById(R.id.edit_btn);
        edit_btn.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
            //show Progress Dialog
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                progress = ProgressDialog.show(ScriptInterface.this, "Loading", "Loading your programs...", true, false);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
              }
            });

            //showDialog(EXISTING_PROGRAM_DIALOG);
          }
        });

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
            this, R.array.program_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

       TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
        tabHost.setup();
        
        TabHost.TabSpec spec1=tabHost.newTabSpec("Tab 1");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Write Program");
        
        TabHost.TabSpec spec2=tabHost.newTabSpec("Tab 2");
        spec2.setIndicator("Tab 2");
        spec2.setContent(R.id.tab2);
        
        TabHost.TabSpec spec3=tabHost.newTabSpec("Tab 3");
        spec3.setIndicator("Tab 3");
        spec3.setContent(R.id.tab3);

        TabHost.TabSpec spec4=tabHost.newTabSpec("Tab 4");
        spec4.setIndicator("Tab 4");
        spec4.setContent(R.id.tab4);
        
        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);
        tabHost.addTab(spec4);

  }

  @Override
  protected void onNodeCreate(Node node) {
    super.onNodeCreate(node);
  }
  
  @Override
  protected void onNodeDestroy(Node node) {
    super.onNodeDestroy(node);
  }
  
 
  private void getProgram(long id) {
    Log.i("ScriptInterface", "Run: GetProgram");
    try {
      ServiceClient<GetProgram.Request, GetProgram.Response> appServiceClient =
        getNode().newServiceClient("/program_queue/getProgram", "program_queue/GetProgram");  
      GetProgram.Request appRequest = new GetProgram.Request();
      appRequest.id = token;
      appServiceClient.call(appRequest, new ServiceResponseListener<GetProgram.Response>() {
          @Override public void onSuccess(GetProgram.Response message) {
            current_program = message.program;
            program_field.setText(message.program.code);
          }

          @Override public void onFailure(RemoteException e) {
            //TODO: SHOULD ERROR
            Log.e("ScriptInterface", e.toString());
          }
        });
    } catch (Exception e) {
      //TODO: should error
      Log.e("ScriptInterface", e.toString());
    }
  }

  private void getMyPrograms(String service) {
    Log.i("ScriptInterface", "Run: GetMyPrograms");
    try {
      ServiceClient<GetMyPrograms.Request, GetMyPrograms.Response> appServiceClient =
        getNode().newServiceClient("/program_queue/getMyPrograms", "program_queue/GetMyPrograms");  
      GetMyPrograms.Request appRequest = new GetMyPrograms.Request();
      appRequest.token = token;
      appServiceClient.call(appRequest, new ServiceResponseListener<GetMyPrograms.Response>() {
          @Override public void onSuccess(GetMyPrograms.Response message) {
            showDialog(EXISTING_PROGRAM_DIALOG);
            my_programs = message.programs;
          }
        
          @Override public void onFailure(RemoteException e) {
            //TODO: SHOULD ERROR
            Log.e("ScriptInterface", e.toString());
          }
        });
    } catch (Exception e) {
      //TODO: should error
      Log.e("ScriptInterface", e.toString());
    }
  }

  private void updateProgram() {
    Log.i("ScriptInterface", "Run: UpdateProgram");
    try {
      ServiceClient<UpdateProgram.Request, UpdateProgram.Response> appServiceClient =
        getNode().newServiceClient("/program_queue/updateProgram", "program_queue/UpdateProgram");  
      UpdateProgram.Request appRequest = new UpdateProgram.Request();
      appRequest.token = token;
      appRequest.program = current_program;
      appServiceClient.call(appRequest, new ServiceResponseListener<UpdateProgram.Response>() {
          @Override public void onSuccess(UpdateProgram.Response message) {
          }

          @Override public void onFailure(RemoteException e) {
            //TODO: SHOULD ERROR
            Log.e("ScriptInterface", e.toString());
          }
        });
    } catch (Exception e) {
      //TODO: should error
      Log.e("ScriptInterface", e.toString());
    }
    
  }
 
  private void createProgram() {
    current_program.code = program_field.getText().toString();
    current_program.info.name = name_field.getText().toString();
    current_program.info.type = (byte) type;
    current_program.info.owner = username;
    
    Log.i("ScriptInterface", "Run: CreateProgram");
    try {
      ServiceClient<CreateProgram.Request, CreateProgram.Response> appServiceClient =
        getNode().newServiceClient("/program_queue/createProgram", "program_queue/CreateProgram"); 
      CreateProgram.Request appRequest = new CreateProgram.Request();
      appRequest.token = token;
      appServiceClient.call(appRequest, new ServiceResponseListener<CreateProgram.Response>() {
          @Override public void onSuccess(CreateProgram.Response message) {
            current_program.info.id = message.id;
            updateProgram();
          }

          @Override public void onFailure(RemoteException e) {
            //TODO: SHOULD ERROR
            Log.e("ScriptInterface", e.toString());
          }
        });
    } catch (Exception e) {
      //TODO: should error
      Log.e("ScriptInterface", e.toString());
    }
  }

  @Override 
  public void onBackPressed() {
    if (getIntent().getStringExtra("activity") != null) {
      Class<?> activityClass = null;
      Intent intent = new Intent();
      try {
        activityClass = Class.forName(getIntent().getStringExtra("activity"));
        intent = new Intent(ScriptInterface.this, activityClass);
      } catch (ClassNotFoundException e) {
        intent = ScriptInterface.this.getPackageManager().getLaunchIntentForPackage("org.ros.android.app_chooser");
      }
      intent.setAction("android.intent.action.MAIN");
      intent.addCategory("android.intent.category.LAUNCHER");
      intent.addCategory("android.intent.category.DEFAULT");
      startActivity(intent);
    } else {
      Intent intent = new Intent();
      intent = ScriptInterface.this.getPackageManager().getLaunchIntentForPackage("org.ros.android.app_chooser");
      intent.setAction("android.intent.action.MAIN");
      intent.addCategory("android.intent.category.LAUNCHER");
      intent.addCategory("android.intent.category.DEFAULT");
      startActivity(intent);
    }
  }




  @Override
  protected Dialog onCreateDialog(int id) {
    String[] program_names = { "program1", "program2", "program3" };
    final Dialog dialog;
    switch (id) {
      case EXISTING_PROGRAM_DIALOG:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (program_names.length>0) { 
              builder.setTitle("Select a Progam to Edit");
          builder.setSingleChoiceItems(program_names, 0, null)
           .setPositiveButton("Edit Selected", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                showProgram(my_programs.get(selectedPosition));
             }
           });
         builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int whichButton) {
             removeDialog(EXISTING_PROGRAM_DIALOG);
           }
         });
         dialog = builder.create();
         }
       else {
         builder.setTitle("No Programs to Edit. Create a New Program.");
         dialog = builder.create();
         final Timer t = new Timer();
         t.schedule(new TimerTask() {
           public void run() {
             removeDialog(EXISTING_PROGRAM_DIALOG);
           }
         }, 3*1000);
       }
        break;
      default:
        dialog = null;
    }
    return dialog;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.options_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.logout) {
      
      Intent intent = new Intent(this, LoginActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
      finish();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }
  
  public void logout() {
    Log.i("ScriptInterface", "Run: Logout");
    try {
      ServiceClient<Logout.Request, Logout.Response> appServiceClient =
        getNode().newServiceClient("/program_queue/logout", "program_queue/Logout");  
      Logout.Request appRequest = new Logout.Request();
      appRequest.token = token;
      appServiceClient.call(appRequest, new ServiceResponseListener<Logout.Response>() {
          @Override public void onSuccess(Logout.Response message) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
          }

          @Override public void onFailure(RemoteException e) {
            //TODO: SHOULD ERROR
            Log.e("ScriptInterface", e.toString());
          }
        });
    } catch (Exception e) {
      //TODO: should error
      Log.e("ScriptInterface", e.toString());
    }
  }

  public void showProgram(ProgramInfo info) {
    //get actual program from program info 
    //switch program type field, enter program name, put text in edit text
    getProgram(info.id); 
    name_field.setText(info.name);
    if (info.type == PYTHON) {
      spinner.setSelection(0);
      type = PYTHON;
    } else if (info.type == PUPPETSCRIPT) {
      spinner.setSelection(1);
      type = PUPPETSCRIPT;
    }
     
  }

  public void saveProgram() {
    if (current_program != null) {
      if (name_field.getText().toString() == current_program.info.name) {
        updateProgram();
      } else if (name_field.getText().toString() == "") {
        //alert dialog about needing name
      } else {
        //you are about to create a new program
        createProgram();
      }
      
    } else {
      createProgram();
    }
  }

  public void addToQueue() {
    //check if saved, if not prompt to save
    if (current_program.code == program_field.getText().toString()) {
      Log.i("ScriptInterface", "Run: QueueProgram");
      try {
        ServiceClient<QueueProgram.Request, QueueProgram.Response> appServiceClient =
          getNode().newServiceClient("/program_queue/queueProgram", "program_queue/QueueProgram");  
        QueueProgram.Request appRequest = new QueueProgram.Request();
        appRequest.token = token;
        appServiceClient.call(appRequest, new ServiceResponseListener<QueueProgram.Response>() {
            @Override public void onSuccess(QueueProgram.Response message) {
              //tell user which position their item is in the queue, message.queue_position
            }

            @Override public void onFailure(RemoteException e) {
              //TODO: SHOULD ERROR
              Log.e("ScriptInterface", e.toString());
            }
          });
      } catch (Exception e) {
        //TODO: should error
        Log.e("ScriptInterface", e.toString());
      }
    } else {
      //prompt to save
      saveProgram(); 
    }  
  } 

  public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    @Override
    public void onItemSelected(AdapterView<?> parent,
        View view, int pos, long id) {
          if (pos == 0) {
            type = PYTHON;
          } else if (pos == 1) {
            type = PUPPETSCRIPT;
          }
    }
    @Override
    public void onNothingSelected(AdapterView parent) {
      // Do nothing.
    }

  }



}
