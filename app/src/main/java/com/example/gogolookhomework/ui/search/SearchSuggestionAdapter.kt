package com.example.gogolookhomework.ui.search

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cursoradapter.widget.CursorAdapter
import com.example.gogolookhomework.databinding.ItemSearchHistoryBinding

class SearchSuggestionAdapter(context: Context?, c: Cursor?, flags: Int) :
    CursorAdapter(context, c, flags) {

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val binding = ItemSearchHistoryBinding.inflate(
            LayoutInflater.from(context),
            parent, false
        )
        val viewHolder = ViewHolder(binding)
        binding.root.tag = viewHolder
        return binding.root
    }

    override fun bindView(view: View, context: Context?, cursor: Cursor) {
        val viewHolder = view.tag as ViewHolder
        val item = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
        viewHolder.onBind(item)
    }

    fun getSuggestionText(position: Int): String? {
        if (position >= 0 && position < cursor.count) {
            val cursor = cursor
            cursor.moveToPosition(position)
            return cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
        }
        return null
    }

    private class ViewHolder(private val binding: ItemSearchHistoryBinding) {
        fun onBind(data: String) {
            binding.tvItem.text = data
        }
    }
}
