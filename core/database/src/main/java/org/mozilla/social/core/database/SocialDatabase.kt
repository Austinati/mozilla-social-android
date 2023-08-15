package org.mozilla.social.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.mozilla.social.core.database.converters.AttachmentConverter
import org.mozilla.social.core.database.converters.EmojiConverter
import org.mozilla.social.core.database.converters.FieldConverter
import org.mozilla.social.core.database.converters.HashtagConverter
import org.mozilla.social.core.database.converters.HistoryConverter
import org.mozilla.social.core.database.converters.InstantConverter
import org.mozilla.social.core.database.converters.IntListConverter
import org.mozilla.social.core.database.converters.LocalDateConverter
import org.mozilla.social.core.database.converters.MentionConverter
import org.mozilla.social.core.database.converters.PollOptionConverter
import org.mozilla.social.core.database.dao.AccountsDao
import org.mozilla.social.core.database.dao.HashtagDao
import org.mozilla.social.core.database.dao.StatusDao
import org.mozilla.social.core.database.model.DatabaseAccount
import org.mozilla.social.core.database.model.DatabaseHashTag
import org.mozilla.social.core.database.model.DatabaseStatus

@Database(
    entities = [
        DatabaseStatus::class,
        DatabaseAccount::class,
        DatabaseHashTag::class,
    ],
    version = 1,
    autoMigrations = [

    ],
    exportSchema = true
)
@TypeConverters(
    InstantConverter::class,
    LocalDateConverter::class,
    AttachmentConverter::class,
    EmojiConverter::class,
    FieldConverter::class,
    HashtagConverter::class,
    MentionConverter::class,
    HistoryConverter::class,
    PollOptionConverter::class,
    IntListConverter::class,
)
abstract class SocialDatabase : RoomDatabase() {
    abstract fun statusDao(): StatusDao
    abstract fun accountsDao(): AccountsDao
    abstract fun hashtagDao(): HashtagDao
}