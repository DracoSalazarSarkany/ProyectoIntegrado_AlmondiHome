package com.example.interfaces1_2trimestre;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IAService {
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    Call<IAResponse> getGeminiResponse(
            @Query("key") String apiKey,
            @Body IARequest request
    );
}
