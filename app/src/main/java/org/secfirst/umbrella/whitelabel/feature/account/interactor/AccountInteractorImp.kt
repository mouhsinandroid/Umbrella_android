package org.secfirst.umbrella.whitelabel.feature.account.interactor

import org.secfirst.umbrella.whitelabel.data.database.account.AccountRepo
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.geolocation.GeolocationService
import org.secfirst.umbrella.whitelabel.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject


class AccountInteractorImp @Inject constructor(preferenceHelper: AppPreferenceHelper,
                                               private val accountRepo: AccountRepo,
                                               private val geolocationService: GeolocationService)
    : BaseInteractorImp(preferenceHelper), AccountBaseInteractor {

    override fun setLoggedIn() = preferenceHelper.setIsLoggedIn(true)

    override suspend fun accessDatabase(userToken: String) = accountRepo.loginDatabase(userToken)

    override suspend fun changeDatabaseAccess(userToken: String) = accountRepo.changeToken(userToken)

    override suspend fun insertFeedLocation(feedLocation: FeedLocation) = accountRepo.saveFeedLocation(feedLocation)

    override suspend fun insertAllFeedSources(feedSources: List<FeedSource>) = accountRepo.saveAllFeedSources(feedSources)

    override suspend fun fetchFeedSources() = accountRepo.getAllFeedSources()

    override suspend fun fetchFeedLocation() = accountRepo.getFeedLocation()

    override suspend fun fetchGeolocation(nameLocation: String) = geolocationService.retrieveAddress(nameLocation)


}