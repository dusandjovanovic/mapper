package com.dushan.dev.mapper.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dushan.dev.mapper.Adapters.MarkersAdapter;
import com.dushan.dev.mapper.Data.Marker;
import com.dushan.dev.mapper.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int RECENT_TAB = 1;
    private final int SAVED_TAB = 2;
    private final int VIEW_TAB = 3;

    private Toolbar mToolbar;
    private TextView recentTab, savedTab, viewTab;
    private View recentHighlight, savedHighlight, viewHighlight;
    private RecyclerView mainRecycler;
    private int selectedTab;
    private MarkersAdapter markersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectedTab = RECENT_TAB;
        connectViews();
    }

    private void connectViews(){
        mToolbar = findViewById(R.id.mainToolbar);
        recentTab = findViewById(R.id.recentTab);
        savedTab = findViewById(R.id.savedTab);
        viewTab = findViewById(R.id.viewTab);
        recentHighlight = findViewById(R.id.recentHighlight);
        savedHighlight = findViewById(R.id.savedHighlight);
        viewHighlight = findViewById(R.id.viewHighlight);
        mainRecycler = findViewById(R.id.mainRecycler);
        setupTheActivity();
    }

    private void setupTheActivity(){
       setSupportActionBar(mToolbar);
       getSupportActionBar().setDisplayShowTitleEnabled(false);
        setupListeners();
        initiateRecyclerView(pribaviPodatke1());
    }

    private void initiateRecyclerView(List<Marker> markerList){
        if (markersAdapter == null) {
            markersAdapter = new MarkersAdapter(getApplicationContext(), markerList);
            mainRecycler.setLayoutManager(new LinearLayoutManager(this));
            mainRecycler.setAdapter(markersAdapter);
        } else {
            markersAdapter.setmMarkerList(markerList);
            markersAdapter.notifyDataSetChanged();
        }

    }

    private List<Marker> pribaviPodatke1(){
        ///// dodjes do podataka u formatu lista markera;
        List listamarkera = new ArrayList<>();
        Marker instanca1 = new Marker();
        instanca1.setCategory("travel");
        instanca1.setName("marker name 1");
        instanca1.setDescription("lerem ispumvansfad sdhasdiasda asdd s sadsa dasd");
        instanca1.setImageURL("https://www.juznevesti.com/uploads/assets/2014/07/11/39378/800x0_20140612-003518-LLS.jpg");
        listamarkera.add(instanca1);

        Marker instanca2 = new Marker();
        instanca2.setCategory("nature");
        instanca2.setName("marker name 2");
        instanca2.setDescription("lerem iasdasdasdd s sadsa dasd");
        instanca2.setImageURL("https://www.nis.eu/lat/wp-content/uploads/sites/2/2017/11/NIS-PC1.jpg");
        listamarkera.add(instanca2);

        return  listamarkera;
    }

    private List<Marker> pribaviPodatke2(){
        ///// dodjes do podataka u formatu lista markera;
        List listamarkera = new ArrayList<>();
        Marker instanca1 = new Marker();
        instanca1.setCategory("travel");
        instanca1.setName("marker naasdasdme 1");
        instanca1.setDescription("lerem ispumvansfad sdhasdiasda asdd s sadsa dasd");
        instanca1.setImageURL("https://www.juznevesti.com/uploads/assets/2014/07/11/39378/800x0_20140612-003518-LLS.jpg");
        listamarkera.add(instanca1);

        Marker instanca2 = new Marker();
        instanca2.setCategory("nature");
        instanca2.setName("marker asdsadad 2");
        instanca2.setDescription("lerem iasdasdasdd s sadsa dasd");
        instanca2.setImageURL("https://www.nis.eu/lat/wp-content/uploads/sites/2/2017/11/NIS-PC1.jpg");
        listamarkera.add(instanca2);

        return  listamarkera;
    }

    private void setupListeners(){
        recentTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab = RECENT_TAB;

                initiateRecyclerView(pribaviPodatke1());

                repaintTabs();
            }
        });

        savedTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab = SAVED_TAB;

                initiateRecyclerView(pribaviPodatke2());

                repaintTabs();
            }
        });

        viewTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTab = VIEW_TAB;
                repaintTabs();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    private void repaintTabs(){
        recentTab.setTextColor(getApplicationContext().getResources().getColor(R.color.unselectedTabTextColor));
        savedTab.setTextColor(getApplicationContext().getResources().getColor(R.color.unselectedTabTextColor));
        viewTab.setTextColor(getApplicationContext().getResources().getColor(R.color.unselectedTabTextColor));
        recentHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.lightBlue));
        savedHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.lightBlue));
        viewHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.lightBlue));
        switch (selectedTab) {
            case (RECENT_TAB):
                recentTab.setTextColor(getApplicationContext().getResources().getColor(R.color.altTextColor));
                recentHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.tabHighlightColor));
                break;
            case (SAVED_TAB):
                savedTab.setTextColor(getApplicationContext().getResources().getColor(R.color.altTextColor));
                savedHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.tabHighlightColor));
                break;
            case (VIEW_TAB):
                viewTab.setTextColor(getApplicationContext().getResources().getColor(R.color.altTextColor));
                viewHighlight.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.tabHighlightColor));
                break;
                default:
                    break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
          /*  case R.id.search:
                Toast.makeText(this, "Refresh selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;*/
            default:
                break;
        }

        return true;
    }


}
