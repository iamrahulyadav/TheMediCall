package themedicall.com;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import themedicall.com.Adapter.BlogPostForSpecificCategoryAdapter;
import themedicall.com.GetterSetter.BlogPostGetterSetter;

import java.util.ArrayList;

public class BlogPostForSpecificCategory extends AppCompatActivity {
    RecyclerView recycler_view_post_for_specific_category ;
    ArrayList<BlogPostGetterSetter> postArray ;
    String categoryName ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post_for_specific_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getPostListFromAdapter();
        initiate();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initiate()
    {
        recycler_view_post_for_specific_category = findViewById(R.id.recycler_view_post_for_specific_category);
        recycler_view_post_for_specific_category.setHasFixedSize(true);
        recycler_view_post_for_specific_category.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        BlogPostForSpecificCategoryAdapter blogPostForSpecificCategory = new BlogPostForSpecificCategoryAdapter(this , postArray);
        recycler_view_post_for_specific_category.setAdapter(blogPostForSpecificCategory);
        blogPostForSpecificCategory.notifyDataSetChanged();
    }

    public void getPostListFromAdapter()
    {
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if (bd != null) {

            postArray =(ArrayList) bd.getParcelableArrayList("postList");
            categoryName = (String) bd.get("categoryName");
            getSupportActionBar().setTitle(categoryName);
            Log.e("tag" , "post list size in post for specific category : "+postArray.size());

        }
    }

}
