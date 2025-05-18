package com.euntaek.uicomponent.cachedasyncImage

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.placeholder
import coil3.toBitmap

/**
 * A composable that asynchronously loads and displays an image from the provided URL with caching.
 *
 * @param imageUrl The URL of the image to load. This is used as the memory and disk cache key.
 * @param modifier Modifier used to adjust the layout algorithm or draw decoration content.
 * @param contentScale Optional scale parameter used to determine the aspect ratio scaling to be
 *  used if the bounds are a different size from the intrinsic size of the [AsyncImagePainter].
 * @param placeholderDrawableResId Optional drawable resource ID to display as a placeholder
 *  while the image is loading or if it fails to load.
 * @param onPalette Optional callback that returns a [Palette] generated from the successfully
 *  loaded image. Useful for color extraction or theming based on image content.
 */
@Composable
fun CachedAsyncImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    @DrawableRes placeholderDrawableResId: Int? = null,
    onPalette: ((Palette) -> Unit)? = null
) {
    val context = LocalContext.current

    val imageRequest = ImageRequest.Builder(context).data(imageUrl).diskCacheKey(imageUrl)
        .diskCachePolicy(CachePolicy.ENABLED).memoryCacheKey(imageUrl)
        .memoryCachePolicy(CachePolicy.ENABLED).apply {
            if (placeholderDrawableResId != null) {
                this.placeholder(placeholderDrawableResId)
            }
        }
        .allowHardware(false) // Disable hardware bitmaps as Palette needs to read the image's pixels.
        .build()

    SubcomposeAsyncImage(
        modifier = modifier,
        contentScale = contentScale,
        model = imageRequest,
        contentDescription = null,
        onSuccess = onPalette?.let {
            { result ->
                val bitmap = result.result.image.toBitmap()
                val palette = Palette.from(bitmap).generate()
                onPalette(palette)
            }
        }
    )
}
