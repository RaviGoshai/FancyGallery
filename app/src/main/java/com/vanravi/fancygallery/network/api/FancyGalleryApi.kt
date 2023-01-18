package com.vanravi.fancygallery.network.api

import com.vanravi.fancygallery.data.model.ImageListItem
import com.vanravi.fancygallery.utils.Constants.Companion.QUERY_PER_PAGE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FancyGalleryApi {

    @GET("v2/list")
    suspend fun getImages(
        @Query("page")
        pageNumber: Int = 1,
        @Query("limit")
        pageSize: Int = QUERY_PER_PAGE
    ): Response<List<ImageListItem>>
}