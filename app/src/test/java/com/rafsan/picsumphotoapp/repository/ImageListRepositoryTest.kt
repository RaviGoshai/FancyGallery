package com.rafsan.picsumphotoapp.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.rafsan.picsumphotoapp.data.local.ImageListDao
import com.rafsan.picsumphotoapp.data.local.ImageListDb
import com.rafsan.picsumphotoapp.network.api.ApiHelper
import com.rafsan.picsumphotoapp.network.api.ApiHelperImpl
import com.rafsan.picsumphotoapp.network.api.PicsumApi
import com.rafsan.picsumphotoapp.network.repository.ImageListRepository
import com.rafsan.picsumphotoapp.util.FakeDataUtil
import com.rafsan.picsumphotoapp.util.MainCoroutineRule
import com.rafsan.picsumphotoapp.util.MockWebServerBaseTest
import com.rafsan.picsumphotoapp.util.runBlockingTest
import com.rafsan.picsumphotoapp.utils.NetworkResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.robolectric.RobolectricTestRunner
import java.net.HttpURLConnection

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class ImageListRepositoryTest : MockWebServerBaseTest() {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    private lateinit var imageListRepo: ImageListRepository
    private lateinit var imageListDb: ImageListDb
    private lateinit var imageListDao: ImageListDao
    private lateinit var apiHelper: ApiHelper
    private lateinit var picsumApi: PicsumApi
    private lateinit var apiHelperImpl: ApiHelperImpl
    private val page = ArgumentMatchers.anyInt()

    override fun isMockServerEnabled(): Boolean = true

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        imageListDb = Room.inMemoryDatabaseBuilder(
            context, ImageListDb::class.java
        ).allowMainThreadQueries().build()
        imageListDao = imageListDb.getImageListDao()
        picsumApi = provideTestApiService()
        apiHelperImpl = ApiHelperImpl(picsumApi)
        apiHelper = apiHelperImpl
        imageListRepo = ImageListRepository(apiHelper, imageListDao)
    }

    @Test
    fun `test image item insertion in DB`() {
        coroutineRule.runBlockingTest {
            imageListRepo.saveImageItem(FakeDataUtil.getFakeImage())
            val savedImage = imageListRepo.getSavedImagesList()
            assertThat(savedImage.isNotEmpty()).isTrue()
            assertThat(savedImage.size).isEqualTo(1)
        }
    }

    @Test
    fun `test image item deletion in DB`() {
        coroutineRule.runBlockingTest {
            imageListRepo.deleteAllImages()
            val savedImage = imageListRepo.getSavedImagesList()
            assertThat(savedImage.isEmpty()).isTrue()
            assertThat(savedImage.size).isEqualTo(0)
        }
    }

    @Test
    fun `test saved image item`() {
        coroutineRule.runBlockingTest {
            val fakeImage = FakeDataUtil.getFakeImage()
            imageListRepo.saveImageItem(fakeImage)
            val savedImages = imageListRepo.getSavedImagesList()
            assertThat(savedImages.isNotEmpty()).isTrue()
            assertThat(savedImages.get(0).id == fakeImage.id).isTrue()
            assertThat(savedImages.get(0).url == fakeImage.url).isTrue()
        }
    }

    @Test
    fun `given response ok when fetching results then return a list with elements`() {
        runBlocking {
            mockHttpResponse("image_list_response.json", HttpURLConnection.HTTP_OK)
            val apiResponse = imageListRepo.getImages(page)

            assertThat(apiResponse).isNotNull()
            assertThat(apiResponse.data).hasSize(10)
        }
    }

    @Test
    fun `given response ok when fetching empty results then return an empty list`() {
        runBlocking {
            mockHttpResponse("image_response_empty_list.json", HttpURLConnection.HTTP_OK)
            val apiResponse = imageListRepo.getImages(page)
            assertThat(apiResponse).isNotNull()
            assertThat(apiResponse.data).hasSize(0)
        }
    }

    @Test
    fun `given response failure when fetching results then return exception`() {
        runBlocking {
            mockHttpResponse(502)
            val apiResponse = imageListRepo.getImages(page)

            Assert.assertNotNull(apiResponse)
            val expectedValue = NetworkResult.Error("An error occurred", null)
            assertThat(expectedValue.message).isEqualTo(apiResponse.message)
        }
    }

    @After
    fun release() {
        imageListDb.close()
    }
}