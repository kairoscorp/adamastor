package corp.kairos.adamastor;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import corp.kairos.adamastor.ContextList.ContextListActivity;
import corp.kairos.adamastor.Settings.Settings;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SetContext.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SetContext#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetContext extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private AppDetails appDetail;

    public SetContext() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetContext.
     */
    public static SetContext newInstance(String param1, String param2) {
        SetContext fragment = new SetContext();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.appDetail = (AppDetails) this.getArguments().getSerializable("app");
        View v = inflater.inflate(R.layout.fragment_set_context, container, false);
        Log.i("cas", getActivity().getClass().getSimpleName());

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().findViewById(R.id.frame_context).setOnClickListener(view -> {
            nothingCLickListener(view);
        });
        getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.black_overlay));
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black_overlay));
        CheckBox cWork = getActivity().findViewById(R.id.checkBox_Work);
        CheckBox cLeisure = getActivity().findViewById(R.id.checkBox_Leisure);
        CheckBox cCommute = getActivity().findViewById(R.id.checkBox_Commute);
        Button cancel = getActivity().findViewById(R.id.cancel);
        Settings s = Settings.getInstance(this.getActivity());
        UserContext work = s.getUserContext(getActivity().getResources().getString(R.string.work_name));
        UserContext leisure = s.getUserContext(getActivity().getResources().getString(R.string.leisure_name));
        UserContext commute = s.getUserContext(getActivity().getResources().getString(R.string.commute_name));
        Boolean workIs = work.appExists(appDetail);
        Boolean leisureIs = leisure.appExists(appDetail);
        Boolean commuteIs = commute.appExists(appDetail);
        cWork.setChecked(workIs);
        cLeisure.setChecked(leisureIs);
        cCommute.setChecked(commuteIs);
        View frame = getActivity().findViewById(R.id.select_context);
        View fragment = getActivity().findViewById(R.id.frame_context);
        fragment.setOnKeyListener(null);
        cancel.setOnClickListener(v1 -> {
            getActivity().getWindow().setStatusBarColor(0);
            getActivity().getWindow().setNavigationBarColor(0);
            getActivity().onBackPressed();
            getFragmentManager().popBackStack();

        });
        Button ok = getActivity().findViewById(R.id.ok);
        ok.setOnClickListener(v2 -> {
                    if (cWork.isChecked()) {
                        work.addApp(appDetail);
                    } else {
                        work.removeApp(appDetail);
                    }
                    if (cLeisure.isChecked()) {
                        leisure.addApp(appDetail);
                    } else {
                        leisure.removeApp(appDetail);
                    }
                    if (cCommute.isChecked()) {
                        commute.addApp(appDetail);
                    } else {
                        commute.removeApp(appDetail);
                    }
                    s.setUserContext(work);
                    s.setUserContext(commute);
                    s.setUserContext(leisure);
                    s.saveContextSettings();
                    if (getActivity().getClass().getSimpleName().equals("ContextListActivity")) {
                        Log.i("SSS", "nope");
                        ((ContextListActivity) getActivity()).notifyDataSet();
                    } else {
                        Log.i("SSS", "YES");

                    }
                    getActivity().getWindow().setStatusBarColor(0);
                    getActivity().getWindow().setNavigationBarColor(0);
                    getFragmentManager().popBackStack();
                    getActivity().onBackPressed();
                }
        );
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        getActivity().getWindow().setStatusBarColor(0);
        getActivity().getWindow().setNavigationBarColor(0);
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void nothingCLickListener(View v) {

    }

}
