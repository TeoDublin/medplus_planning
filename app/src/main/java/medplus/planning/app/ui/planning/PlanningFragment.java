package medplus.planning.app.ui.planning;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import medplus.planning.app.R;
import medplus.planning.app.databinding.FragmentPlanningBinding;

public class PlanningFragment extends Fragment {

    private FragmentPlanningBinding binding;
    private static final String BACKEND_URL = "https://teo.place/medplus_planning_backend/end_points/planning.php";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlanningBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Fetch and populate table
        fetchDataAndPopulateTable();

        return root;
    }

    private void fetchDataAndPopulateTable() {
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                BACKEND_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        populate(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void populate(JSONObject data) {
        LinearLayout container = binding.linearLayout;
        try {
            JSONArray planningRow = data.getJSONArray("planning_row");


            for (int i = 0; i < planningRow.length(); i++) {
                JSONObject item = planningRow.getJSONObject(i);
                LinearLayout rowLayout = new LinearLayout(requireContext());
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                TextView planTextView = createTextView(item.getString("plan"));

                String id = item.getString("id");
                String[] parts = id.split("_");
                Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.planning_item);
                switch (parts[0]){
                    case "sbarra":{
                        background = ContextCompat.getDrawable(requireContext(), R.drawable.planning_item_sbarra);
                        break;
                    }
                    case "seduta":{
                        background = ContextCompat.getDrawable(requireContext(), R.drawable.planning_item_seduta);
                        break;
                    }
                    case "-":{
                        background = ContextCompat.getDrawable(requireContext(), R.drawable.planning_item_libero);

                        break;
                    }
                }

                planTextView.setBackground(background);

                rowLayout.addView(planTextView);
                container.addView(rowLayout);
            }
        } catch (JSONException e) {
            Toast.makeText(requireContext(), "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private int dp(int px) {
        return (int) (px * getResources().getDisplayMetrics().density);
    }

    private TextView createTextView(String text) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(80)
        );
        layoutParams.setMargins(dp(3), dp(3), dp(3), dp(3));

        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(dp(1), dp(8), dp(1), dp(8));
        textView.setTextSize(18);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextColor(Color.BLACK);
        return textView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
