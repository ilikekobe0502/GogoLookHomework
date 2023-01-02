package com.example.gogolookhomework.ui.search

import android.app.SearchManager
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.util.Consumer
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gogolookhomework.R
import com.example.gogolookhomework.contenproviders.SearchSuggestionProvider
import com.example.gogolookhomework.databinding.FragmentSearchBinding
import com.example.gogolookhomework.enums.DisplayType
import com.example.gogolookhomework.model.api.ApiResult
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by inject()
    private val listAdapter = ImageRecyclerViewAdapter()
    private lateinit var suggestionAdapter: SearchSuggestionAdapter
    private lateinit var searchView: SearchView

    private val linearLayoutManager by lazy {
        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private val gridLayoutManager by lazy {
        GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
    }

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            searchView = menu.findItem(R.id.action_search).actionView as SearchView
            setupSearchView()
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return true
        }
    }

    private val queryTextListener: OnQueryTextListener = object : OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            viewModel.searchImage(query ?: "")
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            val cursor: Cursor? = getRecentSuggestions(newText!!)
            suggestionAdapter.swapCursor(cursor)
            return false
        }
    }

    private val suggestionListener: SearchView.OnSuggestionListener = object :
        SearchView.OnSuggestionListener {
        override fun onSuggestionSelect(position: Int): Boolean {
            return false
        }

        override fun onSuggestionClick(position: Int): Boolean {
            val query = suggestionAdapter.getSuggestionText(position)
            searchView.setQuery(query, true)
            viewModel.searchImage(query ?: "")
            return true
        }
    }

    private val onNewIntentListener: Consumer<Intent> =
        Consumer<Intent> { t -> t?.let { handleIntent(it) } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserve()
    }

    override fun onDestroy() {
        requireActivity().removeOnNewIntentListener(onNewIntentListener)
        super.onDestroy()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initView() {
        initToolbar()
        initDisplayType()
        initRecyclerView()
        requireActivity().addOnNewIntentListener(onNewIntentListener)
    }

    private fun initToolbar() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(menuProvider)
    }

    private fun setupSearchView() {
        val searchManager =
            requireContext().getSystemService(AppCompatActivity.SEARCH_SERVICE) as SearchManager
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            suggestionAdapter = SearchSuggestionAdapter(requireContext(), null, 0)
            suggestionsAdapter = suggestionAdapter
            setOnQueryTextListener(queryTextListener)
            setOnSuggestionListener(suggestionListener)
        }
    }

    private fun initDisplayType() {
        updateDisplayType(DisplayType.getLocalDefaultDisplayType())
        binding.apply {
            ivGird.setOnClickListener {
                updateDisplayType(DisplayType.GRID)
                updateRecyclerAdapter()
            }

            ivList.setOnClickListener {
                updateDisplayType(DisplayType.LIST)
                updateRecyclerAdapter()
            }
        }
    }

    private fun updateDisplayType(displayType: DisplayType) {
        when (displayType) {
            DisplayType.LIST -> {
                binding.ivList.isSelected = true
                binding.ivGird.isSelected = false
            }
            DisplayType.GRID -> {
                binding.ivList.isSelected = false
                binding.ivGird.isSelected = true
            }
        }
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            val suggestions = SearchRecentSuggestions(
                requireContext(),
                SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE
            )
            suggestions.saveRecentQuery(query, null)
        }
    }


    private fun getRecentSuggestions(query: String): Cursor? {
        val uriBuilder = Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(SearchSuggestionProvider.AUTHORITY)
        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY)
        val selection = " ?"
        val selArgs = arrayOf(query)
        val uri = uriBuilder.build()
        return requireContext().contentResolver.query(uri, null, selection, selArgs, null)
    }

    private fun initRecyclerView() {
        updateRecyclerAdapter()
        binding.rvContent.adapter = listAdapter
    }

    private fun updateRecyclerAdapter() {
        binding.rvContent.layoutManager = if (binding.ivList.isSelected) {
            linearLayoutManager
        } else {
            gridLayoutManager
        }
    }

    private fun initObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResult.observe(viewLifecycleOwner) { response ->
                    when (response) {
                        is ApiResult.Error -> {
                            Snackbar.make(
                                binding.root,
                                response.throwable.message ?: "",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        is ApiResult.Loading -> {
                            binding.pbLoading.isVisible = true
                        }
                        is ApiResult.Success -> {
                            response.result?.let { list ->
                                binding.tvEmptyMessage.isVisible = list.isEmpty()
                                binding.pbLoading.isVisible = false
                                listAdapter.setData(list)
                            }
                        }
                    }
                }
                viewModel.defaultDisplayTypeResult.observe(viewLifecycleOwner) { response ->
                    when (response) {
                        is ApiResult.Error -> {
                            updateDisplayType(DisplayType.getLocalDefaultDisplayType())
                        }
                        is ApiResult.Loading -> {}
                        is ApiResult.Success -> {
                            response.result?.let { updateDisplayType(it) } ?: kotlin.run {
                                updateDisplayType(DisplayType.getLocalDefaultDisplayType())
                            }
                        }
                    }
                }
            }
        }
    }
}