//package magia.af.ezpay.helper;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Path;
//import android.graphics.Rect;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.widget.PopupMenu;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import ir.mahanco.mahansocial.Feeds_Items.CommentFeed;
//import ir.mahanco.mahansocial.Feeds_Items.CommentItem;
//import ir.mahanco.mahansocial.Feeds_Items.LikeListFeed;
//import ir.mahanco.mahansocial.Feeds_Items.PostListFeed;
//import ir.mahanco.mahansocial.Feeds_Items.PostListItem;
//import ir.mahanco.mahansocial.Feeds_Items.ProfileItem;
//import ir.mahanco.mahansocial.Image.ImageLoader;
//import ir.mahanco.mahansocial.LikeList;
//import ir.mahanco.mahansocial.MainActivity;
//import ir.mahanco.mahansocial.R;
//import ir.mahanco.mahansocial.Utilities.DOMParser;
//import ir.mahanco.mahansocial.Utilities.getPath;
//import ir.mahanco.mahansocial.WholePost;
//import ir.mahanco.mahansocial.profilePage;
//
//
///**
// * show all feeds of pages
// * come from Main Activity
// */
//
//public class Profile_AD_Main extends RecyclerView.Adapter<Profile_AD_Main.PostViewHolder> {
//
//    ProfileItem _profileItem;
//    PostListItem postListItem;
//    private ImageLoader imageLoader;
//    Button likeUnique;
//    TextView tvLikeUnique, tvCommentUnique, fullName, location;
//    int pos;
//    private boolean likeNewsOrComment = true;
//    TextView tvDate_comment_global, tvCaption_comment_global, tvTitle_comment_global;
//    ImageView postPic_comment_global;
//    LinearLayout commentLayoutAll_global, commentLayoutSecond_global, commentLayoutFirst_global;
//    MainActivity cx = null;
//    String userId, path;
//    ImageView btPosterPic, coverPic;
//    SharedPreferences values;
//
//
//    public Profile_AD_Main(MainActivity cx, final ProfileItem profileItem) {
//        this._profileItem = profileItem;
//        this.cx = cx;
//        imageLoader = new ImageLoader(cx);
//        values = cx.getSharedPreferences("MAHAN", 0);
//        Log.e("////+++++++++++++", "Profile_AD_Main: " + _profileItem.getFeeds().getItemCount());
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.e("Request Code 00000 ", "onActivityResult: " + requestCode);
//        Log.e("Result Code  00000", "onActivityResult: " + resultCode);
//        if (requestCode == 2) {
//            SharedPreferences values = cx.getSharedPreferences("MAHAN", 0);
//            if (values.getBoolean("changeC", false)) {
//                _profileItem.getFeeds().getItem(pos).setComment(Integer.valueOf(_profileItem.getFeeds().getItem(pos).getComment()) + 1 + "");
//                tvCommentUnique.setText(_profileItem.getFeeds().getItem(pos).getComment());
//                CommentItem commentItem = (CommentItem) data.getSerializableExtra("commentItem");
//                tvDate_comment_global.setText(commentItem.getDate());
//                tvCaption_comment_global.setText(commentItem.getContent());
//                tvTitle_comment_global.setText(commentItem.getWriter());
//                imageLoader.DisplayImage(commentItem.getUserImage(), postPic_comment_global, true);
//
//                if (_profileItem.getFeeds().getItem(pos).getComment().equals("0"))
//                    commentLayoutAll_global.setVisibility(View.GONE);
//                else if (_profileItem.getFeeds().getItem(pos).getComment().equals("1")) {
//                    commentLayoutAll_global.setVisibility(View.VISIBLE);
//                    commentLayoutFirst_global.setVisibility(View.VISIBLE);
//                    commentLayoutSecond_global.setVisibility(View.GONE);
//                } else {
//                    commentLayoutAll_global.setVisibility(View.VISIBLE);
//                    commentLayoutSecond_global.setVisibility(View.VISIBLE);
//                    commentLayoutFirst_global.setVisibility(View.VISIBLE);
//                }
//            }
//            if (values.getBoolean("changeLT", false)) {
//                _profileItem.getFeeds().getItem(pos).setLike(Integer.valueOf(_profileItem.getFeeds().getItem(pos).getLike()) + 1 + "");
//                _profileItem.getFeeds().getItem(pos).setIsLike(true);
//                tvLikeUnique.setText(_profileItem.getFeeds().getItem(pos).getLike());
//                likeUnique.setBackgroundResource(R.drawable.like_fill);
//            } else if (values.getBoolean("changeLF", false)) {
//                _profileItem.getFeeds().getItem(pos).setLike(Integer.valueOf(_profileItem.getFeeds().getItem(pos).getLike()) - 1 + "");
//                tvLikeUnique.setText(_profileItem.getFeeds().getItem(pos).getLike());
//                _profileItem.getFeeds().getItem(pos).setIsLike(false);
//                likeUnique.setBackgroundResource(R.drawable.like);
//            }
//        } else if (requestCode == 3 && resultCode == -1) {
//            Uri uri = data.getData();
//            path = new getPath().getPath(cx, uri);
//            new AsyncInsertUserImage().execute(path);
//        } else if (requestCode == 4 && resultCode == -1) {
////            coverPicButton.setVisibility(View.INVISIBLE);
//            Uri uri = data.getData();
//            path = new getPath().getPath(cx, uri);
//            new AsyncInsertCoverImage().execute(path);
//        }
//    }
//
//    public class PostViewHolder extends RecyclerView.ViewHolder {
//
//        ImageView userImage, coverImage;
//        RelativeLayout main;
//        LinearLayout commentLayoutAll, commentLayoutSecond, commentLayoutFirst;
//        ImageView postPic, postPic_comment1, postPic_comment2;
//        ImageView posterPic, changeCover;
//        Button like, changeInfoButton, remove_post;
//        LinearLayout showLikeList, changeInfoLayout;
//
//        TextView tvDate_comment1, tvDate_comment2, tvDate, tvCaption_comment1, tvCaption_comment2, tvCaption, tvTitle_comment1, tvTitle_comment2, tvTitle, tvLike, tvComment, tvView, fullName, city;
//
//
//        public PostViewHolder(View itemView, int type) {
//            super(itemView);
//            if (type == 0) {
//                fullName = (TextView) itemView.findViewById(R.id.fullName);
//                city = (TextView) itemView.findViewById(R.id.locText);
//                coverImage = (ImageView) itemView.findViewById(R.id.coverBack);
//                userImage = (ImageView) itemView.findViewById(R.id.user_pic);
//                changeCover = (ImageView) itemView.findViewById(R.id.coverButton);
//                changeInfoButton = (Button) itemView.findViewById(R.id.changeSettingButton);
//                changeInfoLayout = (LinearLayout) itemView.findViewById(R.id.changeSetting);
//            } else {
//
//
//                tvTitle = (TextView) itemView.findViewById(R.id.poster_name);
//                tvCaption = (TextView) itemView.findViewById(R.id.caption);
//                tvDate = (TextView) itemView.findViewById(R.id.date);
//                main = (RelativeLayout) itemView.findViewById(R.id.main_profile_layout);
//                postPic = (ImageView) itemView.findViewById(R.id.post_pic);
//                postPic_comment1 = (ImageView) itemView.findViewById(R.id.poster_pic_comment1);
//                postPic_comment2 = (ImageView) itemView.findViewById(R.id.poster_pic_comment2);
//                posterPic = (ImageView) itemView.findViewById(R.id.poster_pic);
//                like = (Button) itemView.findViewById(R.id.like);
//                tvComment = (TextView) itemView.findViewById(R.id.commentTX);
//                tvView = (TextView) itemView.findViewById(R.id.viewTX);
//                tvLike = (TextView) itemView.findViewById(R.id.likeTX);
//                showLikeList = (LinearLayout) itemView.findViewById(R.id.show_like_list);
//                remove_post = (Button) itemView.findViewById(R.id.post_delete);
//
//                commentLayoutAll = (LinearLayout) itemView.findViewById(R.id.twoCommentsLayout);
//                commentLayoutSecond = (LinearLayout) itemView.findViewById(R.id.secondCommentsLayout);
//                commentLayoutFirst = (LinearLayout) itemView.findViewById(R.id.firstCommentsLayout);
//                tvDate_comment1 = (TextView) itemView.findViewById(R.id.date_comment1);
//                tvDate_comment2 = (TextView) itemView.findViewById(R.id.date_comment2);
//                tvCaption_comment1 = (TextView) itemView.findViewById(R.id.comment_text1);
//                tvCaption_comment2 = (TextView) itemView.findViewById(R.id.comment_text2);
//                tvTitle_comment1 = (TextView) itemView.findViewById(R.id.poster_name_comment1);
//                tvTitle_comment2 = (TextView) itemView.findViewById(R.id.poster_name_comment2);
//
//            }
//        }
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (position == 0)
//            return 0;
//        else
//            return 1;
//    }
//
//    @Override
//    public int getItemCount() {
//        return (_profileItem.getFeeds().getItemCount() + 1);
//    }
//
//    @Override
//    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        switch (viewType) {
//            case 0:
//                return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item_type1, parent, false), viewType);
//            case 1:
//                return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item_type2, parent, false), viewType);
//        }
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(final PostViewHolder holder, final int position) {
//
//        if (position == 0) {
//
//            if (_profileItem.getUserImage() != null)
//                imageLoader.DisplayImage(_profileItem.getUserImage(), holder.userImage, true);
//
//            if (_profileItem.getCoverImage() == null)
//                holder.changeCover.setVisibility(View.VISIBLE);
//            else {
//                holder.changeCover.setVisibility(View.INVISIBLE);
//                imageLoader.DisplayImage(_profileItem.getCoverImage(), holder.coverImage, false);
//            }
//
//            holder.fullName.setText(_profileItem.getFullName());
//            holder.city.setText(_profileItem.getCity());
//            if (values.getString("userId", "").equals(_profileItem.getFeeds().getItem(position).getUserId()))
//                holder.changeInfoLayout.setVisibility(View.VISIBLE);
//            else
//                holder.changeInfoLayout.setVisibility(View.INVISIBLE);
//
//            /**
//             * set onclickListener for 3dot for changing basic info
//             * 3 item : change full name
//             * */
//            holder.changeInfoLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    PopupMenu popup = new PopupMenu(cx, holder.changeInfoButton);
//                    //Inflating the Popup using xml file
//                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
//                    //registering popup with OnMenuItemClickListener
//                    popup.setOnMenuItemClickListener(
//                            new PopupMenu.OnMenuItemClickListener() {
//                                public boolean onMenuItemClick(MenuItem item) {
//                                    if (item.getItemId() == R.id.one) {
//                                        fullName = holder.fullName;
//                                        location = holder.city;
//                                        show_dialog();
//                                    } else if (item.getItemId() == R.id.two) {
//                                        btPosterPic = holder.userImage;
//                                        if (Build.VERSION.SDK_INT < 19) {
//                                            Intent intent = new Intent();
//                                            intent.setType("image/jpeg");
//                                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                                            cx.startActivityForResult(Intent.createChooser(intent, "selll"), 3);
//                                        } else {
//                                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                                            intent.addCategory(Intent.CATEGORY_OPENABLE);
//                                            intent.setType("image/jpeg");
//                                            cx.startActivityForResult(intent, 3);
//                                        }
//                                    } else if (item.getItemId() == R.id.three) {
//                                        coverPic = holder.coverImage;
//                                        if (Build.VERSION.SDK_INT < 19) {
//                                            Intent intent = new Intent();
//                                            intent.setType("image/jpeg");
//                                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                                            cx.startActivityForResult(Intent.createChooser(intent, "selll"), 4);
//                                        } else {
//                                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                                            intent.addCategory(Intent.CATEGORY_OPENABLE);
//                                            intent.setType("image/jpeg");
//                                            cx.startActivityForResult(intent, 4);
//                                        }
//                                    }
//                                    return true;
//                                }
//                            }
//
//                    );
//
//                    popup.show(); //showing popup menu
//                }
//            });
//
//        } else {
//
//            final PostListFeed postListFeed = _profileItem.getFeeds();
//
//            holder.remove_post.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    pos = position - 1;
//                    show_remove_dialog(postListFeed.getItem(position - 1).getFeedId());
//                }
//            });
//
//            if (postListFeed.getItem(position - 1).getComment().equals("0"))
//                holder.commentLayoutAll.setVisibility(View.GONE);
//            else if (postListFeed.getItem(position - 1).getComment().equals("1")) {
//                holder.commentLayoutAll.setVisibility(View.VISIBLE);
//                holder.commentLayoutSecond.setVisibility(View.GONE);
//            } else {
//                holder.commentLayoutAll.setVisibility(View.VISIBLE);
//                holder.commentLayoutSecond.setVisibility(View.VISIBLE);
//            }
//
//            holder.main.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    pos = position - 1;
//                    tvCommentUnique = holder.tvComment;
//                    likeUnique = holder.like;
//                    tvLikeUnique = holder.tvLike;
//
//                    if (postListFeed.getItem(pos).getComment().equals("0")) {
//                        tvCaption_comment_global = holder.tvCaption_comment1;
//                        tvTitle_comment_global = holder.tvTitle_comment1;
//                        tvDate_comment_global = holder.tvDate_comment1;
//                        postPic_comment_global = holder.postPic_comment1;
//                    } else {
//                        tvCaption_comment_global = holder.tvCaption_comment2;
//                        tvTitle_comment_global = holder.tvTitle_comment2;
//                        tvDate_comment_global = holder.tvDate_comment2;
//                        postPic_comment_global = holder.postPic_comment2;
//                    }
//
//                    commentLayoutAll_global = holder.commentLayoutAll;
//                    commentLayoutSecond_global = holder.commentLayoutSecond;
//                    commentLayoutFirst_global = holder.commentLayoutFirst;
//
//                    values.edit().remove("changeC").apply();
//                    values.edit().remove("changeLT").apply();
//                    values.edit().remove("changeLF").apply();
//                    userId = postListFeed.getItem(pos).getUserId();
//                    new AsyncLoadComment().execute(postListFeed.getItem(position - 1).getFeedId(), "0");
//                }
//            });
//
//            holder.posterPic.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    new AsyncLoadProfile().execute(postListFeed.getItem(position - 1).getUserId());
//                }
//            });
//
//            imageLoader.DisplayImage(postListFeed.getItem(position - 1).getComment1().getUserImage(), holder.postPic_comment1, true);
//            imageLoader.DisplayImage(postListFeed.getItem(position - 1).getComment2().getUserImage(), holder.postPic_comment2, true);
//            imageLoader.DisplayImage(postListFeed.getItem(position - 1).getUserImage(), holder.posterPic, true);
//
//            /**
//             * if the post doesn't have picture the ImageView should be GONE
//             * */
//            if (postListFeed.getItem(position - 1).getContentImage() == null)
//                holder.postPic.setVisibility(View.GONE);
//            else {
//                imageLoader.DisplayImage(postListFeed.getItem(position - 1).getContentImage(), holder.postPic, false);
//                holder.postPic.setVisibility(View.VISIBLE);
//            }
//
//            holder.tvCaption.setText(postListFeed.getItem(position - 1).getContent());
//            holder.tvDate.setText(postListFeed.getItem(position - 1).getDate());
//            holder.tvTitle.setText(postListFeed.getItem(position - 1).getWriter());
//
//            holder.like.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    likeNewsOrComment = false;
//                    //** assign the values to global for use in postExecute of asyncTask*//*
//                    tvLikeUnique = holder.tvLike;
//                    likeUnique = holder.like;
//
//                    /**
//                     * check if feed is like before then delete the like with action ;
//                     j -> action (for delete like or like)
//                     * */
//                    int j = (postListFeed.getItem(position - 1).getIsLike()) ? 0 : 1;
//                    postListItem = postListFeed.getItem(position - 1);
//                    new AsyncInsertLike().execute(postListFeed.getItem(position - 1).getFeedId(), j + "");
//                }
//            });
//
//            if (postListFeed.getItem(position - 1).getIsLike())
//                holder.like.setBackgroundResource(R.drawable.like_fill);
//            else
//                holder.like.setBackgroundResource(R.drawable.like);
//
//            holder.tvLike.setText(postListFeed.getItem(position - 1).getLike());
//            holder.tvComment.setText(postListFeed.getItem(position - 1).getComment());
//            holder.tvView.setText("0");
//            holder.showLikeList.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    new AsyncShowLikeList().execute("likes", postListFeed.getItem(position - 1).getFeedId());
//                }
//            });
//
//            holder.tvTitle_comment1.setText(postListFeed.getItem(position - 1).getComment1().getWriter());
//            holder.tvTitle_comment2.setText(postListFeed.getItem(position - 1).getComment2().getWriter());
//            holder.tvCaption_comment1.setText(postListFeed.getItem(position - 1).getComment1().getContent());
//            holder.tvCaption_comment2.setText(postListFeed.getItem(position - 1).getComment2().getContent());
//            holder.tvDate_comment1.setText(postListFeed.getItem(position - 1).getComment1().getDate());
//            holder.tvDate_comment2.setText(postListFeed.getItem(position - 1).getComment2().getDate());
//
//        }
//    }
//
//    AlertDialog show;
//
//    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
//        int targetWidth = 300;
//        int targetHeight = 300;
//        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
//                targetHeight, Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(targetBitmap);
//        Path path = new Path();
//        path.addCircle(((float) targetWidth - 1) / 2,
//                ((float) targetHeight - 1) / 2,
//                (Math.min(((float) targetWidth),
//                        ((float) targetHeight)) / 2),
//                Path.Direction.CCW);
//
//        canvas.clipPath(path);
//        Bitmap sourceBitmap = scaleBitmapImage;
//        canvas.drawBitmap(sourceBitmap,
//                new Rect(0, 0, sourceBitmap.getWidth(),
//                        sourceBitmap.getHeight()),
//                new Rect(0, 0, targetWidth, targetHeight), null);
//        return targetBitmap;
//    }
//
//    public void show_dialog() {
//
//        final AlertDialog.Builder editAlert = new AlertDialog.Builder(cx);
//        LayoutInflater inflater = (LayoutInflater) cx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.job_dialog, null);
//
//        Button bt = (Button) view.findViewById(R.id.confirm);
//        final EditText ed1 = (EditText) view.findViewById(R.id.nameED);
//        final EditText ed2 = (EditText) view.findViewById(R.id.posED);
//
//        editAlert.setView(view);
//        show = editAlert.show();
//
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences values = cx.getSharedPreferences("MAHAN", 0);
//                values.edit().putString("job", ed1.getText().toString()).apply();
//                values.edit().putString("location", ed2.getText().toString()).apply();
//                new AsyncInsertBasicInfo().execute(ed1.getText().toString(), ed2.getText().toString());
//            }
//        });
//
//
//    }
//
//    public void show_remove_dialog(final String feedId) {
//
//        final AlertDialog.Builder editAlert = new AlertDialog.Builder(cx);
//        LayoutInflater inflater = (LayoutInflater) cx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.remove_dialog, null);
//
//        Button bt = (Button) view.findViewById(R.id.confirm);
//        Button bt_cancel = (Button) view.findViewById(R.id.cancel);
//
//        editAlert.setView(view);
//        show = editAlert.show();
//
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new AsyncDelete().execute(feedId);
//            }
//        });
//
//        bt_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                show.dismiss();
//            }
//        });
//
//
//    }
//
//    private class AsyncDelete extends AsyncTask<String, Void, Boolean> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            cx.progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            SharedPreferences values = cx.getSharedPreferences("MAHAN", 0);
//            DOMParser myParser = new DOMParser();
//            return myParser.deletePost(values.getString("cookies", "") + " " + values.getString("cookies_login", "")
//                    , values.getString("token", ""), params[0], values.getString("userId", ""), "user");
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            cx.progressBar.setVisibility(View.INVISIBLE);
//            if (result) {
//                cx._profileItem.getFeeds().removeItem(pos);
//                cx.adapter_profile.notifyDataSetChanged();
//                show.dismiss();
//                Toast.makeText(cx, "Post deleted !", Toast.LENGTH_SHORT).show();
//            } else
//                Toast.makeText(cx, "Error In Connection !", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private class AsyncInsertLike extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            cx.progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            SharedPreferences values = cx.getSharedPreferences("MAHAN", 0);
//            DOMParser myParser = new DOMParser();
//            if (likeNewsOrComment)
//                return myParser.insertLikeProfileFeed(values.getString("cookies", "") + " " + values.getString("cookies_login", ""), values.getString("userId", ""), params[0], values.getString("token", ""), params[1], "C");
//            else
//                return myParser.insertLikeProfileFeed(values.getString("cookies", "") + " " + values.getString("cookies_login", ""), values.getString("userId", ""), params[0], values.getString("token", ""), params[1], "N");
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            cx.progressBar.setVisibility(View.INVISIBLE);
//            if (result != null) {
//                if (result.equals("like")) {
//                    postListItem.setIsLike(true);
//                    postListItem.setLike(Integer.valueOf(postListItem.getLike()) + 1 + "");
//                } else if (result.equals("dislike")) {
//                    postListItem.setIsLike(false);
//                    postListItem.setLike(Integer.valueOf(postListItem.getLike()) - 1 + "");
//                }
//                tvLikeUnique.setText(postListItem.getLike());
//                if (postListItem.getIsLike())
//                    likeUnique.setBackgroundResource(R.drawable.like_fill);
//                else
//                    likeUnique.setBackgroundResource(R.drawable.like);
//            } else {
//                Toast.makeText(cx, "Error In Connection", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private class AsyncInsertBasicInfo extends AsyncTask<String, Void, Boolean> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            cx.progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            SharedPreferences values = cx.getSharedPreferences("MAHAN", 0);
//            DOMParser myParser = new DOMParser();
//            return myParser.editBasicInfo(values.getString("cookies", "") + " " + values.getString("cookies_login", ""), values.getString("userId", ""), values.getString("token", ""), params[0], params[1]);
//
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            cx.progressBar.setVisibility(View.INVISIBLE);
//            if (result != false) {
//                show.dismiss();
//                SharedPreferences values = cx.getSharedPreferences("MAHAN", 0);
//                fullName.setText(values.getString("job", "نامشخص"));
//                location.setText(values.getString("location", "نامشخص"));
//            } else {
//                Toast.makeText(cx, "Error In Connection", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    public class AsyncInsertUserImage extends AsyncTask<String, Void, Boolean> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            cx.progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            SharedPreferences values = cx.getSharedPreferences("MAHAN", 0);
//            DOMParser myParser = new DOMParser();
//            return myParser.changeUserImage(values.getString("cookies", "") + " " + values.getString("cookies_login", ""), values.getString("userId", ""), values.getString("token", ""), params[0]);
//
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            cx.progressBar.setVisibility(View.INVISIBLE);
//            if (result != false) {
//                Toast.makeText(cx, "OK !", Toast.LENGTH_SHORT).show();
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
//                btPosterPic.setImageBitmap(getRoundedShape(bitmap));
//            } else {
//                Toast.makeText(cx, "Error In Connection", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private class AsyncInsertCoverImage extends AsyncTask<String, Void, Boolean> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            cx.progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            SharedPreferences values = cx.getSharedPreferences("MAHAN", 0);
//            DOMParser myParser = new DOMParser();
//            return myParser.changeCoverImage(values.getString("cookies", "") + " " + values.getString("cookies_login", ""), values.getString("userId", ""), values.getString("token", ""), params[0]);
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            cx.progressBar.setVisibility(View.INVISIBLE);
//            if (result != false) {
//                Toast.makeText(cx, "OK !", Toast.LENGTH_SHORT).show();
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
//                coverPic.setImageBitmap(bitmap);
//            } else {
//                Toast.makeText(cx, "Picture is more than 2M !", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private class AsyncLoadComment extends AsyncTask<String, Void, CommentFeed> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            cx.progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected CommentFeed doInBackground(String... params) {
//            SharedPreferences values = cx.getSharedPreferences("MAHAN", 0);
//            DOMParser myParser = new DOMParser();
//            return myParser.getCommentsProfileFeed(values.getString("cookies", "") + " " + values.getString("cookies_login", ""), values.getString("userId", ""), params[0], params[1]);
//        }
//
//        @Override
//        protected void onPostExecute(CommentFeed result) {
//            cx.progressBar.setVisibility(View.INVISIBLE);
//            if (result != null) {
//                cx.startActivityForResult(new Intent(cx, WholePost.class).putExtra("postItem", _profileItem.getFeeds().getItem(pos))
//                        .putExtra("commentFeed", result).putExtra("kind", false).putExtra("userId", userId), 2);
//            } else
//                Toast.makeText(cx, "Error In Connection !", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private class AsyncShowLikeList extends AsyncTask<String, Void, LikeListFeed> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            cx.progressBar.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected LikeListFeed doInBackground(String... params) {
//            SharedPreferences values = cx.getSharedPreferences("MAHAN", 0);
//            DOMParser myParser = new DOMParser();
//            return myParser.getLikeListProfileFeed(values.getString("cookies", "") + " " + values.getString("cookies_login", ""), values.getString("userId", ""), params[0], params[1]);
//
//        }
//
//        @Override
//        protected void onPostExecute(LikeListFeed result) {
//            cx.progressBar.setVisibility(View.INVISIBLE);
//            if (result != null) {
//                if (result.getItemCount() > 0)
//                    cx.startActivity(new Intent(cx, LikeList.class).putExtra("likeList", result));
////                else
////                    Toast.makeText(cx, "Nothing to show !", Toast.LENGTH_SHORT).show();
//
//            } else {
//                Toast.makeText(cx, "Error In Connection", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private class AsyncLoadProfile extends AsyncTask<String, Void, ProfileItem> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            cx.progressBar.setVisibility(View.VISIBLE);
//
//        }
//
//        @Override
//        protected ProfileItem doInBackground(String... params) {
//            DOMParser myParser = new DOMParser();
//            SharedPreferences values = cx.getSharedPreferences("MAHAN", 0);
//            return myParser.getProfile(values.getString("cookies", "") + " " + values.getString("cookies_login", ""), params[0], "0");
//        }
//
//        @Override
//        protected void onPostExecute(ProfileItem result) {
//            cx.progressBar.setVisibility(View.INVISIBLE);
//            if (result != null) {
//                cx.startActivity(new Intent(cx, profilePage.class).putExtra("profileItem", result).putExtra("userId", userId));
//            } else {
//                Toast.makeText(cx, "Error In Connection", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//}
