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

import program_queue.Program;
/*
import pr2_hack_the_future.program_queue.msg.ProgramInfo;
import pr2_hack_the_future.program_queue.msg.Output;
import pr2_hack_the_future.program_queue.srv.GetProgram;
import pr2_hack_the_future.program_queue.srv.GetMyPrograms;
import pr2_hack_the_future.program_queue.srv.GetPrograms;
import pr2_hack_the_future.program_queue.srv.Login;
import pr2_hack_the_future.program_queue.srv.Logout;
import pr2_hack_the_future.program_queue.srv.ClearQueue;
import pr2_hack_the_future.program_queue.srv.CreateUser;
import pr2_hack_the_future.program_queue.srv.CreateProgram;
import pr2_hack_the_future.program_queue.srv.DequeueProgram;
import pr2_hack_the_future.program_queue.srv.GetOutput;
import pr2_hack_the_future.program_queue.srv.GetQueue;
import pr2_hack_the_future.program_queue.srv.QueueProgram;
import pr2_hack_the_future.program_queue.srv.RunProgram;
import pr2_hack_the_future.program_queue.srv.UpdateProgram;
*/
/**
 * @author damonkohler@google.com (Damon Kohler)
 * @author pratkanis@willowgarage.com (Tony Pratkanis)
 */
public class ScriptInterface extends RosAppActivity {
  
  private static final int token;
  private static final int EXISTING_PROGRAM_DIALOG = 1;
  private static final int PYTHON = 1;
  private static final int PUPPETSCRIPT = 0;
  private ProgressDialog progress;
  //private ArrayList<ProgramInfo> my_programs;
  private EditText name_field;
  private Spinner spinner;
  private EditText program_field;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    if (getIntent().hasExtra("stop")) {
      finish();
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
  
  /*
  private void getProgram(int id) {
    Log.i("ScriptInterface", "Run: GetProgram");
    try {
      ServiceClient<GetProgram.Request, GetProgram.Response> appServiceClient =
        getNode().newServiceClient(service, "package/GetProgram");  //TODO: fix package
      GetProgram.Request appRequest = new GetProgram.Request();
      appServiceClient.call(appRequest, new ServiceResponseListener<GetProgram.Response>() {
          @Override public void onSuccess(GetProgram.Response message) {
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
        getNode().newServiceClient(service, "package/GetMyPrograms");  //TODO: fix package
      GetMyPrograms.Request appRequest = new GetMyPrograms.Request();
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
  */

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
        if (program_names.length>0) { //check out how to get size of that list
              builder.setTitle("Select a Progam to Edit");
          builder.setSingleChoiceItems(program_names, 0, null)
           .setPositiveButton("Edit Selected", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                showProgram(my_programs[selectedPosition]);
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
  /*
  public void showPrograms(ProgramInfo info) {
    //get actual program from program info 
    //switch program type field, enter program name, put text in edit text
    Program program = getProgram(info.id); 
    name_field.setText(info.name);
    if (info.type == PYTHON) {
      spinner.setSelection(0);
    } else if (info.type == PUPPETSCRIPT) {
      spinner.setSelection(1);
    }
     
  }*/

}
