package com.webmne.salestracker.agent.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.webmne.salestracker.R;
import com.webmne.salestracker.api.model.AgentModel;
import com.webmne.salestracker.helper.Functions;
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

        TfTextView txtAgentName, letterIcon, txtLocaion;
        ImageView imgCheck, imgCall, imgEmail;
        LinearLayout parentView;

        public AgentViewHolder(View itemView) {
            super(itemView);
            txtAgentName = (TfTextView) itemView.findViewById(R.id.txtAgentName);
            letterIcon = (TfTextView) itemView.findViewById(R.id.letterIcon);
            txtLocaion = (TfTextView) itemView.findViewById(R.id.txtLocaion);
            imgCheck = (ImageView) itemView.findViewById(R.id.imgCheck);
            imgCall = (ImageView) itemView.findViewById(R.id.imgCall);
            imgEmail = (ImageView) itemView.findViewById(R.id.imgEmail);
            parentView = (LinearLayout) itemView.findViewById(R.id.parentView);

        }

        public void setAgentDetails(final AgentModel agentModel) {

            txtAgentName.setText(agentModel.getName());
            letterIcon.setText(agentModel.getName().substring(0, 1));

            if (TextUtils.isEmpty(agentModel.getMobileNo())) {
                imgCall.setVisibility(View.GONE);
            } else {
                imgCall.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(agentModel.getEmailid())) {
                imgEmail.setVisibility(View.GONE);
            } else {
                imgEmail.setVisibility(View.VISIBLE);
            }

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
                    //PrefUtils.setAgent(context, agentModel);
                    agentModel.setChecked(false);
                    onSelectionListener.onSelect();
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

            letterIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flip();
                }

                private void flip() {
                    // PrefUtils.setAgent(context, agentModel);
                    agentModel.setChecked(true);
                    onSelectionListener.onSelect();
                    parentView.setBackgroundColor(ContextCompat.getColor(context, R.color.off_white));
                    letterIcon.startAnimation(flipAnim);
                    flipAnim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            letterIcon.setVisibility(View.GONE);
                            imgCheck.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            });

            imgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Functions.makePhoneCall(context, agentModel.getMobileNo());
                }
            });

            imgEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Functions.sendMailTo(context, agentModel.getEmailid());
                }
            });
        }
    }

    public interface onSelectionListener {
        void onSelect();
    }

    public void searchFilter(String searchQuery) {
        final ArrayList<AgentModel> filterAgent = new ArrayList<>();
        for (AgentModel model : agentList) {
            final String text = model.getName().toLowerCase();
            if (text.contains(searchQuery.toLowerCase())) {
                filterAgent.add(model);
            }
        }
        setAgentList(filterAgent);

    }

    public ArrayList<AgentModel> getAgentList() {
        return agentList;
    }
}
