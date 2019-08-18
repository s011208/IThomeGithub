package yhh.ithome.repository.github

import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import yhh.ithome.repository.BuildConfig
import yhh.ithome.repository.github.entity.UserEntity
import yhh.ithome.repository.github.interceptor.BasicAuthInterceptor

class GithubRepository {

    private interface GithubApi {
        @GET("/user")
        fun user(
            @Query("client_id") clientId: String = BuildConfig.GITHUB_CLIENT_ID,
            @Query("client_secret") clientSecret: String = BuildConfig.GITHUB_CLIENT_SECRET
        ): Single<UserEntity>
    }

    private lateinit var githubApi: GithubApi

    private fun createService(userName: String, password: String) {
        githubApi =
            Retrofit
                .Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(BasicAuthInterceptor(userName, password))
                        .build()
                )
                .build()
                .create(GithubApi::class.java)
    }

    fun logIn(userName: String, password: String) =
        Single.fromCallable { createService(userName, password) }
            .flatMap { githubApi.user() }
}