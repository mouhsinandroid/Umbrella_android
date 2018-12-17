package org.secfirst.umbrella.whitelabel.feature.checklist.view.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.checklist_dashboard.*
import kotlinx.android.synthetic.main.checklist_dashboard.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Dashboard
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.DaggerChecklistComponent
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.presenter.ChecklistBasePresenter
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter.DashboardAdapter
import org.secfirst.umbrella.whitelabel.misc.initRecyclerView
import javax.inject.Inject

class DashboardController(bundle: Bundle) : BaseController(bundle), ChecklistView {
    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>
    private val dashboardItemClick: (Checklist?) -> Unit = this::onDashboardItemClicked
    private val isCustomBoard by lazy { args.getBoolean(EXTRA_IS_CUSTOM_BOARD) }

    constructor(isCustomBoard: Boolean) : this(Bundle().apply {
        putBoolean(EXTRA_IS_CUSTOM_BOARD, isCustomBoard)
    })

    override fun onInject() {
        DaggerChecklistComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)

    }

    override fun onAttach(view: View) {
        presenter.onAttach(this)
        checkWorkflow()
    }

    private fun checkWorkflow() {
        if (isCustomBoard) {
            addNewChecklist?.visibility = View.VISIBLE
            presenter.submitLoadCustomDashboard()
        } else
            presenter.submitLoadDashboard()
    }


    private fun onDashboardItemClicked(checklist: Checklist?) {
        if (checklist != null)
            parentController?.router?.pushController(RouterTransaction.with(ChecklistDetailController(checklist)))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.checklist_dashboard, container, false)
        view.addNewChecklist.setOnClickListener { addNewChecklist() }
        return view
    }

    private fun addNewChecklist() {
        parentController?.router?.pushController(RouterTransaction
                .with(ChecklistCustomController(System.currentTimeMillis().toString())))
    }

    override fun showDashboard(dashboards: List<Dashboard.Item>) {
        val dashboardAdapter = DashboardAdapter(dashboards, dashboardItemClick)
        checklistDashboardRecyclerView?.initRecyclerView(dashboardAdapter)
    }

    companion object {
        const val EXTRA_IS_CUSTOM_BOARD = "custom_board"
    }
}