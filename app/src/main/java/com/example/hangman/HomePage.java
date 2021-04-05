package com.example.hangman;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hangman.databinding.FragmentHomePageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static com.example.hangman.Game.mediaPlayer;

import static com.example.hangman.MainActivity.newToken;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePage extends Fragment implements View.OnClickListener {
    private Button btnCity;
    private Button btnAnimals;
    private Button btnVarious;
    private Button btnFamousPeople;
    private Button btnFilms;
    private Button btnSport;
    private Button btnGeographicalNames;
    private Button songs;
    private static boolean isPlaying = true;
    public static Button[] buttons;
    public String newToken;
    public static boolean isExists;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private FragmentHomePageBinding binding;
    private TextView[] textViews;
    public static ImageView soundOn;
    public static ImageView soundOff;
    private Dialog dialog;
    private ImageView startBtn;
    private EditText usernameWrite;
    private static int userId;
    private TextView num_win_Us;
    public static String tempUser;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomePage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomePage.
     */
    // TODO: Rename and change types and number of parameters
    public static HomePage newInstance(String param1, String param2) {
        HomePage fragment = new HomePage();
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


        binding = FragmentHomePageBinding.inflate(inflater, container, false);

        mDBHelper = new DatabaseHelper(getActivity());

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }


        soundOn = (ImageView) binding.soundOn;
        soundOff = binding.soundOff;

        if (!isPlaying) {
            soundOn.setVisibility(View.GONE);
            soundOff.setVisibility(View.VISIBLE);
        } else {
            soundOn.setVisibility(View.VISIBLE);
            soundOff.setVisibility(View.GONE);
        }

        soundOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundOn.getVisibility() == View.VISIBLE) {
                    soundOn.setVisibility(View.GONE);
                    soundOff.setVisibility(View.VISIBLE);
                    isPlaying = false;
                } else {
                    soundOn.setVisibility(View.VISIBLE);
                    soundOff.setVisibility(View.GONE);
                    isPlaying = true;
                }
            }
        });


        soundOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (soundOn.getVisibility() == View.VISIBLE) {
                    soundOn.setVisibility(View.GONE);
                    soundOff.setVisibility(View.VISIBLE);
                    isPlaying = false;
                } else {
                    soundOn.setVisibility(View.VISIBLE);
                    soundOff.setVisibility(View.GONE);
                    isPlaying = true;
                }
            }
        });


        binding.city.setOnClickListener(this);
        binding.literature.setOnClickListener(this);
        binding.various.setOnClickListener(this);
        binding.films.setOnClickListener(this);
        binding.sport.setOnClickListener(this);
        binding.geographicalNames.setOnClickListener(this);
        binding.songs.setOnClickListener(this);
        binding.childrens.setOnClickListener(this);


        buttons = new Button[]{binding.city, binding.literature, binding.various, binding.films, binding.sport, binding.geographicalNames, binding.songs, binding.childrens};
        textViews = new TextView[]{binding.sc1, binding.sc2, binding.sc3, binding.sc4, binding.sc5, binding.sc6, binding.sc7, binding.sc8};
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);


        isExists = false;

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            newToken = task.getResult().getToken();
                            Cursor c = mDb.rawQuery("select * from user", null);
                            c.moveToFirst();
                            while (!c.isAfterLast()) {
                                if (c.getString(2).equals(newToken)) {
                                    isExists = true;
                                }
                                c.moveToNext();
                            }


                            c.close();
                            int i = 0;
                            for (Button button : buttons) {
                                Cursor c3 = mDb.rawQuery("select words from words inner join categories on categories.id = words.category_id where categories.name=" + "'" + button.getText() + "' ", null);
                                Cursor c1 = mDb.rawQuery("SELECT repeatWord.repeat_word FROM repeatWord " +
                                        "INNER JOIN categories on categories.id=repeatWord.category_id " +
                                        "inner join user on repeatWord.user_id = user.Id where user.token=" + "'" + newToken + "' AND categories.name=" + "'" + button.getText() + "'", null);
                                if (c3.getCount() == c1.getCount()) {
                                    button.setEnabled(false);
                                    button.setAlpha(0.70f);
                                }

                                Cursor c4 = mDb.rawQuery("SELECT count(words) from words INNER join categories on words.category_id=categories.id where categories.name=" + "'" + button.getText() + "'", null);
                                Cursor c5 = mDb.rawQuery("SELECT count(repeat_word) FROM repeatWord INNER JOIN categories on categories.id=repeatWord.category_id inner join user on user.Id=repeatWord.user_id " +
                                        "where categories.name=" + "'" + button.getText() + "' AND user.token=" + "'" + newToken + "'", null);
                                c4.moveToFirst();
                                c5.moveToFirst();
                                while (!c4.isAfterLast()) {
                                    int result = Integer.parseInt(c4.getString(0)) - Integer.parseInt(c5.getString(0));
                                    textViews[i].setText(result + "/" + c4.getString(0));
                                    result = 0;
                                    i++;
                                    c4.moveToNext();
                                    c5.moveToNext();
                                }
                                c4.close();
                                c5.close();
                            }
                        }
                    }
                });


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            newToken = task.getResult().getToken();
                            ArrayList<Integer> list = new ArrayList<Integer>();
                            for (int i = 1; i < 11; i++) {
                                list.add(new Integer(i));
                            }
                            Collections.shuffle(list);
                            for (int i = 0; i < 3; i++) {
                                userId = list.get(i);
                            }
                            if (!isExists) {
                                insertUsers();
                                mDBHelper.insertUser(0, newToken, "Խաղացող_" + userId);
                            }

                            binding.usernameBtn.setOnClickListener(v -> {
                                dialog = new Dialog(getActivity());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.username_layout);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                ImageView dialogButton = (ImageView) dialog.findViewById(R.id.dialog_exit_btn);

                                ConstraintLayout constraintLayout = dialog.findViewById(R.id.username_lay);
                                View layout3 = LayoutInflater.from(getActivity()).inflate(R.layout.username_layout, constraintLayout, false);
                                ImageView startBtn = (ImageView) layout3.findViewById(R.id.start_game_btn);
                                usernameWrite = (EditText) layout3.findViewById(R.id.username_write);
                                startBtn.setEnabled(false);
                                startBtn.setAlpha(0.70f);
                                usernameWrite.addTextChangedListener(new TextWatcher() {

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                        if (s.toString().trim().length() == 0) {
                                            startBtn.setClickable(false);
                                            startBtn.setEnabled(false);
                                            startBtn.setAlpha(0.70f);
                                        } else {
                                            startBtn.setClickable(true);
                                            startBtn.setEnabled(true);
                                            startBtn.setAlpha(1f);
                                        }
                                    }

                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                                  int after) {

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });


                                startBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        System.out.println(String.valueOf(usernameWrite.getText()) + " //////////////////////////////////");
                                        Cursor c = mDb.rawQuery("select user.username from user", null);
                                        c.moveToFirst();
                                        boolean f = false;
                                        while (!c.isAfterLast()) {
                                            if (c.getString(0).equals(String.valueOf(usernameWrite.getText()))) {
                                                f = true;
                                                break;
                                            }
                                            c.moveToNext();
                                        }
                                        if (!f) {
                                            ContentValues contentValues1 = new ContentValues();
                                            contentValues1.put("username", String.valueOf(usernameWrite.getText()));
                                            mDb.update("user", contentValues1, "token = ?", new String[]{newToken});
                                            dialog.dismiss();
                                        } else {
                                            Toast toast = Toast.makeText(getActivity(), "Նման խաղանուն արդեն գոյություն ունի", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 100);
                                            toast.show();
                                            usernameWrite.getText().clear();
                                        }


                                        Cursor cursor1 = mDb.rawQuery("select user.username from user where user.token= " + "'" + newToken + "'", null);
                                        cursor1.moveToFirst();
                                        while (!cursor1.isAfterLast()) {
                                            String Sc = cursor1.getString(0);
                                            cursor1.moveToNext();
                                            cursor1.close();
                                            binding.usernameBtn.setText(Sc);
                                        }

                                    }
                                });


                                constraintLayout.addView(layout3);

                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();

                                    }
                                });


                                dialog.show();

                            });


                            Cursor cursor1 = mDb.rawQuery("select user.username from user where user.token= " + "'" + newToken + "'", null);
                            cursor1.moveToFirst();
                            while (!cursor1.isAfterLast()) {
                                String Sc = cursor1.getString(0);
                                cursor1.moveToNext();
                                cursor1.close();
                                binding.usernameBtn.setText(Sc);

                            }


                            binding.winners.setOnClickListener(v -> {
                                dialog = new Dialog(getActivity());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.winner_layout);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                ImageView dialogButton = (ImageView) dialog.findViewById(R.id.dialog_exit_btn);


                                ConstraintLayout constraintLayout = dialog.findViewById(R.id.winner_lay);
                                View layout3 = LayoutInflater.from(getActivity()).inflate(R.layout.winner_layout, constraintLayout, false);

                                List<String> names = new ArrayList<>();
                                List<String> scores = new ArrayList<>();
                                List<String> digits = new ArrayList<>();

                                tempUser = "";
                                String temp_score = "";
                                Cursor cursorTemp = mDb.rawQuery("select user.username,user.score from user where user.token= " + "'" + newToken + "'", null);
                                cursorTemp.moveToFirst();
                                while (!cursorTemp.isAfterLast()) {
                                    tempUser = cursorTemp.getString(0);
                                    temp_score = cursorTemp.getString(1);
                                    cursorTemp.moveToNext();
                                }

                                List<String> topThreeUsers = new ArrayList<>();
                                Cursor cursorRepUs = mDb.rawQuery("select user.username from user order by user.score DESC limit 3", null);
                                cursorRepUs.moveToFirst();
                                while (!cursorRepUs.isAfterLast()) {
                                    topThreeUsers.add(cursorRepUs.getString(0));
                                    cursorRepUs.moveToNext();
                                }
                                cursorRepUs.close();

                                Cursor cursorW = mDb.rawQuery("select user.username,user.score from user order by user.score DESC", null);
                                cursorW.moveToFirst();
                                while (!cursorW.isAfterLast()) {
                                    names.add(cursorW.getString(0));
                                    scores.add(cursorW.getString(1));
                                    cursorW.moveToNext();
                                }
                                cursorW.close();

                                for (int i = 1; i <= names.size(); i++) {
                                    digits.add(String.valueOf(i));
                                }

                                MyListData listData = new MyListData(names, digits, scores);

                                List<MyListData> myListData = new ArrayList<MyListData>();
                                for (int i = 0; i < names.size(); i++) {
                                    myListData.add(listData);
                                }

                                TextView textView = layout3.findViewById(R.id.temp_user_me);
                                TextView temp_User_Score = layout3.findViewById(R.id.temp_user_score);
                                textView.setText(tempUser);
                                temp_User_Score.setText(temp_score);


//                                for (int i = 0; i < topThreeUsers.size(); i++) {
//                                    if (tempUser.equals(topThreeUsers.get(0)) || tempUser.equals(topThreeUsers.get(01))
//                                            || tempUser.equals(topThreeUsers.get(2))) {
//                                        TextView textView = layout3.findViewById(R.id.temp_user_me);
//                                        TextView temp_User_Score = layout3.findViewById(R.id.temp_user_score);
//                                        ImageView polygon_1 = layout3.findViewById(R.id.polygon_1);
//                                        ImageView polygon_2 = layout3.findViewById(R.id.polygon_2);
//                                        ImageView temp_gum = layout3.findViewById(R.id.temp_gum);
//                                        polygon_1.setVisibility(View.GONE);
//                                        polygon_2.setVisibility(View.GONE);
//                                        textView.setVisibility(View.GONE);
//                                        temp_gum.setVisibility(View.GONE);
//                                        temp_User_Score.setVisibility(View.GONE);
//
//
//
//                                    } else {
//                                        TextView textView = layout3.findViewById(R.id.temp_user_me);
//                                        TextView temp_User_Score = layout3.findViewById(R.id.temp_user_score);
//                                        ImageView polygon_1 = layout3.findViewById(R.id.polygon_1);
//                                        ImageView polygon_2 = layout3.findViewById(R.id.polygon_2);
//                                        ImageView temp_gum = layout3.findViewById(R.id.temp_gum);
//                                        polygon_1.setVisibility(View.VISIBLE);
//                                        polygon_2.setVisibility(View.VISIBLE);
//                                        textView.setVisibility(View.VISIBLE);
//                                        temp_gum.setVisibility(View.VISIBLE);
//                                        textView.setText(tempUser);
//                                        temp_User_Score.setText(temp_score);
//
//                                    }
//                                }
                                cursorTemp.close();

                                RecyclerView recyclerView = (RecyclerView) layout3.findViewById(R.id.recyclerView);
                                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
                                MyListAdapter adapter = new MyListAdapter(myListData);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerView.setAdapter(adapter);


                                constraintLayout.addView(layout3);


                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();

                            });


                        }
                    }
                });


        Cursor cursor = mDb.rawQuery("select * from categories", null);
        cursor.moveToFirst();
        int i = 0;
        while (!cursor.isAfterLast()) {
            buttons[i].setText(cursor.getString(1));
            i++;
            cursor.moveToNext();
        }
        cursor.close();


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            newToken = task.getResult().getToken();
                            Cursor cursor1 = mDb.rawQuery("select user.score from user where user.token= " + "'" + newToken + "'", null);
                            cursor1.moveToFirst();
                            String Sc = cursor1.getString(0);
                            cursor1.moveToNext();
                            cursor1.close();
                            binding.scoreHome.setText(Sc);
                        }
                    }
                });

        return binding.getRoot();

    }

    public void insertUsers() {
        mDBHelper.insertUser(565, "fghxfhxfhfxhghgthe558oa4554t", "Խաղացող_01");
        mDBHelper.insertUser(550, "dhdddh5465dhdfh5dfh5dh5dh1d5fh4d5", "Խաղացող_02");
        mDBHelper.insertUser(532, "fdhdh455614552dghhxfhfxhgdhdh4t", "Խաղացող_03");
        mDBHelper.insertUser(514, "fdhdh455614552dghhxfhfxhgdhdh4t", "Խաղացող_04");
        mDBHelper.insertUser(499, "fdthtydujytdj54sedg5re4re54ydhdh4t", "Խաղացող_05");
    }

    @Override
    public void onClick(View v) {

        Button button = (Button) v;
        button.setBackgroundResource(R.drawable.click_btn);
        Game game = new Game();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString("category", button.getText().toString());
        game.setArguments(bundle);


        fragmentTransaction.replace(R.id.mainId, game);
        fragmentTransaction.commit();

    }
}