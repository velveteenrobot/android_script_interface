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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author damonkohler@google.com (Damon Kohler)
 * @author pratkanis@willowgarage.com (Tony Pratkanis)
 */
public class LoginActivity extends RosAppActivity {
  
  private EditText username_field;
  private EditText pw_field;
  private Button login_btn;
  private Button cancel_btn;
  private Button newuser_btn;
  private String token = "token";

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    setDefaultAppName("pr2_props_app/pr2_props");
    setDashboardResource(R.id.top_bar);
    setMainWindowResource(R.layout.login_main);
    super.onCreate(savedInstanceState);

    username_field = (EditText) this.findViewById(R.id.username_field);
    pw_field = (EditText) this.findViewById(R.id.pw_field);
    login_btn = (Button) this.findViewById(R.id.login_btn);
    cancel_btn = (Button) this.findViewById(R.id.cancel_btn);
    newuser_btn = (Button) this.findViewById(R.id.newuser_btn);
    login_btn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //Service call to Login 
        //get token back, switch activities
        if (token == "token") {
          Toast.makeText(LoginActivity.this, "Login!", Toast.LENGTH_LONG).show();
          //Intent intent = getPackageManager().getLaunchIntentForPackage("org.ros.android.scriptinterface.ScriptInterface");
          Intent intent = new Intent(LoginActivity.this, ScriptInterface.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          startActivity(intent);
        }
      }
    });
    newuser_btn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //Service call to CreateUser
        if (token == "token") {
          //Intent intent = getPackageManager().getLaunchIntentForPackage("org.ros.android.scriptinterface.ScriptInterface");
          Intent intent = new Intent(v.getContext(), ScriptInterface.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          startActivity(intent); 
        }
      }
    });
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
    Log.i("LoginActivity", "Run: " + service);
    try {
      ServiceClient<Empty.Request, Empty.Response> appServiceClient =
        getNode().newServiceClient(service, "std_srvs/Empty");
      Empty.Request appRequest = new Empty.Request();
      appServiceClient.call(appRequest, new ServiceResponseListener<Empty.Response>() {
          @Override public void onSuccess(Empty.Response message) {
          }
        
          @Override public void onFailure(RemoteException e) {
            //TODO: SHOULD ERROR
            Log.e("LoginActivity", e.toString());
          }
        });
    } catch (Exception e) {
      //TODO: should error
      Log.e("LoginActivity", e.toString());
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

  /* Creates the menu for the options */
  //@Override
  //public boolean onCreateOptionsMenu(Menu menu) {
  //  MenuInflater inflater = getMenuInflater();
  //  inflater.inflate(R.menu.pr2_props_menu, menu);
  //  return true;
  //}

  /* Run when the menu is clicked. */
  //@Override
  //public boolean onOptionsItemSelected(MenuItem item) {
  //  switch (item.getItemId()) {
  //  case R.id.kill: //Shutdown if the user clicks kill
  //    android.os.Process.killProcess(android.os.Process.myPid());
  //    return true;
  //  default:
  //    return super.onOptionsItemSelected(item);
  //  }
  //}

}
