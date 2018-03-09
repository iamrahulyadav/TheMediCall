package themedicall.com.Adapter;



import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import themedicall.com.BlogPostForSpecificCategory;
import themedicall.com.GetterSetter.BlogPostGetterSetter;
import themedicall.com.R;


import java.util.ArrayList;
import java.util.List;

public class BlogPostAdapter extends RecyclerView.Adapter<BlogPostAdapter.MyViewHolder>  {

    private List<BlogPostGetterSetter> blogPostList;
    private Context mContext;
    private String title;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView postImage ;
        public TextView postTitle , postId;
        private ImageView iv_post;

        public MyViewHolder(final View view) {
            super(view);


            postImage = (TextView) view.findViewById(R.id.postImage);
            postTitle = (TextView) view.findViewById(R.id.postTitle);
            postId = (TextView) view.findViewById(R.id.postId);
            iv_post = (ImageView) view.findViewById(R.id.iv_post);



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext() , BlogPostForSpecificCategory.class);
                    intent.putParcelableArrayListExtra("postList" , (ArrayList<? extends Parcelable>) blogPostList);
                    intent.putExtra("categoryName" , title);
                    v.getContext().startActivity(intent);
                    Log.e("tag" , "post list size in adapter : "+blogPostList.size());
                }
            });

        }
    }

    public BlogPostAdapter(Context context, List<BlogPostGetterSetter> adList, String categoryTitle) {
        this.mContext = context;
        this.blogPostList = adList;
        this.title = categoryTitle;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.pharmacy_custom_row, parent, false);
//        return new MyViewHolder(itemView);

        MyViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        Log.e("TAg", "the view type : " + viewType);

        switch (viewType) {
            case ITEM:
                // viewHolder = getViewHolder(parent, inflater);

                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_blog_post_list, parent, false);
                viewHolder = new MyViewHolder(itemView);

                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.progress_item_at_end, parent, false);
                viewHolder = new MyViewHolder(v2);
                break;

        }




        return viewHolder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {


            switch (getItemViewType(position)) {

                case ITEM:
                    BlogPostGetterSetter ad = blogPostList.get(position);
                    Picasso.with(mContext).load(ad.getUrl()).into(holder.iv_post);
                    holder.postImage.setText(ad.getUrl());
                    holder.postId.setText(ad.getId());
                    holder.postTitle.setText(ad.getTitle());


                    break;
                case LOADING:
                    //do Nothing
                    break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return blogPostList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return (position == blogPostList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public BlogPostGetterSetter getItem(int position) {
        return blogPostList.get(position);
    }

    public void add(BlogPostGetterSetter mc) {
        blogPostList.add(mc);
        notifyItemInserted(blogPostList.size() - 1);
    }

    public void addAll(List<BlogPostGetterSetter> mcList) {
        for (BlogPostGetterSetter mc : mcList) {
            add(mc);
        }
    }



}
