package com.webmne.salestracker.agent.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.webmne.salestracker.R;
import com.webmne.salestracker.agent.model.AgentModel;
import com.webmne.salestracker.helper.PrefUtils;
import com.webmne.salestracker.widget.TfTextView;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 12-08-2016.
 */
public class AgentsListAdapter extends RecyclerView.Adapter<AgentsListAdapter.AgentViewHolder> {

    private Context context;
    private ArrayList<AgentModel> agentList;
    private Animation flipAnim, flipReverse;
    private onSelectionListener onSelectionListener;

    public void setAgentList(ArrayList<AgentModel> agentList) {
        this.agentList = new ArrayList<>();
        this.agentList = agentList;
        notifyDataSetChanged();
    }

    public AgentsListAdapter(Context context, ArrayList<AgentModel> agentList, onSelectionListener onSelectionListener) {
        this.context = context;
        this.agentList = agentList;
        this.onSelectionListener = onSelectionListener;
        flipAnim = AnimationUtils.loadAnimation(context, R.anim.flip_anim);
        flipReverse = AnimationUtils.loadAnimation(context, R.anim.flip_anim);
    }

    @Override
    public AgentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_agent, parent, false);
        return new AgentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AgentViewHolder holder, int position) {
        AgentModel agentModel = agentList.get(position);
        holder.setAgentDetails(agentModel);
    }

    @Override
    public int getItemCount() {
        return agentList.size();
    }

    class AgentViewHolder extends RecyclerView.ViewHolder {

        TfTextView txtAgentName;
        MaterialLetterIcon letterIcon;
        ImageView imgCheck;
        LinearLayout parentView;

        public AgentViewHolder(View itemView) {
            super(itemView);
            txtAgentName = (TfTextView) itemView.findViewById(R.id.txtAgentName);
            letterIcon = (MaterialLetterIcon) itemView.findViewById(R.id.letterIcon);
            imgCheck = (ImageView) itemView.findViewById(R.id.imgCheck);
            parentView = (LinearLayout) itemView.findViewById(R.id.parentView);

        }

        public void setAgentDetails(final AgentModel agentModel) {

            txtAgentName.setText(agentModel.getAgentName());
            letterIcon.setLetter(agentModel.getAgentName().substring(0, 1));
            letterIcon.setShapeColor(agentModel.getColor());
            imgCheck.setBackgroundColor(agentModel.getColor());

            if (agentModel.isChecked()) {
                imgCheck.setVisibility(View.VISIBLE);
                letterIcon.setVisibility(View.GONE);
                parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));
            } else {
                imgCheck.setVisibility(View.GONE);
                letterIcon.setVisibility(View.VISIBLE);
                parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }

            imgCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reverseFlip();
                }

                private void reverseFlip() {
                    PrefUtils.setAgent(context, agentModel);
                    onSelectionListener.onSelect(false);
                    agentModel.setChecked(false);
                    imgCheck.startAnimation(flipReverse);
                    parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    flipReverse.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            imgCheck.setVisibility(View.GONE);
                            letterIcon.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

            });

        }
    }

    public interface onSelectionListener {
        void onSelect(boolean isSelect);
    }

}
