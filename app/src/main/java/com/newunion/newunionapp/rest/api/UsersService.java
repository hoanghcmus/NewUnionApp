package com.newunion.newunionapp.rest.api;

import com.newunion.newunionapp.pojo.LoginResult;
import com.newunion.newunionapp.pojo.User;

import java.util.List;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import rx.Observable;

/**
 * <p> Rest APIs
 * </p>
 * Created by Nguyen Ngoc Hoang on 7/20/2016.
 *
 * @author Nguyen Ngoc Hoang (www.hoangvnit.com)
 */
public interface UsersService {
    /**
     * Authorization: m eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwcm92aWRlciI6ImxvY2FsIiwibmFtZSI6Im5nb2Nob2FuZyIsImVtYWlsIjoibmdvY2hvYW5nLm5ndXllbkBnbWFpbC5jb20iLCJfaWQiOiI1NzkwMzFmZTM4MTM4NDQyMTUyNmJjNWUiLCJfX3YiOjAsImNyZWF0ZWQiOiIyMDE2LTA3LTIxVDAyOjIyOjU0LjM1M1oiLCJhdmF0YXIiOiJodHRwczovL2NkbjQuaWNvbmZpbmRlci5jb20vZGF0YS9pY29ucy91c2VyLWF2YXRhci1mbGF0LWljb25zLzUxMi9Vc2VyX0F2YXRhci0zMy01MTIucG5nIn0.dK8F7PJiE-T4mR8FmhRC5pBzwzcR4VIqTTJdd0hR7t0
     */
    @Headers({"Cache-Control: no-cache"})
    @GET("/users")
    Observable<List<User>> getUsers();

    @Headers({"Cache-Control: no-cache"})
    @POST("/create")
    Observable<Response> signUp(@Body User userModel);

    @Headers({"Cache-Control: no-cache"})
    @POST("/auth/signin")
    Observable<LoginResult> signIn(@Body User info);
}
