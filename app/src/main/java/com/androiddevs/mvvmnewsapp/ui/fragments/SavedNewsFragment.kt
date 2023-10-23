package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapters.NewsAdapter
import com.androiddevs.mvvmnewsapp.ui.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment: Fragment(R.layout.fragment_saved_news) {


    private lateinit var viewModel: NewsViewModel

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var rvSavedNews: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as NewsActivity).mViewModel

        rvSavedNews = view.findViewById(R.id.rvSavedNews)

        setupNewsAdapter()

        newsAdapter.setOnItemClickListener { article ->
            /* take the article put it into a bundle and attach the bundle to the navigation component
             so that the navigation component will handle the transition for us and pass the arguments to our Article fragment*/
            val bundle = Bundle().apply {
                putSerializable("article", article)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        //anonymous class
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            //determine which directions we want to support to scroll/ drag the recycler view (up or down to scroll)
            //determine which directions we want to support to swipe on the recycler view items (left or right)
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true //true no functionality.
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //get the article (position) that was swiped
                val position = viewHolder.adapterPosition
                //get the article from the list
                val article = newsAdapter.differ.currentList[position]
                //delete the article
                viewModel.deleteArticle(article)
                //Snackbar with delete successful message & also action button if needed
                Snackbar.make(view, "Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        //undo the delete
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }


        ItemTouchHelper(itemTouchHelperCallback).apply {
            //attach the item touch helper to the recycler view
            attachToRecyclerView(rvSavedNews)
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer{ articles ->
            //update recycler view
            //list differ will automatically calculate the differences between new and old list and update accordingly
            newsAdapter.differ.submitList(articles)
        })

    }

    private fun setupNewsAdapter() {
        newsAdapter = NewsAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}