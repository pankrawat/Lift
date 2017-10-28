package com.liftindia.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liftindia.app.R;
import com.liftindia.app.activity.BaseActivity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class FaqFragment extends Fragment implements View.OnClickListener {
    RelativeLayout drawerIcon;
    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> faqcollection;
    ExpandableListView expListView;
    Integer plus = R.mipmap.plus;
    Integer minus = R.mipmap.minus;

    TextView tv_q1, tv_q2, tv_q3, tv_q4, tv_q5, tv_q6, tv_q7, tv_q8;
    TextView tv_a1, tv_a2, tv_a3, tv_a4, tv_a5, tv_a6, tv_a7, tv_a8;
    boolean b1, b2, b3, b4, b5, b6, b7, b8;

    String ans1 = "We have designed the security keeping in mind the sensitivity of the situation.\n\n"
            + "We have incorporated a 5-layer security which is unbeatable. Some of the features are-\n\n"
            + Html.fromHtml("&#9679") + " Social verification like Facebook & LinkedIn, you can see the contacts of the person.\n"
            + Html.fromHtml("&#9679") + " Government ID proofs verification\n"
            + Html.fromHtml("&#9679") + " Lift details exchange to emergency contact before boarding the lift\n"
            + Html.fromHtml("&#9679") + " Real-time tracking of each ride.\n"
            + Html.fromHtml("&#9679") + " Help button that triggers a notification to police, emergency contact & us.\n\n"
            + "We track all the rides from pickup point to drop point and can trace any deviations from intended route and timing.\n"
            + "In addition, we also keep the records of every ride, the person travelled, the route and the timing for future references.\n"
            + "Moreover, you can call/sms them on the click of a button on our app or share the details of the lift with anyone.\n"
            + "For women travelers, we have filters to show only women offerers. You can choose according to your comfort.";

    String ans2 = "Lift Requester is a person who requests for a lift from someone going on his route or someone who wants to share his cab for the commercial or personal purpose.";

    String ans3 = "Lift offerer can be a person who:\n"
            + Html.fromHtml("&#9679") + " Is going to any place in his private car and willing to offer lift\n"
            + Html.fromHtml("&#9679") + " Owns a cab for commercial purpose and willing to offer lift";

    String ans4 = "The Estimated Time of Arrival (ETA) of the offerer is given while you choose the lift. There is also a time sorting option.\n" +
            "ETA is the calculated real time with the help of google map according to the current location of the offerer. However, you can directly message or call the offerer for the expected lift time.";

    String ans5 = "It is a measure of the distance that matches with the offerer’s route.\n" +
            "Example – The route you entered is 8 km. The common route is 7 km, therefore, route match % is (7/8*100) = 87.5%";

    String ans6 = "It depends on the lift offerer. How much deroute he allows while posting lift.\n\n" +
            "We will display the route match % on the lift details and when you select any lift we will display your route as well as the offerer’s route and your pick and drop points.\n" +
            "So you can make the decision wisely.";

    String ans7 = "No, It’s not necessary to enter end point for the offerers who are willing to go anywhere according to the requester. In that case, it would be matched according to its real-time location.";

    String ans8 = "There is a long cue of reasons for offering a lift. But to be precise, these are some points before you choose not to offer.\n" +
            "You will save one more car or cab on the road. Hence, you will restrict traffic.\n" +
            "You can save fuel that is going to be consumed by the requester in his car or cab. Hence, you save fuel and prevent a tree from being chopped down.\n" +
            "You can save pollution that one particular vehicle will produce.\n" +
            "You can save the fuel and maintenance expenses of your rides.\n" +
            "You can build more connections and friends with the requester and make your travel time a fun time.\n" +
            "It is entirely safe with 5-layer security feature which you cannot find anywhere else.";

    public FaqFragment() {
        // Required empty public constructor
    }

    public static FaqFragment newInstance() {
        FaqFragment fragment = new FaqFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faq, container, false);
        drawerIcon = (RelativeLayout) view.findViewById(R.id.drawerIcon);
        drawerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.mDrawer.toggleMenu();
            }
        });

        tv_q1 = (TextView) view.findViewById(R.id.tv_q1);
        tv_q2 = (TextView) view.findViewById(R.id.tv_q2);
        tv_q3 = (TextView) view.findViewById(R.id.tv_q3);
        tv_q4 = (TextView) view.findViewById(R.id.tv_q4);
        tv_q5 = (TextView) view.findViewById(R.id.tv_q5);
        tv_q6 = (TextView) view.findViewById(R.id.tv_q6);
        tv_q7 = (TextView) view.findViewById(R.id.tv_q7);
        tv_q8 = (TextView) view.findViewById(R.id.tv_q8);
        tv_a1 = (TextView) view.findViewById(R.id.tv_a1);
        tv_a2 = (TextView) view.findViewById(R.id.tv_a2);
        tv_a3 = (TextView) view.findViewById(R.id.tv_a3);
        tv_a4 = (TextView) view.findViewById(R.id.tv_a4);
        tv_a5 = (TextView) view.findViewById(R.id.tv_a5);
        tv_a6 = (TextView) view.findViewById(R.id.tv_a6);
        tv_a7 = (TextView) view.findViewById(R.id.tv_a7);
        tv_a8 = (TextView) view.findViewById(R.id.tv_a8);

        tv_a1.setText(ans1);
        tv_a2.setText(ans2);
        tv_a3.setText(ans3);
        tv_a4.setText(ans4);
        tv_a5.setText(ans5);
        tv_a6.setText(ans6);
        tv_a7.setText(ans7);
        tv_a8.setText(ans8);

        tv_q1.setOnClickListener(this);
        tv_q2.setOnClickListener(this);
        tv_q3.setOnClickListener(this);
        tv_q4.setOnClickListener(this);
        tv_q5.setOnClickListener(this);
        tv_q6.setOnClickListener(this);
        tv_q7.setOnClickListener(this);
        tv_q8.setOnClickListener(this);

        return view;
    }

    private void createGroupList() {
        groupList = new ArrayList<String>();
        groupList.add("1. Who is the Lift Offerer?");
        groupList.add("2. Who is the Lift Requester?");
        groupList.add("3. What about safety and security?");
    }

    private void createCollection() {
        // preparing laptops collection(child)
        String[] lift_Offerer = {"Lift offerer can be a person who :\n\n" + Html.fromHtml("&#9679") + " Is going to any place in his private car and willing to offer lift\n\n" + Html.fromHtml("&#9679") + " Owns a cab for commercial purpose and willing to offer lift."};
        String[] lift_Requester = {"Lift Requester is a person who request for a lift from someone going on his route or someone who wants to share his cab for the commercial or personal purpose."};
        String[] security = {"Lift offerer can be a person who:\n\n" + Html.fromHtml("&#9679") + " is going to any place in his private car and willing to offer lift.\n\n" + Html.fromHtml("&#9679") + " Owns a cab for commercial purpose and willing to offer lift."};

        faqcollection = new LinkedHashMap<String, List<String>>();

        for (String list_faq : groupList) {
            if (list_faq.equals("1. Who is the Lift Offerer?")) {
                loadChild(lift_Offerer);
            } else if (list_faq.equals("2. Who is the Lift Requester?"))
                loadChild(lift_Requester);
            else if (list_faq.equals("3. What about safety and security?"))
                loadChild(security);
            else
                loadChild(security);

            faqcollection.put(list_faq, childList);
        }
    }

    private void loadChild(String[] list_model) {
        childList = new ArrayList<String>();
        for (String model : list_model)
            childList.add(model);
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_q1:
                if(b1){
                    tv_q1.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(plus), null);
                    tv_a1.setVisibility(View.GONE);
                    b1 = !b1;
                } else {
                    tv_q1.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(minus), null);
                    tv_a1.setVisibility(View.VISIBLE);
                    b1 = !b1;
                }
                break;
            case R.id.tv_q2:
                if(b2){
                    tv_q2.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(plus), null);
                    tv_a2.setVisibility(View.GONE);
                    b2 = !b2;
                } else {
                    tv_q2.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(minus), null);
                    tv_a2.setVisibility(View.VISIBLE);
                    b2 = !b2;
                }
                break;
            case R.id.tv_q3:
                if(b3){
                    tv_q3.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(plus), null);
                    tv_a3.setVisibility(View.GONE);
                    b3 = !b3;
                } else {
                    tv_q3.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(minus), null);
                    tv_a3.setVisibility(View.VISIBLE);
                    b3 = !b3;
                }
                break;
            case R.id.tv_q4:
                if(b4){
                    tv_q4.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(plus), null);
                    tv_a4.setVisibility(View.GONE);
                    b4 = !b4;
                } else {
                    tv_q4.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(minus), null);
                    tv_a4.setVisibility(View.VISIBLE);
                    b4 = !b4;
                }
                break;
            case R.id.tv_q5:
                if(b5){
                    tv_q5.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(plus), null);
                    tv_a5.setVisibility(View.GONE);
                    b5 = !b5;
                } else {
                    tv_q5.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(minus), null);
                    tv_a5.setVisibility(View.VISIBLE);
                    b5 = !b5;
                }
                break;
            case R.id.tv_q6:
                if(b6){
                    tv_q6.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(plus), null);
                    tv_a6.setVisibility(View.GONE);
                    b6 = !b6;
                } else {
                    tv_q6.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(minus), null);
                    tv_a6.setVisibility(View.VISIBLE);
                    b6 = !b6;
                }
                break;
            case R.id.tv_q7:
                if(b7){
                    tv_q7.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(plus), null);
                    tv_a7.setVisibility(View.GONE);
                    b7 = !b7;
                } else {
                    tv_q7.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(minus), null);
                    tv_a7.setVisibility(View.VISIBLE);
                    b7 = !b7;
                }
                break;
            case R.id.tv_q8:
                if(b8){
                    tv_q8.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(plus), null);
                    tv_a8.setVisibility(View.GONE);
                    b8 = !b8;
                } else {
                    tv_q8.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(minus), null);
                    tv_a8.setVisibility(View.VISIBLE);
                    b8 = !b8;
                }
                break;

        }
    }
}
