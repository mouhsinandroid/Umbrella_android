package org.secfirst.umbrella.whitelabel.feature.tour.presenter

import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.tour.interactor.TourBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject


class TourPresenterImp<V : TourView, I : TourBaseInteractor>
@Inject internal constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), TourBasePresenter<V, I> {

    override fun manageContent() {
        var isFetchData: Boolean
        launchSilent(uiContext) {
            interactor?.let {
                it.initDatabase(AppDatabase.DEFAULT)
                getView()?.downloadContentInProgress()
                isFetchData = it.fetchData()

                if (isFetchData) {
                    val root = it.initParser()
                    it.persist(root)
                }
                it.persistFeedSource(createFeedSources())
                getView()?.downloadContentCompleted(isFetchData)
            }
        }
    }

    private fun createFeedSources(): List<FeedSource> {
        val feedSources = mutableListOf<FeedSource>()
        val feedSource1 = FeedSource("ReliefWeb", false, 0)
        val feedSource2 = FeedSource("UN", false, 1)
        val feedSource3 = FeedSource("FCO", false, 2)
        val feedSource4 = FeedSource("CDC", false, 3)
        val feedSource5 = FeedSource("Global Disaster Alert\nCoordination System", false, 4)
        val feedSource6 = FeedSource("US State Department Country\nWarnings", false, 5)
        feedSources.add(feedSource1)
        feedSources.add(feedSource2)
        feedSources.add(feedSource3)
        feedSources.add(feedSource4)
        feedSources.add(feedSource5)
        feedSources.add(feedSource6)
        return feedSources
    }
}