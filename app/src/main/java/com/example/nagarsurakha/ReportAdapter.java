package com.example.nagarsurakha;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private final List<ReportModel> reportList;

    // ✅ Constructor
    public ReportAdapter(List<ReportModel> reportList) {
        this.reportList = reportList; // keep original reference
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReportModel report = reportList.get(position);

        // ✅ Use exact field names from ReportModel
        holder.tvId.setText("Report_id: " + report.getReport_id());
        holder.tvTime.setText("Time: " + report.getReport_time());
        holder.tvStatus.setText("Status: " + report.getStatus());
        holder.tvDept.setText("Department: " + report.getDepartment());
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    // ✅ ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvTime, tvStatus, tvDept;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.report_id);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDept = itemView.findViewById(R.id.tvDept);
        }
    }
}
