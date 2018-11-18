package org.secfirst.umbrella.whitelabel.data.database.content

import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.disk.Root
import javax.inject.Inject

class ContentRepository @Inject constructor(private val contentDao: ContentDao) : ContentRepo {


    override suspend fun initDatabase(userToken: String) = contentDao.initDatabase(userToken)

    override suspend fun insertFeedSource(feedSources: List<FeedSource>) = contentDao.insertFeedSource(feedSources)

    override suspend fun insertAllLessons(root: Root) = contentDao.insertAllLessons(root)

}