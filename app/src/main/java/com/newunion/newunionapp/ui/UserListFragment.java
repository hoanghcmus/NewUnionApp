package com.newunion.newunionapp.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.newunion.newunionapp.R;
import com.newunion.newunionapp.pojo.User;
import com.newunion.newunionapp.rest.RestClient;
import com.newunion.newunionapp.rest.api.UsersService;
import com.newunion.newunionapp.rx.SimpleSubscriber;
import com.newunion.newunionapp.ui.widget.ItemSpaceDecoration;
import com.newunion.newunionapp.utils.CONSTANTS;
import com.newunion.newunionapp.utils.CollectionUtils;
import com.newunion.newunionapp.utils.LoginPreferences;
import com.newunion.newunionapp.utils.NetworkUtils;
import com.newunion.newunionapp.utils.NewUnionLog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tylerjroach.com.eventsource_android.EventSource;
import tylerjroach.com.eventsource_android.EventSourceHandler;
import tylerjroach.com.eventsource_android.MessageEvent;

/**
 * <p> List User Fragment
 * </p>
 * Created by Nguyen Ngoc Hoang on 7/20/2016.
 *
 * @author Nguyen Ngoc Hoang (www.hoangvnit.com)
 */
public class UserListFragment extends Fragment implements EventSourceHandler {

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.user_list_recycler_view)
    RecyclerView mUserListRecyclerView;

    @BindView(R.id.empty_view)
    TextView mEmptyView;

    private UsersService mUsersService;

    private Subscription mSubscription;

    private UserListAdapter mListAdapter;

    private EventSource mEventSource;

    private Map<String, String> mRequestHeaders = new HashMap();

    private JsonNode data;

    private final ObjectMapper jsonObjectMapper = new ObjectMapper();


    public static UserListFragment newInstance(Bundle args) {
        UserListFragment fragment = new UserListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mUsersService = RestClient.getInstance().getUserService();

        mRequestHeaders.put(CONSTANTS.STREAM_DATA_TOKEN_HEADER_NAME, CONSTANTS.STREAM_DATA_TOKEN_HEADER_VALUE);
        mRequestHeaders.put(CONSTANTS.REQUEST_HEADER_AUTHORIZATION, "m " + LoginPreferences.getToken(getActivity()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(this, root);
        setupSwipeRefreshLayout();
        setupRecyclerView();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (NetworkUtils.isNetworkConnected(getActivity())) {
            showProgressDialog();
            fetchUserList();
        } else {
            mEmptyView.setText(getString(R.string.error_no_network_connection));
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    private void fetchUserList() {
        unsubscribe();
        mSubscription = mUsersService.getUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleSubscriber<List<User>>() {
                    @Override
                    public void onNext(List<User> users) {
                        hideProgressDialog();

                        mSwipeRefreshLayout.setRefreshing(false);
                        if (!CollectionUtils.isEmpty(users)) {
                            Collections.sort(users);
                            mListAdapter.setData(users);
                            mUserListRecyclerView.smoothScrollToPosition(0);
                        }

                        if (mListAdapter.getItemCount() == 0) {
                            mEmptyView.setText(getString(R.string.message_data_empty));
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        mSwipeRefreshLayout.setRefreshing(false);

                        if (mListAdapter.getItemCount() == 0) {
                            mEmptyView.setText(getString(R.string.error_fetching_data_from_server));
                            mEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyView.setVisibility(View.GONE);
                        }

                        NewUnionLog.e(e.toString());
                    }
                });
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                fetchUserList();
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    private void setupRecyclerView() {
        int space = (int) (4 * getResources().getDisplayMetrics().density);
        ItemSpaceDecoration spaceDecoration = new ItemSpaceDecoration(space);
        mListAdapter = new UserListAdapter(getActivity(), new ArrayList<User>());
        mUserListRecyclerView.setAdapter(mListAdapter);
        mUserListRecyclerView.addItemDecoration(spaceDecoration);
        mUserListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        unsubscribe();
        hideProgressDialog();
        disconnect();
    }

    private void unsubscribe() {
        if (mSubscription != null) {
            if (!mSubscription.isUnsubscribed()) {
                mSubscription.unsubscribe();
            }
            mSubscription = null;
        }
    }

    private void showProgressDialog() {
        ((MainActivity) getActivity()).showProgressDialog();
    }

    private void hideProgressDialog() {
        ((MainActivity) getActivity()).hideProgressDialog();
    }

    public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

        private Context mContext;

        private List<User> mUsers;

        private LayoutInflater mLayoutInflater;

        public UserListAdapter(Context context, List<User> list) {
            mContext = context;
            mUsers = list != null ? list : new ArrayList<User>();
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
            UserViewHolder vh = new UserViewHolder(itemView);
            return vh;
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            User user = mUsers.get(position);
            Picasso.with(mContext).load(user.getAvatar()).into(holder.mAvatarImageView);
            holder.mNameTextView.setText(user.getName());
            holder.mEmailTextView.setText(user.getEmail());
        }

        @Override
        public int getItemCount() {
            return mUsers == null ? 0 : mUsers.size();
        }

        public void addAll(List<User> list) {
            if (CollectionUtils.isEmpty(list)) {
                return;
            }

            int oldSize = mUsers.size();
            mUsers.addAll(list);
            notifyItemRangeInserted(oldSize, list.size());
        }

        public void add(User user) {
            if (user == null) {
                return;
            }
            mUsers.add(user);
            notifyItemInserted(mUsers.size() - 1);
        }

        public void setData(List<User> users) {
            mUsers.clear();
            mUsers.addAll(users);
            notifyDataSetChanged();
        }

        public void addData(User user) {
            mUsers.add(0, user);
            notifyDataSetChanged();
        }

        public class UserViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.avatar)
            CircleImageView mAvatarImageView;

            @BindView(R.id.name)
            TextView mNameTextView;

            @BindView(R.id.email)
            TextView mEmailTextView;

            public UserViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    /*******************************************************************************************************************************************************************************
     * For Real time API
     *******************************************************************************************************************************************************************************/

    @Override
    public void onConnect() {
        NewUnionLog.d("SSE Connected");
    }

    @Override
    public void onMessage(String event, MessageEvent message) throws IOException {
        if ("data".equals(event)) {
            data = jsonObjectMapper.readTree(message.data);
        } else if ("patch".equals(event)) {
            try {
                JsonNode patchNode = jsonObjectMapper.readTree(message.data);
                JsonPatch patch = JsonPatch.fromJson(patchNode);
                data = patch.apply(data);
                updateList();
            } catch (Exception e) {
                NewUnionLog.e(e.toString());
            }
        } else {
            if (message.toString().contains("429 Unknown Error")) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(),
                                getActivity().getString(R.string.error_too_many_api_call),
                                Toast.LENGTH_LONG).show();
                    }
                });
                disconnect();
            } else {
                throw new RuntimeException("Unexpected SSE message: " + event);
            }
        }
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void onClosed(boolean willReconnect) {
    }

    private void updateList() {
        NewUnionLog.d("Updating...");

        try {
            final User user = new User();
            JSONObject jsonObject = null;
            JSONArray jsonArray = new JSONArray(data.toString());
            if (jsonArray != null && jsonArray.length() > 0) {
                jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);
                user.setName(jsonObject.getString("name"));
                user.setEmail(jsonObject.getString("email"));
                user.setPassword(jsonObject.getString("password"));
                user.setAvatar(jsonObject.getString("avatar"));
                NewUnionLog.d(jsonObject.getString("created"));
                DateFormat df = new SimpleDateFormat(CONSTANTS.GSON_DATE_FORMAT);
                try {
                    Date createDate = df.parse(jsonObject.getString("created"));
                    user.setCreatedDate(createDate);
                } catch (ParseException e) {
                    NewUnionLog.e(e.toString());
                }
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListAdapter.addData(user);
                    mUserListRecyclerView.smoothScrollToPosition(0);
                    Toast.makeText(getActivity(), String.format(getString(R.string.message_new_user_created), user.getName()), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            NewUnionLog.e(e.toString());
        }

        NewUnionLog.d("Update completed");

    }


    private void connect() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isNetworkConnected =
                (activeNetwork != null) &&
                        (activeNetwork.isConnectedOrConnecting());

        if (isNetworkConnected) {
            try {
                mEventSource = new EventSource(new URI(CONSTANTS.STREAM_DATA_PROXY_ADDRESS), new URI(CONSTANTS.REST_API_FETCH_NEW_ADDRESS), this, mRequestHeaders);
            } catch (URISyntaxException e) {
                NewUnionLog.e(e.toString());
            }
        } else {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getActivity(),
                            getActivity().getString(R.string.error_no_network_connection),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void disconnect() {
        if (mEventSource != null) {
            mEventSource.close();
        }
        mEventSource = null;
    }
}
