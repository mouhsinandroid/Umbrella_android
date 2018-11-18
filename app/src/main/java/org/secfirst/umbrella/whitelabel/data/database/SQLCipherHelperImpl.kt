package org.secfirst.umbrella.whitelabel.data.database

import com.raizlabs.android.dbflow.config.DatabaseDefinition
import com.raizlabs.android.dbflow.sqlcipher.SQLCipherOpenHelper
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener


class SQLCipherHelperImpl(
        databaseDefinition: DatabaseDefinition,
        databaseHelperListener: DatabaseHelperListener?,
        private val userToken: String? = null) : SQLCipherOpenHelper(databaseDefinition, databaseHelperListener) {

    override fun getCipherSecret(): String {
        return userToken ?: "1"
    }
}
