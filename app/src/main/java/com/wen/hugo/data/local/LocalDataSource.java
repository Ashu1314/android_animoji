package com.wen.hugo.data.local;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.avos.avoscloud.AVException;
import com.wen.hugo.bean.Comment;
import com.wen.hugo.bean.Status;
import com.wen.hugo.bean.User;
import com.wen.hugo.data.DataSource;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by hugo on 11/21/17.
 */

public class LocalDataSource implements DataSource {
    @Override
    public Observable<List<Status>> getNewStatus(int skip, int limit) {
        return null;
    }

    @Override
    public Observable<List<Status>> getTimeline(User my, long maxId, int limit) {
        return null;
    }

    @Override
    public Observable<List<Status>> getUserStatus(User user, long maxId, int limit) {
        return null;
    }

    @Override
    public Observable<List<User>> getFollowers(User user, int skip, int limit) {
        return null;
    }

    @Override
    public Observable<List<User>> getFollowings(User user, int skip, int limit) {
        return null;
    }

    @Override
    public Observable<List<Comment>> getComments(Status status, int skip, int limit) {
        return null;
    }

    @Override
    public boolean getRelationship(User user, boolean isFollower) {
        return false;
    }

    @Override
    public Observable<String> addSendStatus(@NonNull final String content,final Bitmap bitmap) {
           return null;
    }

    @Override
    public void addComment(@NonNull Comment comment) {

    }

    @Override
    public String addUploadFile(@NonNull Bitmap bitmap) {
        return null;
    }

    @Override
    public void updateFollowAction(User user, boolean follow) {

    }

    @Override
    public void updateStatusLikes(Status status, List<String> likes) {

    }

    @Override
    public void deleteStatus(Status status) {

    }

    @Override
    public void deleteComment(Comment comment) {

    }

    @Override
    public void deleteFile(String url) {

    }

    @Override
    public void login(String username, String password) throws AVException {

    }


    @Override
    public void signUp(User user) {

    }

    @Override
    public boolean IMLogin(String username, String password) {
        return false;
    }

    @Override
    public boolean IMRegister(String username, String password) {
        return false;
    }
}
