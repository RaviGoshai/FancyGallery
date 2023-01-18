package com.vanravi.fancygallery.utils

import com.vanravi.fancygallery.BuildConfig

class Constants {
    companion object {
        val BASE_URL = "https://picsum.photos/"
        const val QUERY_PER_PAGE = 20
        const val TOTAL_IMAGES = 1000
        const val TOTAL_PAGES = TOTAL_IMAGES / QUERY_PER_PAGE
        const val DEFAULT_PAGE_INDEX = 1
        const val TAG = "FancyGalleryApp"
        const val IMAGE_QUALITY = 80
    }
}