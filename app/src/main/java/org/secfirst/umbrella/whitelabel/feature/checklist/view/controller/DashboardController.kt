package org.secfirst.umbrella.whitelabel.feature.checklist.view.controller

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.alert_control.view.*
import kotlinx.android.synthetic.main.checklist_custom_dialog.view.*
import kotlinx.android.synthetic.main.checklist_dashboard.*
import kotlinx.android.synthetic.main.checklist_dashboard.view.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.main_view.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.component.SwipeToDeleteCallback
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Dashboard
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.DaggerChecklistComponent
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.presenter.ChecklistBasePresenter
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter.DashboardAdapter
import org.secfirst.umbrella.whitelabel.feature.segment.view.controller.HostSegmentController
import org.secfirst.umbrella.whitelabel.misc.initRecyclerView
import javax.inject.Inject

class DashboardController(bundle: Bundle) : BaseController(bundle), ChecklistView {


    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>
    private val dashboardItemClick: (Checklist) -> Unit = this::onDashboardItemClicked
    private val isCustomBoard by lazy { args.getBoolean(EXTRA_IS_CUSTOM_BOARD) }
    private lateinit var adapter: DashboardAdapter
    private lateinit var customChecklistDialog: android.app.AlertDialog
    private lateinit var customChecklistView: View

    constructor(isCustomBoard: Boolean) : this(Bundle().apply {
        putBoolean(EXTRA_IS_CUSTOM_BOARD, isCustomBoard)
    })

    override fun onInject() {
        DaggerChecklistComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.checklist_dashboard, container, false)
        customChecklistView = inflater.inflate(R.layout.checklist_custom_dialog, container, false)
        customChecklistDialog = android.app.AlertDialog
                .Builder(activity)
                .setView(customChecklistView)
                .create()

        customChecklistView.alertControlOk.onClick { startCustomChecklist() }
        customChecklistView.alertControlCancel.onClick { customChecklistDialog.dismiss() }
        view.addNewChecklistBtn.setOnClickListener { showCustomChecklistDialog() }
        presenter.onAttach(this)
        return view
    }

    override fun onAttach(view: View) {
        checkWorkflow()
    }

    private fun onDeleteChecklist(checklist: Checklist) {
        presenter.submitDeleteChecklist(checklist)
    }

    private fun showCustomChecklistDialog() {
        val dialogManager = DialogManager(this)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return customChecklistDialog
            }
        })
        customChecklistDialog.show()
    }

    private fun initOnDeleteChecklist() {
        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val checklist = adapter.getChecklist(position)
                if (!checklist!!.custom) {
                    resetChecklist(checklist)
                } else onDeleteChecklist(checklist)
                adapter.removeAt(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(checklistDashboardRecyclerView)
    }

    private fun checkWorkflow() {
        if (isCustomBoard) {
            addNewChecklistBtn?.show()
            presenter.submitLoadCustomDashboard()
        } else
            presenter.submitLoadDashboard()
            initOnDeleteChecklist()
    }

    private fun onDashboardItemClicked(checklist: Checklist) {
        if (checklist.custom)
            parentController?.router?.pushController(RouterTransaction.with(ChecklistController(checklist.id)))
        else {
            parentController?.router?.pushController(RouterTransaction.with(HostSegmentController(arrayListOf(checklist.difficulty!!.id), true, isFromDashboard = true)))
            mainActivity.navigation.menu.getItem(3).isChecked = true
        }
    }

    private fun resetChecklist(checklist: Checklist) {
        checklist.content.forEach {
            it.value = false
        }
        checklist.progress = 0
        presenter.submitUpdateChecklist(checklist)
    }

    private fun startCustomChecklist() {
        val customName = customChecklistView.editChecklistItem.text.toString()
        if (customName.isNotBlank())
            parentController?.router?.pushController(RouterTransaction
                    .with(ChecklistCustomController(System.currentTimeMillis().toString(), customName)))
        else
            customChecklistView.editChecklistItem.error = context.getString(R.string.invalid_name_custom_checklist_messenge)
    }

    override fun showDashboard(dashboards: MutableList<Dashboard.Item>) {
        if (dashboards.isEmpty() && isCustomBoard) {
            emptyTitleView?.text = context.getText(R.string.empty_custom_checklist_message)
            addNewChecklistBtn?.show()
        } else if (dashboards.isEmpty() && !isCustomBoard) {
            emptyTitleView?.text = context.getText(R.string.empty_checklist_message)
        } else {
            customChecklistContainer?.visibility = View.VISIBLE
            emptyDashboardView?.visibility = View.GONE
            adapter = DashboardAdapter(dashboards, dashboardItemClick)
            checklistDashboardRecyclerView?.initRecyclerView(adapter)
        }
    }

    companion object {
        const val EXTRA_IS_CUSTOM_BOARD = "custom_board"
    }
}