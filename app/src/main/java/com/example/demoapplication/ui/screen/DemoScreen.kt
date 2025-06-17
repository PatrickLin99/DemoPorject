package com.example.demoapplication.ui.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.demoapplication.model.UiState
import com.example.demoapplication.model.Url
import org.koin.androidx.compose.koinViewModel

@Composable
fun DemoScreen(modifier: Modifier) {
    val demoViewModel = koinViewModel<DemoViewModel>()
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val uiState = demoViewModel.uiState) {
            is UiState.Loading -> {
                DemoLoadingScreen()
            }

            is UiState.Success -> {
                DemoSuccessScreen(uiState.item.item)
            }

            is UiState.Error -> {
                DemoErrorScreen()
            }
        }
    }
}

@Composable
fun DemoSuccessScreen(urls: List<Url>) {
    val pageState = rememberPagerState(pageCount = { urls.size })
    HorizontalPager(
        modifier = Modifier.fillMaxSize(),
        state = pageState
    ) {
        ComponentWebview(urls[it].item)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ComponentWebview(url: String) {
    val demoViewModel = koinViewModel<DemoViewModel>()
    var isWebViewLoaded by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    val hasScreenshot = demoViewModel.screenShot.containsKey(url)

    val config = LocalConfiguration.current

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasScreenshot) {
            demoViewModel.screenShot[url]?.let { bitmap ->
                ConstraintLayout {
                    val (img, note) = createRefs()
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .constrainAs(img) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                end.linkTo(parent.end)
                            },
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Cached Screenshot",
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        modifier = Modifier.constrainAs(note) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        },
                        text = "This is a Screenshot", color = Color.DarkGray, fontSize = 20.sp
                    )
                }
            }
        } else {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        webViewClient = WebViewClient()
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                if (newProgress == 100) {
                                    isWebViewLoaded = true
                                    // 延遲一下確保頁面完全載入
                                    view?.postDelayed({
                                        getWebViewScreenshot(
                                            view,
                                            config.screenWidthDp,
                                            config.screenHeightDp
                                        )?.let {
                                            demoViewModel.screenShot[url] = it
                                        }
                                    }, 500)
                                }
                            }
                        }
                        loadUrl(url)
                        webViewRef = this
                    }
                }
            )

            if (!isWebViewLoaded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.DarkGray)
                }
            }
        }
    }
}

private fun getWebViewScreenshot(
    webView: WebView?,
    screenWidth: Int,
    screenHeight: Int
): Bitmap? = webView?.let { view ->
    val width = maxOf(view.width, screenWidth)
    val height = maxOf(view.height, screenHeight)

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    view.draw(canvas)
    bitmap
}


@Composable
fun DemoLoadingScreen() {
    CircularProgressIndicator(
        color = Color.DarkGray
    )
}

@Composable
fun DemoErrorScreen() {
    Text(
        text = "Oops, something went wrong..."
    )
}