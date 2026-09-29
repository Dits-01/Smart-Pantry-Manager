package com.example.smartpantrymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.ShelfItem;

import java.util.Locale;

public class ShelfAdapter extends ListAdapter<ShelfItem, ShelfAdapter.ShelfViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ShelfItem item);
        void onDeleteClick(ShelfItem item);
    }

    public ShelfAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<ShelfItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<ShelfItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull ShelfItem oldItem, @NonNull ShelfItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ShelfItem oldItem, @NonNull ShelfItem newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getCategory().equals(newItem.getCategory()) &&
                    oldItem.getQuantity() == newItem.getQuantity() &&
                    ((oldItem.getUnit() == null ? "" : oldItem.getUnit()).equals(newItem.getUnit() == null ? "" : newItem.getUnit())) &&
                    oldItem.getShelfLocation().equals(newItem.getShelfLocation()) &&
                    oldItem.getNotes().equals(newItem.getNotes());
        }
    };

    @NonNull
    @Override
    public ShelfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shelf_row, parent, false);
        return new ShelfViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ShelfViewHolder holder, int position) {
        ShelfItem currentItem = getItem(position);
        Context context = holder.itemView.getContext();

        String[] cardInfo = new String[3];
        cardInfo[0] = context.getString(R.string.shelf_adapter_category_placeholder, currentItem.getCategory());
        double q = currentItem.getQuantity();
        String qtyFormatted = q == (long) q ? String.format(Locale.getDefault(), "%d", (long) q) : String.valueOf(q);
        String unit = currentItem.getUnit();
        String qtyWithUnit = qtyFormatted + (unit != null && !unit.isEmpty() ? " " + unit : "");
        cardInfo[1] = context.getString(R.string.shelf_adapter_quantity_placeholder, qtyWithUnit);
        cardInfo[2] = context.getString(R.string.shelf_adapter_shelf_placeholder, currentItem.getShelfLocation());

        holder.textViewName.setText(currentItem.getName());
        holder.textViewCategory.setText(cardInfo[0]);
        holder.textViewQuantity.setText(cardInfo[1]);
        holder.textViewLocation.setText(cardInfo[2]);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentItem);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(currentItem);
            }
        });
    }

    public ShelfItem getItemAt(int position) {
        return getItem(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ShelfViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewCategory;
        private final TextView textViewQuantity;
        private final TextView textViewLocation;
        private final View btnDelete;

        public ShelfViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_item_name);
            textViewCategory = itemView.findViewById(R.id.text_view_item_category);
            textViewQuantity = itemView.findViewById(R.id.text_view_item_quantity);
            textViewLocation = itemView.findViewById(R.id.text_view_item_location);
            btnDelete = itemView.findViewById(R.id.button_delete_item);
        }
    }
}
