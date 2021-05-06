package uk.co.santander.onboarding

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

class OnboardingInitProvider: ContentProvider() {

    override fun onCreate(): Boolean {
        val ctx = context
        if (ctx is Application) {
            app = ctx
            appContext = ctx.applicationContext
        }
        return true
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        throw Exception("unimplemented")
    }

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? {
        throw Exception("unimplemented")
    }

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int {
        throw Exception("unimplemented")
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int {
        throw Exception("unimplemented")
    }

    override fun getType(p0: Uri): String? {
        throw Exception("unimplemented")
    }

    companion object {
        lateinit var app: Application
        lateinit var appContext: Context
    }
}