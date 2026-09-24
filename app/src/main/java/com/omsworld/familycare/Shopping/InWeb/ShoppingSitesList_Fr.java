package com.omsworld.familycare.Shopping.InWeb;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.omsworld.familycare.R;
import com.omsworld.familycare.api_call.CallWebService;
import com.omsworld.familycare.api_call.CommonFunctions;
import com.omsworld.familycare.api_call.Controller;
import com.omsworld.familycare.api_call.MyServiceListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nityanand.p on 8/21/2017.
 */

public class ShoppingSitesList_Fr extends Fragment {

    CommonFunctions cmf;
    GridView gridView;
    ArrayList<ShoppingModel> gridArray = new ArrayList<ShoppingModel>();
    RecyclerView RV_categories;
    //-------------------------------
    Controller controller;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.shoping_sites_fr, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cmf = new CommonFunctions(getActivity());
        initilize();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void initilize() {

        // for category work... ui inflate and other view....
        RV_categories = (RecyclerView) rootView.findViewById(R.id.RV_categories);
        GridLayoutManager recyclerViewLayoutManager = new GridLayoutManager(getActivity(), 3);
        RV_categories.setLayoutManager(recyclerViewLayoutManager);

        gridView = (GridView) rootView.findViewById(R.id.grdView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
               /* Fragment fragment = new Shop_Detail_web_Fr();

                cmf.bdl.putString("siteName", gridArray.get(position).getTitle());
                cmf.bdl.putString("siteUrl", gridArray.get(position).getUrl());
                fragment.setArguments(cmf.bdl);
                //cmf.replaceFragment(fragment);

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.commit();*/


                cmf.bdl.putString("siteName", gridArray.get(position).getTitle());
                cmf.bdl.putString("siteUrl", gridArray.get(position).getUrl());
                Intent intent = new Intent(getActivity(), ShopInWeb_Ac.class);
                intent.putExtras(cmf.bdl);
                startActivity(intent);


            }
        });
        listOfShopping("YES");
        controller = new Controller() {
            @Override
            public void callback(String id) {
                gridArray.clear();
                listOfShopping(id);
            }
        };
    }

    private void listOfShopping(final String main_category) {
        if (gridArray.size() > 0) {
            ShoppingSitesAdapter CompanyAdapters = new ShoppingSitesAdapter(getActivity(), R.layout.shopping_sites_row, gridArray);
            gridView.setAdapter(CompanyAdapters);
            CompanyAdapters.notifyDataSetChanged();
            return;
        }
        String Url = cmf.urlList.shopping_site_list;
        if (main_category.equals("YES")) {
            Url = cmf.urlList.shopping_category;
        }

        new CallWebService(getActivity(), Url, cmf.shopping_site_list(main_category), new MyServiceListener() {
            @Override
            public void onSuccess(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    String Status = jsonObject.optString("success");
                    String Message = jsonObject.optString("message");
                    if (Status.equals("1")) {

                        if (main_category.equals("YES")) {
                            //do for category list product
                            JSONArray home_cat_jsonArray = jsonObject.getJSONArray("shopping_category");
                            HeadCategoryAdapter adapter = new HeadCategoryAdapter(getActivity(), home_cat_jsonArray, R.layout._item, controller);
                            RV_categories.setAdapter(adapter);
                            return;
                        }
                        //-----------------------Add new Data
                        JSONArray DataArray = jsonObject.optJSONArray("shopping_site_list");
                        for (int i = 0; i < DataArray.length(); i++) {
                            JSONObject Dataobj = DataArray.getJSONObject(i);
                            String id = Dataobj.optString("id");
                            String name = Dataobj.optString("name");
                            String url = Dataobj.optString("url");
                            String image = Dataobj.optString("image");

                            ShoppingModel model = new ShoppingModel();
                            model.setId(id);
                            model.setTitle(name);
                            model.setImage(image);
                            model.setUrl(url);

                            gridArray.add(model);
                        }
                        ShoppingSitesAdapter CompanyAdapters = new ShoppingSitesAdapter(getActivity(), R.layout.shopping_sites_row, gridArray);
                        gridView.setAdapter(CompanyAdapters);
                        //----------------------

                    } else {
                        Toast.makeText(getActivity(), Message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                }
            }

            @Override
            public void onFailed() {
            }
        });
    }


}

