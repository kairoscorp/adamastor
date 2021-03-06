package corp.kairos.adamastor;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import corp.kairos.adamastor.AllApps.AllAppsActivity;
import corp.kairos.adamastor.ContextList.ContextListActivity;
import corp.kairos.adamastor.Home.HomeActivity;
import corp.kairos.adamastor.Statistics.StatisticsManager.RealStatisticsDAO;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OptionsMenu.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OptionsMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OptionsMenu extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private AppDetails appDetail;
    private View frameLayout;

    public OptionsMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OptionsMenu.
     */
    public static OptionsMenu newInstance(String param1, String param2) {
        OptionsMenu fragment = new OptionsMenu();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(getActivity().getClass().getSimpleName().equals("ContextListActivity")){
            ((ContextListActivity) getActivity()).contextFragment();

        }
        if(getActivity().getClass().getSimpleName().equals("AllAppsActivity")){
            ((AllAppsActivity) getActivity()).contextFragment();

        }
        this.appDetail = (AppDetails) this.getArguments().getSerializable("app");
        Animation fromdown = AnimationUtils.loadAnimation(this.getActivity(),R.anim.slide_from_up);
        View v = inflater.inflate(R.layout.fragment_options_menu, container, false);
        v.startAnimation(fromdown);
        fromdown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ImageView iv = getActivity().findViewById(R.id.image_options);
                int colorFrom = getResources().getColor(android.R.color.transparent);
                int colorTo = getResources().getColor(R.color.black_overlay);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.setDuration(900); // milliseconds
                colorAnimation.addUpdateListener(animator -> iv.setBackgroundColor((int) animator.getAnimatedValue()));
                colorAnimation.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        return v;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public void nothingCLickListener(View v) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        TextView tv = getActivity().findViewById(R.id.app_name);
        ImageView iv = getActivity().findViewById(R.id.image_options);
        ImageView icon = getActivity().findViewById(R.id.image_icon);
        FrameLayout fl = getActivity().findViewById(R.id.options_menu);
        Button uninstall = getActivity().findViewById(R.id.uninstall_button);
        Button appInfo = getActivity().findViewById(R.id.app_info_button);
        Button context = getActivity().findViewById(R.id.set_context_button);
        TextView lastUsed = getActivity().findViewById(R.id.lastUsed);
        TextView timeUsed = getActivity().findViewById(R.id.timeUsed);
        RelativeLayout rl = getActivity().findViewById(R.id.background_layout);
        rl.setOnClickListener(view -> nothingCLickListener(view));
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.WHITE);
        gd.setCornerRadius(10);
        rl.setBackground(gd);
        RealStatisticsDAO realStatisticsDAO = new RealStatisticsDAO();
        lastUsed.setText("Last Used: "+realStatisticsDAO.lastTimeApp(appDetail));
        timeUsed.setText("Time Used: "+Integer.toString(realStatisticsDAO.getAppTime(appDetail)) +" Seconds");
        getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.black_overlay));
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black_overlay));
        icon.setImageDrawable(appDetail.getIcon());
        if (appDetail.isSystem()) {
            uninstall.setVisibility(View.GONE);
        }
        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        iv.setOnClickListener(v -> {
            HomeActivity.hideSetContext(getActivity());
            if(getActivity().getClass().getSimpleName().equals("ContextListActivity")){
                ((ContextListActivity) getActivity()).back--;

            }
            getActivity().getWindow().setStatusBarColor(0);
            getActivity().getWindow().setNavigationBarColor(0);
        });
        uninstall.setOnClickListener(v -> {
            View frame = getActivity().findViewById(R.id.view_option);
            ((ViewGroup) frame.getParent()).removeView(frame);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE);
            uninstallIntent.setData(Uri.parse("package:" + appDetail.getPackageName()));
            startActivity(uninstallIntent);
        });
        appInfo.setOnClickListener(v -> {
            View frame = getActivity().findViewById(R.id.view_option);
            ((ViewGroup) frame.getParent()).removeView(frame);
            Intent appInfoIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            appInfoIntent.setData(Uri.parse("package:" + appDetail.getPackageName()));
            startActivity(appInfoIntent);
        });
        tv.setText(appDetail.getLabel());
        context.setOnClickListener(v -> {
            View frame = getActivity().findViewById(R.id.view_option);
            ((ViewGroup) frame.getParent()).removeView(frame);
            FrameLayout contextSelect = new FrameLayout(getActivity().getApplicationContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            contextSelect.setOnClickListener(v1 -> {
                getActivity().onBackPressed();
            });
            params.gravity = Gravity.CENTER;
            contextSelect.setLayoutParams(params);
            ViewGroup parentView = getActivity().findViewById(android.R.id.content);

            parentView.addView(contextSelect);
            contextSelect.setId(R.id.select_context);
            contextSelect.setBackgroundColor(getResources().getColor(R.color.black_overlay));
            showSetContext(appDetail, R.id.select_context);
        });

        super.onActivityCreated(savedInstanceState);
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

    public void showSetContext(AppDetails appDetail, int viewId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("app", appDetail);
        Fragment options = new SetContext();
        options.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(viewId, options, "CONTEXT");
        transaction.addToBackStack("CONTEXT");
        transaction.commit();

    }


}
