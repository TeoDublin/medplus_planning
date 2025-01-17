package medplus.planning.app.ui.planning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

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
                        populateTable(response);
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

    private void populateTable(JSONObject data) {
        TableLayout tableLayout = binding.tableLayoutPlanning;

        try {
            Iterator<String> keys = data.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject item = data.getJSONObject(key);

                int id = item.getInt("id");
                String start = item.getString("start");
                String end = item.getString("end");
                String origin = item.getString("origin");
                String reason = item.getString("reason");

                TableRow row = new TableRow(requireContext());
                row.addView(createTextView(String.valueOf(id)));
                row.addView(createTextView(start));
                row.addView(createTextView(end));
                row.addView(createTextView(origin));
                row.addView(createTextView(reason));

                tableLayout.addView(row);
            }
        } catch (JSONException e) {
            Toast.makeText(requireContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
        }
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
