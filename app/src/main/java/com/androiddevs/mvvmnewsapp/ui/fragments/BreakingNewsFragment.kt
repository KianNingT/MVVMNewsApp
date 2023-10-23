package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.androiddevs.mvvmnewsapp.util.Constants
import com.androiddevs.mvvmnewsapp.util.Resource

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {


    lateinit var viewModel: NewsViewModel

    lateinit var newsAdapter: NewsAdapter

//    private val rvBreakingNews by lazy {
//        view?.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvBreakingNews)
//    }

    private lateinit var rvBreakingNews: RecyclerView

    private lateinit var paginationProgressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvBreakingNews = view.findViewById(R.id.rvBreakingNews)
        paginationProgressBar = view.findViewById(R.id.paginationProgressBar)

        viewModel = (activity as NewsActivity).mViewModel

        setupRecyclerView()

        newsAdapter.setOnItemClickListener { article ->
           /* take the article put it into a bundle and attach the bundle to the navigation component
            so that the navigation component will handle the transition for us and pass the arguments to our Article fragment*/
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
          when (response) {
              is Resource.Success -> {
                  hideProgressBar()
                  response.data?.let { newsResponse ->
                      //list differ can't work with mutable list normally. need to convert .toList()
                      newsAdapter.differ.submitList(newsResponse.articles.toList())
                      val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                      isLastPage = viewModel.breakingNewsPage == totalPages
                      if (isLastPage) {
                          rvBreakingNews.setPadding(0,0,0,0)
                      }
                  }
              }

              is Resource.Error -> {
                  hideProgressBar()
                  response.message?.let { message ->
                      Toast.makeText(context, "An error occured: $message", Toast.LENGTH_SHORT)
                  }
              }
              is Resource.Loading -> showProgressBar()
          }

        })
    }

    var isLoading = false
    //to determine if we should stop paginating
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //check if we are currently scrolling
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                //it means we are currently scrolling
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            //get layout manager of the recyclerview
            val layoutManager = rvBreakingNews.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            //now we can check if we scroll until bottom of the recycler view
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            //if we already scrolled a little bit down
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            //is total more than visible, if we have at least as many item in our recyclerview than our query page size (20)
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            //we can now determine if we should paginate or not
            val shouldPaginateOrNot = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginateOrNot){
                viewModel.getBreakingNews("us")
                isScrolling = false
            }
        }
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

}