package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.webView

class ArticleFragment: Fragment(R.layout.fragment_article) {


    lateinit var viewModel: NewsViewModel

    private lateinit var webView: WebView
    private lateinit var saveArticleButton: FloatingActionButton

    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).mViewModel

        webView = view.findViewById(R.id.webView)
        saveArticleButton = view.findViewById(R.id.fab)

        val article = args.article

        webView.apply {
            //to make sure the page always load in webview instead of standard browser of phone
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        saveArticleButton.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }
}