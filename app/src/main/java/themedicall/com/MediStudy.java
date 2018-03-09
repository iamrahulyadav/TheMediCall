package themedicall.com;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import themedicall.com.MediPediaAdapter.GenericBrandCompanyAdapter;
import themedicall.com.MediPediaGetterSetter.ListInfo;

import java.util.ArrayList;

public class MediStudy extends NavigationDrawer {
    RecyclerView recyclerViewMediPedia;
     ArrayList<ListInfo> mediStudy;
    GridLayoutManager gridLayoutManager;
    GenericBrandCompanyAdapter genericBrandCompanyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_medi_study);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.activity_medi_study, null, false);
        drawer.addView(view, 0);


        initiate();
    }

    public void initiate()
    {
        recyclerViewMediPedia = (RecyclerView) findViewById(R.id.recycler_view_for_medi_study);
        recyclerViewMediPedia.setHasFixedSize(true);
        recyclerViewMediPedia.setLayoutManager(new GridLayoutManager(MediStudy.this , 2 ,GridLayoutManager.VERTICAL, false ));
        mediStudy = new ArrayList<ListInfo>();


        mediStudy.add(new ListInfo("1","Anatomy"));
        mediStudy.add(new ListInfo("1","Physiology"));
        mediStudy.add(new ListInfo("1","Biochemistry"));
        mediStudy.add(new ListInfo("1","Pathology"));
        mediStudy.add(new ListInfo("1","Pharmacology"));
        mediStudy.add(new ListInfo("1","Ophthalmology"));
        mediStudy.add(new ListInfo("1","ENT"));
        mediStudy.add(new ListInfo("1","Surgery"));
        mediStudy.add(new ListInfo("1","Medicine"));
        mediStudy.add(new ListInfo("1","Paediatrics"));
        mediStudy.add(new ListInfo("1","Islamic studies"));
        mediStudy.add(new ListInfo("1","Pakistan studies"));
        mediStudy.add(new ListInfo("1","Community medicine"));
        mediStudy.add(new ListInfo("1","Behavioral sciences"));
        mediStudy.add(new ListInfo("1","Forensic medicine and toxicology"));
        mediStudy.add(new ListInfo("1","Gynecology and obstetric"));


        genericBrandCompanyAdapter = new GenericBrandCompanyAdapter(MediStudy.this ,mediStudy , "rtrt");
        recyclerViewMediPedia.setAdapter(genericBrandCompanyAdapter);
        genericBrandCompanyAdapter.notifyDataSetChanged();

    }

}
