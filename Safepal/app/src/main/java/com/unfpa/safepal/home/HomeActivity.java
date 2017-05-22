package com.unfpa.safepal.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unfpa.safepal.ProvideHelp.CsoActivity;
import com.unfpa.safepal.R;

import com.unfpa.safepal.Utils.CenterLockListener;
import com.unfpa.safepal.Utils.General;
import com.unfpa.safepal.Utils.Layout;
import com.unfpa.safepal.messages.EMessageDialogFragment;
import com.unfpa.safepal.report.ReportingActivity;

import java.util.ArrayList;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

        //Global Variables 1234
    /**
     * Next and buttonExit button
     */
    FloatingActionButton fabReportCase;
    //Button buttonExit;
    //Button buttonNext;
    RelativeLayout infoPanel;
    //TextView textViewMessage;
    //AppCompatCheckBox checkBoxAutoScroll;
    //horizontal recycler view for displaying hints
    RecyclerView listView;
    //Array for holding the hints
    ArrayList<Hint> mData = new ArrayList<Hint>();
    NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        // Assignments of variables
//        buttonExit = (Button) findViewById(R.id.exit_app);
//        buttonNext = (Button) findViewById(R.id.next_message);
        fabReportCase = (FloatingActionButton) findViewById(R.id.fab);
        nestedScrollView = (NestedScrollView)findViewById(R.id.scrollview);
        //infoPanel = (RelativeLayout)findViewById(R.id.info_panel);
//        textViewMessage = (TextView) findViewById(R.id.message);
//        checkBoxAutoScroll = (AppCompatCheckBox)findViewById(R.id.auto_scroll_CheckBox) ;

        //fill Hints adapter
        //get Messages
        ArrayAdapter<CharSequence> messages = ArrayAdapter.createFromResource(this,
                R.array.home_contact_info, R.layout.spinner_item);
        //add messages to adapter
        for(int i=0; i< messages.getCount(); ++i){
            Hint hint = new Hint();
            hint.setHint(String.valueOf(messages.getItem(i)));
            //Log.d(TAG, "message: " + String.valueOf(messages.getItem(i)) );
            mData.add( hint );
        }
        listView = (RecyclerView) findViewById(R.id.list);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        listView.setLayoutManager(lm);

        //set recycler view adapter
        listView.setAdapter(new RAdapter(mData));

       // CardView cardView = (CardView)findViewById(R.id.hint_container);
        //int width = cardView.getWidth();
        final int itemWidth = (int)getResources().getDimension(R.dimen.hint_width);
        Log.d(TAG, "cardview width: " + itemWidth);


        final TextView mCenterIndicator = (TextView) findViewById(R.id.centerIndicator);
        mCenterIndicator.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int center;
                if(Layout.isLandscape(getBaseContext())){
                    center = (( mCenterIndicator.getLeft() + mCenterIndicator.getRight()  ) / 3) * 2;
                }else {//portraint. Normal centre
                    center = ( mCenterIndicator.getLeft() + mCenterIndicator.getRight()  ) / 2 ;
                }
                //int padding =  center - itemWidth / 2; //Assuming both left and right padding needed are the same
                //listView.setPadding(padding,0,padding,0);
                listView.setOnScrollListener(new CenterLockListener(center));


            }
        });

//        buttonExit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(Build.VERSION.SDK_INT>=21)  finishAndRemoveTask();
//                else finish();
//            }
//        });
//        buttonExit.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                Uri packageURI = Uri.parse("package:com.unfpa.safepal");
//                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
//                startActivity(uninstallIntent);
//
//                return true;
//            }
//        });

//        buttonNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                animateNextMessage();
//
//            }
//        });

        fabReportCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ReportingActivity.class));

            }
        });

        //for internal support
//        checkBoxAutoScroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(b){
//                    activateAutoScrollTimer();
//                    Log.d(TAG, "activated timer");
//                }else {
//                    Log.d(TAG, "deactivated timer");
//                    deactivateAutoScrollTimer();
//                }
//            }
//        });



        //animations about messages
        animSlideIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.enter_from_right);
        animExit = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.exit_to_left);
        animExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                updateMessageText();
                infoPanel.startAnimation(animSlideIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //animateNextMessage();//show first message
        showDisclaimer();

        //scroll to top after starting the activity
        try {
            nestedScrollView.smoothScrollTo(0,0);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Cannot execute: nestedScrollView.smoothScrollTo(0,0);. Is this landscape mode??");
        }
    }

    private void showDisclaimer() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean(getString(R.string.first_time), true);
        if( isFirstTime ){
            General.showDisclaimerDialog(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(getString(R.string.first_time), false);
            editor.apply();//indicate that app has ever been opened
        }else {
            //General.showDisclaimerDialog(this);
        }
    }

    Animation animSlideIn;
    Animation animExit;
//    void animateNextMessage(){
//        infoPanel.startAnimation(animExit);
//    }

//    boolean isAutoScrollOn= true;
//    Thread threadScrolling;
//    private void activateAutoScrollTimer() {
//        isAutoScrollOn = true;
//        threadScrolling = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (isAutoScrollOn){
//                    try {
//                        Log.d(TAG, "loop!");
//                        int timeOut = getResources().getInteger(R.integer.message_timeout);
//                        Log.d(TAG, "timeout: " + timeOut);
//                        Thread.sleep(timeOut);//wait a bit
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                animateNextMessage();//change message
//                            }
//                        });
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        Log.e(TAG, "error: " + e.toString() );
//                    }
//                }
//                Log.d(TAG, "done n thread");
//            }
//        });threadScrolling.start();
//
//    }
//    private void deactivateAutoScrollTimer() {
//        isAutoScrollOn = false;
//
//    }

    String TAG = HomeActivity.class.getSimpleName();
    private void updateMessageText() {//// TODO: 13-Nov-16 dynamically set messages
       //make adapter
        ArrayAdapter<CharSequence> messages = ArrayAdapter.createFromResource(this,
                R.array.home_contact_info, R.layout.spinner_item);//// TODO: 14-Nov-16 Is this the correct Array???
        Random random = new Random();
        int min = 0;
        int max = messages.getCount();
        Log.d(TAG, "max: " + max);
        int randomIndex = random.nextInt(max-min)+min;
        Log.d(TAG, "randomIndex: " + randomIndex);
        String msg = messages.getItem(randomIndex).toString();
        //Log.d(TAG, "textViewMessage: " + msg);
        //textViewMessage.setText(msg);

    }


    //expand encouraging messages
//    public void onClickInfoPopUp(View view){
//        EMessageDialogFragment emDialog = EMessageDialogFragment.newInstance(
//                "Safepal",
//                textViewMessage.getText().toString(),
//                getString(R.string.close_dialog));
//        emDialog.show(getFragmentManager(), "encouraging message");
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_disclaimer:
                General.showDisclaimerDialog(this);
                return true;
            case R.id.menu_report:
                startActivity(new Intent(getApplicationContext(), ReportingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    //inner class adapter for the hints
    public class RAdapter extends  RecyclerView.Adapter<RAdapter.ReviewHolder>  {
        ArrayList<Hint> mData;

        public RAdapter(ArrayList<Hint> data){
            mData=data;
        }

        @Override
        public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View v= LayoutInflater.from(context).inflate(R.layout.list_item_hint,parent,false);
            return new ReviewHolder(v);
        }

        @Override
        public void onBindViewHolder(ReviewHolder holder, int position) {
            holder.relativeLayout.setMinimumWidth(600);
            holder.hint.setText(mData.get(position).getHint());
            cardWidth = holder.cardView.getWidth();
            //Log.d(TAG, "Hints size:  " + mData.size() );
            //Log.d(TAG, "Hint in holder:  " + mData.get(position).getHint() );
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ReviewHolder extends RecyclerView.ViewHolder {

            protected TextView hint;
            protected CardView cardView;
            RelativeLayout relativeLayout;
            View container;

            public ReviewHolder(View itemView) {
                super(itemView);
                container=itemView;
                hint = (TextView) itemView.findViewById(R.id.textView_hint);
                cardView = (CardView) itemView.findViewById(R.id.hint_container);
                relativeLayout = (RelativeLayout) itemView.findViewById(R.id.hint_top);
            }
        }

    }
int cardWidth;
}
