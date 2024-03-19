package com.atharok.barcodescanner.presentation.views.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.serializable
import com.atharok.barcodescanner.common.utils.CUSTOM_SEARCH_URL_BARCODE_KEY_WORD
import com.atharok.barcodescanner.common.utils.CUSTOM_URL_KEY
import com.atharok.barcodescanner.databinding.ActivityCustomSearchUrlListBinding
import com.atharok.barcodescanner.domain.entity.customUrl.CustomUrl
import com.atharok.barcodescanner.presentation.customView.CustomItemTouchHelperCallback
import com.atharok.barcodescanner.presentation.customView.MarginItemDecoration
import com.atharok.barcodescanner.presentation.intent.createStartActivityIntent
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseCustomUrlViewModel
import com.atharok.barcodescanner.presentation.views.activities.CustomSearchUrlCreatorActivity.Companion.RESULT_CODE_INSERT
import com.atharok.barcodescanner.presentation.views.activities.CustomSearchUrlCreatorActivity.Companion.RESULT_CODE_UPDATE
import com.atharok.barcodescanner.presentation.views.recyclerView.customUrl.CustomUrlItemAdapter
import com.atharok.barcodescanner.presentation.views.recyclerView.customUrl.CustomUrlItemTouchHelperListener
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class CustomSearchUrlListActivity : BaseActivity(), CustomUrlItemAdapter.OnCustomUrlItemListener, CustomUrlItemTouchHelperListener {

    private val viewBinding: ActivityCustomSearchUrlListBinding by lazy {
        ActivityCustomSearchUrlListBinding.inflate(layoutInflater)
    }

    private val databaseCustomUrlViewModel by viewModel<DatabaseCustomUrlViewModel>()

    private val adapter: CustomUrlItemAdapter = CustomUrlItemAdapter(this)
    private val customUrlItemSelected by lazy { mutableListOf<CustomUrl>() }
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.activityCustomSearchUrlListEmptyTextView.visibility = View.GONE
        viewBinding.activityCustomSearchUrlListRecyclerView.visibility = View.GONE

        setSupportActionBar(viewBinding.activityCustomSearchUrlListActivityLayout.toolbar)

        configureRecyclerView()
        observeDatabase()

        setContentView(viewBinding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.dismiss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_custom_search_url_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_activity_custom_search_url_list_delete_item -> {
                if(customUrlItemSelected.isEmpty())
                    showDeleteAllConfirmationDialog()
                else
                    showDeleteSelectedItemsConfirmationDialog()
            }
            R.id.menu_activity_custom_search_url_list_add_item -> {
                openCustomSearchUrlCreatorActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureRecyclerView() {
        val recyclerView = viewBinding.activityCustomSearchUrlListRecyclerView

        val layoutManager = LinearLayoutManager(this)
        val decoration = MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.standard_margin))

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(decoration)

        val itemTouchHelper = ItemTouchHelper(
            CustomItemTouchHelperCallback(this, 0, ItemTouchHelper.START)
        )
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun observeDatabase() {
        databaseCustomUrlViewModel.customUrlList.observe(this) {
            customUrlItemSelected.clear()
            adapter.updateData(it)

            if (it.isEmpty()) {
                viewBinding.activityCustomSearchUrlListEmptyTextView.visibility = View.VISIBLE
                viewBinding.activityCustomSearchUrlListRecyclerView.visibility = View.GONE
            } else {
                viewBinding.activityCustomSearchUrlListEmptyTextView.visibility = View.GONE
                viewBinding.activityCustomSearchUrlListRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    // ---- Item Actions ----

    override fun onItemClick(view: View?, customUrl: CustomUrl) {
        val intent = createStartActivityIntent(this, CustomSearchUrlCreatorActivity::class)
        intent.putExtra(CUSTOM_SEARCH_URL_BARCODE_KEY_WORD, customUrl)
        resultLauncher.launch(intent)
    }

    override fun onItemSelect(view: View?, customUrl: CustomUrl, isSelected: Boolean) {
        if(isSelected){
            customUrlItemSelected.add(customUrl)
        }else{
            customUrlItemSelected.remove(customUrl)
        }
    }

    override fun isSelectedMode(): Boolean = customUrlItemSelected.isNotEmpty()

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        val customUrl: CustomUrl = adapter.getCustomUrl(position)
        databaseCustomUrlViewModel.deleteCustomUrl(customUrl)
        Snackbar.make(viewBinding.root, getString(R.string.custom_url_deleted), Snackbar.LENGTH_SHORT).show()
    }

    // ---- Delete custom URL From Menu ----

    private fun showDeleteAllConfirmationDialog() {
        showDeleteConfirmationDialog(R.string.popup_message_confirmation_deleted_all_custom_urls) {
            databaseCustomUrlViewModel.deleteAll()
        }
    }

    private fun showDeleteSelectedItemsConfirmationDialog() {
        showDeleteConfirmationDialog(R.string.popup_message_confirmation_delete_selected_items_history) {
            databaseCustomUrlViewModel.deleteCustomUrls(customUrlItemSelected)
        }
    }

    private inline fun showDeleteConfirmationDialog(messageRes: Int, crossinline positiveAction: () -> Unit) {
        alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.delete_label)
            .setMessage(messageRes)
            .setPositiveButton(R.string.delete_label) { _, _ ->
                positiveAction()
            }
            .setNegativeButton(R.string.cancel_label, null)
            .show()
    }

    // ---- CustomSearchUrlCreatorActivity ----

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when(result.resultCode) {
            RESULT_CODE_INSERT -> {
                result.data?.serializable(CUSTOM_URL_KEY, CustomUrl::class.java)?.let { customUrl: CustomUrl ->
                    saveIntoDatabase(customUrl)
                }
            }
            RESULT_CODE_UPDATE -> {
                result.data?.serializable(CUSTOM_URL_KEY, CustomUrl::class.java)?.let { customUrl: CustomUrl ->
                    updateIntoDatabase(customUrl)
                }
            }
        }
    }

    private fun openCustomSearchUrlCreatorActivity() {
        val intent = createStartActivityIntent(this, CustomSearchUrlCreatorActivity::class)
        resultLauncher.launch(intent)
    }

    private fun saveIntoDatabase(customUrl: CustomUrl) {
        databaseCustomUrlViewModel.insertCustomUrl(customUrl)
        Snackbar.make(viewBinding.root, getString(R.string.custom_url_added), Snackbar.LENGTH_SHORT).show()
    }

    private fun updateIntoDatabase(customUrl: CustomUrl) {
        databaseCustomUrlViewModel.updateCustomUrl(customUrl)
        Snackbar.make(viewBinding.root, getString(R.string.custom_url_updated), Snackbar.LENGTH_SHORT).show()
    }
}