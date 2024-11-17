package com.teamify

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class TeamDetailsVMFactory (context : Context, val teamId : Long?) : ViewModelProvider.Factory {
    private val model: MyModel = (context.applicationContext as? MyApplication)?.model ?: throw IllegalArgumentException("Bad application context")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TeamDetailsVM::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                TeamDetailsVM(model, teamId) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

class InviteViewModelFactory(context: Context, val teamId : Long?) : ViewModelProvider.Factory {
    private val model: MyModel = (context.applicationContext as? MyApplication)?.model ?: throw IllegalArgumentException("Bad application context")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InviteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InviteViewModel(model, teamId) as T
        }
        else throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ProfileViewModelFactory(context: Context, val personId : Long?) : ViewModelProvider.Factory {
    private val model: MyModel = (context.applicationContext as? MyApplication)?.model ?: throw IllegalArgumentException("Bad application context")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(model, personId) as T
        }
        else throw IllegalArgumentException("Unknown ViewModel class")
    }
}