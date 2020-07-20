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
                    "Authorization: key=AAAAavhGQ80:APA91bGg83UJwTh7a5fjzhqQ_bU3jlC3ewQJZKYQ3KeIPkowF9gYA9uevpetbeJvHwxIzecTt66NUlNZRWuBTZhWAHyjJOwVGhvhIM7yCTRDHLunlynHS3k2AKEv_0t3Pi5vnoj290ZR"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
