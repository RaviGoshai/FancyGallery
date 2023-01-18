package com.vanravi.fancygallery.network.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.vanravi.fancygallery.data.db.ImageListDb
import com.vanravi.fancygallery.data.db.dao.ImageListDao
import com.vanravi.fancygallery.data.model.ImageListItem
import com.vanravi.fancygallery.data.paging.datasource.ImagePagingDataSource
import com.vanravi.fancygallery.data.paging.remotemediator.ImageListRemoteMediator
import com.vanravi.fancygallery.network.api.FancyGalleryApi
import com.vanravi.fancygallery.utils.Constants.Companion.QUERY_PER_PAGE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
class ImageListRepositoryImpl @Inject constructor(
    private val picsumApi: FancyGalleryApi,
    private val localDataSource: ImageListDao,
    private val imageListDb: ImageListDb
) : ImageListRepository {

    override suspend fun getImages(): Flow<PagingData<ImageListItem>> {
        val pagingSourceRemote = { ImagePagingDataSource(picsumApi) }
        val pagingSourceLocal = { imageListDb.getImageListDao().getAllImages() }
        return Pager(
            config = PagingConfig(pageSize = QUERY_PER_PAGE, prefetchDistance = 2),
            remoteMediator = ImageListRemoteMediator(picsumApi, imageListDb),
            pagingSourceFactory = pagingSourceLocal
        ).flow
    }

    override suspend fun saveImageItem(item: ImageListItem) = localDataSource.upsert(item)

    override suspend fun getSavedImages() = localDataSource.getAllImages()

    override suspend fun getSavedImagesList() = localDataSource.getAllImagesList()

    override
    suspend fun deleteAllImages() = localDataSource.deleteAllImages()
}