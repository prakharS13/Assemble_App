package com.prakharsaxena.livesaidarshan;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context mContext;
    private List<NewProduct> mNewProduct;
    private OnItemClickListener mListener;

    public ProductAdapter(Context context, List<NewProduct> newProduct){
        mContext=context;
        mNewProduct=newProduct;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.productcard, parent,false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
       NewProduct uploadCurrent=mNewProduct.get(position);
       holder.productNameTV.setText(uploadCurrent.getProductName());
       holder.productQuantityTV.setText(uploadCurrent.getProductQuantity()+" Qty");
       holder.productTagTV.setText("Tags : "+uploadCurrent.getProductTag());
       holder.productNoteTV.setText("Notes : "+uploadCurrent.getProductNote());
        Picasso.get()
               .load(uploadCurrent.getProductUrl())
                .placeholder(R.mipmap.ic_launcher)
               .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mNewProduct.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView productNameTV,productQuantityTV,productTagTV,productNoteTV;
        public ImageView imageView;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTV= itemView.findViewById(R.id.productCardItemName);
            productQuantityTV= itemView.findViewById(R.id.productCardItemQty);
            productTagTV= itemView.findViewById(R.id.productCardItemTag);
            productNoteTV= itemView.findViewById(R.id.productCardItemNote);
            imageView= itemView.findViewById(R.id.productCardImageView);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View v) {
            if(mListener!=null){
                int position=getAdapterPosition();
                if(position!=RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem edit=menu.add(Menu.NONE, 1, 1,"Edit");
            MenuItem share=menu.add(Menu.NONE, 2, 2,"Share");
            MenuItem delete=menu.add(Menu.NONE, 3, 3,"Delete");

            edit.setOnMenuItemClickListener(this);
            share.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mListener!=null){
                int position=getAdapterPosition();
                if(position!=RecyclerView.NO_POSITION){
                 switch(item.getItemId()){
                     case 1:
                         mListener.onEditClick(position);
                         return true;
                     case 2:
                         mListener.onShareClick(position);
                         return true;
                     case 3:
                         mListener.onDeleteClick(position);
                         return true;
                 }
                }
            }
            return false;
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
        void onEditClick(int position);
        void onShareClick(int position);
        void onDeleteClick(int positon);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }
}
