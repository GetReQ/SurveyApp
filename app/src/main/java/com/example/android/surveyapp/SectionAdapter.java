package com.example.android.surveyapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.surveyapp.network.Section;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {
    private List<Section> mSections;

    final private ListItemClickListener mOnClickListener;   //item click event

    //interface for an item click event
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public SectionAdapter(ListItemClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.section_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup,
                shouldAttachToParentImmediately);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return (mSections != null)? mSections.size() : 0;
    }

    public void setSectionData(Iterable<Section> sections) {
        mSections = new ArrayList<>();
        for (Section s : sections)
            mSections.add(s);
        notifyDataSetChanged();
    }

    class SectionViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        TextView sectionLabel;
        TextView sectionLength;

        public SectionViewHolder(View itemView) {
            super(itemView);
            sectionLabel = (TextView) itemView.findViewById(R.id.tv_section_label);
            sectionLength = (TextView) itemView.findViewById(R.id.tv_section_length);
            itemView.setOnClickListener(this);
        }

        void bind(int sectionPosition) {
            if (mSections != null &&
                    sectionPosition >= 0 && sectionPosition < mSections.size()) {
                Section section = mSections.get(sectionPosition);
                sectionLabel.setText(section.Label);
                sectionLength.setText(String.valueOf(section.Length) + "m");
            }
        }

        /**
         * Get the item clicked on within the recycler list.
         * @param view
         */
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

}
