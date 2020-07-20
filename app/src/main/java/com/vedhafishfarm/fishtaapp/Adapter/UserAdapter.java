package com.vedhafishfarm.fishtaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.vedhafishfarm.fishtaapp.Fragments.ProfileFragment;
import com.vedhafishfarm.fishtaapp.MainActivity;
import com.vedhafishfarm.fishtaapp.Model.User;
import com.vedhafishfarm.fishtaapp.Notifications.APIService;
import com.vedhafishfarm.fishtaapp.Notifications.Client;
import com.vedhafishfarm.fishtaapp.Notifications.Data;
import com.vedhafishfarm.fishtaapp.Notifications.MyResponse;
import com.vedhafishfarm.fishtaapp.Notifications.Sender;
import com.vedhafishfarm.fishtaapp.R;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;

    private FirebaseUser firebaseUser;
    private APIService apiService;
    private String tempUserName;
    private String tempUserImage;

    public UserAdapter(Context context, List<User> users, boolean isFragment){
        mContext = context;
        mUsers = users;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ImageViewHolder holder, final int position) {

        //Api Service
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);
        isFollowing(user.getId(), holder.btn_follow);

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(true)
                .centerCrop()
                .dontAnimate()
                .dontTransform()
                .override(15, 15)
                .placeholder(R.drawable.placeholder)
                .priority(Priority.IMMEDIATE)
                .encodeFormat(Bitmap.CompressFormat.PNG)
                .format(DecodeFormat.DEFAULT);

        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());
        Glide.with(mContext)
                .applyDefaultRequestOptions(requestOptions)
                .load(user.getImageurl()).into(holder.image_profile);

        if (user.getId().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFragment) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getId());
                    editor.apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                } else {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("publisherid", user.getId());
                    mContext.startActivity(intent);
                }
            }
        });

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btn_follow.getText().toString().equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotification(user.getId());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }

        });
    }

    private void addNotification(String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.keepSynced(true);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);


        reference.push().setValue(hashMap);

        DatabaseReference PReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(firebaseUser.getUid());

        PReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tempUserName = user.getUsername();
                tempUserImage = user.getImageurl();
                //System.out.println(user.getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference mreference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        mreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                String GivenToken = user.getToken();// getter method from your model
                //System.out.println(GivenToken);
                sendNotifications (GivenToken, "Fishta", tempUserName+" followed you", tempUserImage );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        UpdateToken();
    }

    private void UpdateToken() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.child("token").setValue(FirebaseInstanceId.getInstance().getToken());


    }

    //Method for sendNotification

    public void sendNotifications(String GivenToken, String title, String message, String imageURL) {
        Data data = new Data(title, message, imageURL);
        Sender sender = new Sender(data, GivenToken);

        apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1 )  {
                        //Toast.makeText(UserAdapter.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_follow;

        public ImageViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);
        }
    }

    private void isFollowing(final String userid, final Button button){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()) {
                    button.setText("following");
                } else {
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}