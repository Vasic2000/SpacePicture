package com.example.spacepicture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import coil.api.load
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class MainActivity : AppCompatActivity() {

    private val retrofitImpl: RetrofitImpl = RetrofitImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendServerRequest()
    }

    private fun renderData(dataModel: DataModel?, error: Throwable?) {
        if (dataModel == null || error != null) {
            //Ошибка
        } else {
            val url = dataModel.url
            if (url.isNullOrEmpty()) {
                text_view_title.text = "Ссылка пустая"
            } else {
                image_view.load(url)
            }

            val explanation = dataModel.explanation
            if (explanation.isNullOrEmpty()) {
                //"Описание пустое"
            } else {
                text_view.text = explanation
            }

            val title = dataModel.title
            if (title.isNullOrEmpty()) {
                //"Название пустое"
            } else {
                text_view_title.text = title
            }
        }
    }

    private fun sendServerRequest() {
        retrofitImpl.getRetrofit().getPictureOfTheDay("DEMO_KEY").enqueue(object :
                Callback<DataModel> {

            override fun onResponse(
                    call: Call<DataModel>,
                    response: Response<DataModel>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    renderData(response.body()!!, null)
                } else {
                    renderData(null, Throwable("Ответ от сервера пустой"))
                }
            }

            override fun onFailure(call: Call<DataModel>, t: Throwable) {
                renderData(null, t)
            }
        })
    }
}

data class DataModel(
        val explanation: String?,
        val url: String?,
        val title: String?
)

interface PictureOfTheDayAPI {
    @GET("planetary/apod")
    fun getPictureOfTheDay(@Query("api_key") apiKey: String): Call<DataModel>
}

class RetrofitImpl {

    fun getRetrofit(): PictureOfTheDayAPI {
        val podRetrofit = Retrofit.Builder()
                .baseUrl("https://api.nasa.gov/")
                .addConverterFactory(
                        GsonConverterFactory.create(
                                GsonBuilder().setLenient().create()
                        )
                )
                .build()
        return podRetrofit.create(PictureOfTheDayAPI::class.java)
    }
}
