package com.omsworld.familycare.Shopping;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.omsworld.familycare.R;
import com.omsworld.familycare.Shopping.InWeb.ShoppingSitesList_Fr;
import com.omsworld.familycare.activity.MainActivity;


public class ShoppingMain_Ac extends AppCompatActivity implements android.support.v7.app.ActionBar.TabListener {

    GroceryList_Fr fragmentTab1 = new GroceryList_Fr();
    ShoppedHistory_Fr fragmentTab2 = new ShoppedHistory_Fr();
 //   ShoppingSitesList_Fr fragmentTab3 = new ShoppingSitesList_Fr();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_ac);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);

        // Three tab to display in actionbar
        ab.addTab(ab.newTab().setText("CheckList").setTabListener(this));
        ab.addTab(ab.newTab().setText("History").setTabListener(this));
        //ab.addTab(ab.newTab().setText("EXTRA").setTabListener(this));
    }

    @Override
    public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction ft) {

        //Called when a tab is selected
        int nTabSelected = tab.getPosition();
        switch (nTabSelected) {
            case 0:
                ft.replace(R.id.fragment_container, fragmentTab1);
                break;
            case 1:
                ft.replace(R.id.fragment_container, fragmentTab2);
                break;
            /*case 2:
                ft.replace(R.id.fragment_container, fragmentTab3);
                break;*/
        }
    }

    @Override
    public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // Called when a tab unselected.
        //fragmentTransaction.remove(fragment);
    }


    @Override
    public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        // Called when a tab is selected again.
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ShoppingMain_Ac.this, MainActivity.class));
        finish();
    }
}
