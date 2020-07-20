package com.vedhafishfarm.fishtaapp.Notifications;

import com.vedhafishfarm.fishtaapp.Notifications.MyResponse;
import com.vedhafishfarm.fishtaapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization: key=AAAA5MHBrmE:APA91bGDnEg7dGn3Hlw4Ny2fRr0kIMvgjn5wR4ThAk50NTs06JMpT_Ik7rLOPmpA-60LCgcWAbsXPl6OMoSyk1ywthsoSDoV5OEkiX-zbiufkf49uDKue9tigDyaKxi3CPGQI_m_YdEc"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
