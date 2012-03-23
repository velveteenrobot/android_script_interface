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


/**
 * @author damonkohler@google.com (Damon Kohler)
 * @author pratkanis@willowgarage.com (Tony Pratkanis)
 */
public class ScriptInterface extends RosAppActivity {
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    setDefaultAppName("pr2_props_app/pr2_props");
    setDashboardResource(R.id.top_bar);
    setMainWindowResource(R.layout.scriptinterface_main);
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onNodeCreate(Node node) {
    super.onNodeCreate(node);
  }
  
  @Override
  protected void onNodeDestroy(Node node) {
    super.onNodeDestroy(node);
  }
  
  private void runService(String service) {
    Log.i("ScriptInterface", "Run: " + service);
    try {
      ServiceClient<Empty.Request, Empty.Response> appServiceClient =
        getNode().newServiceClient(service, "std_srvs/Empty");
      Empty.Request appRequest = new Empty.Request();
      appServiceClient.call(appRequest, new ServiceResponseListener<Empty.Response>() {
          @Override public void onSuccess(Empty.Response message) {
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

  /*
  //Callbacks
  public void highFiveLeft(View view) {
    runService("/pr2_props/high_five_left");
  }
  public void highFiveRight(View view) {
    runService("/pr2_props/high_five_right");
  }
  public void highFiveDouble(View view) { 
    runService("/pr2_props/high_five_double");
  }
  public void lowFiveLeft(View view) { 
    runService("/pr2_props/low_five_left");
  }
  public void lowFiveRight(View view) { 
    runService("/pr2_props/low_five_right");
  }
  public void poundLeft(View view) { 
    runService("/pr2_props/pound_left");
  }
  public void poundRight(View view) { 
    runService("/pr2_props/low_five_right");
  }
  public void poundDouble(View view) { 
    runService("/pr2_props/pound_double");
  }
  public void hug(View view) { 
    runService("/pr2_props/hug");
  }
  public void raiseSpine(View view) { 
    spineHeight = 0.31;
  }
  public void lowerSpine(View view) { 
    spineHeight = 0.0;
  }
  */  


  /** Creates the menu for the options */
//  @Override
//  public boolean onCreateOptionsMenu(Menu menu) {
//    MenuInflater inflater = getMenuInflater();
//    inflater.inflate(R.menu.pr2_props_menu, menu);
//    return true;
//  }

  /** Run when the menu is clicked. */
//  @Override
//  public boolean onOptionsItemSelected(MenuItem item) {
//    switch (item.getItemId()) {
//    case R.id.kill: //Shutdown if the user clicks kill
//      android.os.Process.killProcess(android.os.Process.myPid());
//      return true;
//    default:
//      return super.onOptionsItemSelected(item);
//    }
//  }
}
