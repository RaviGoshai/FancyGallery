package com.vanravi.fancygallery.data.paging.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.vanravi.fancygallery.data.model.ImageListItem
import com.vanravi.fancygallery.network.api.FancyGalleryApi
import com.vanravi.fancygallery.utils.Constants.Companion.TOTAL_PAGES

class ImagePagingDataSource(private val service: FancyGalleryApi) :
    PagingSource<Int, ImageListItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageListItem> {
        val pageNumber = params.key ?: 1
        return try {
            val response = service.getImages(pageNumber)
            val pagedResponse = response.body()

            var nextPageNumber: Int? = null
            if (pageNumber < TOTAL_PAGES) {
                nextPageNumber = pageNumber + 1
            }

            LoadResult.Page(
                data = pagedResponse.orEmpty(),
                prevKey = null,
                nextKey = nextPageNumber
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageListItem>): Int = 1
}