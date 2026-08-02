package com.foxfocus.app

import android.app.Application
import androidx.room.Room
import com.foxfocus.app.auth.AuthRepository
import com.foxfocus.app.data.db.FoxDatabase
import com.foxfocus.app.data.firestore.UserProfileRepository
import com.foxfocus.app.data.repo.FoxRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FoxFocusApp : Application() {

  val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  val database: FoxDatabase by lazy {
    Room.databaseBuilder(this, FoxDatabase::class.java, "foxfocus.db")
      .fallbackToDestructiveMigration()
      .build()
  }

  val repository: FoxRepository by lazy { FoxRepository(database) }
  val authRepository: AuthRepository by lazy { AuthRepository() }
  val userProfileRepository: UserProfileRepository by lazy { UserProfileRepository() }

  override fun onCreate() {
    super.onCreate()
    applicationScope.launch { repository.ensurePlayerInitialized() }
  }
}
