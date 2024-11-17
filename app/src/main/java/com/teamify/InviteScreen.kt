package com.teamify

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


@Composable
fun InviteScreen(
    teamId : Long?,
    role : Role,
    navController: NavController,
    vm : InviteViewModel = viewModel( factory = InviteViewModelFactory(LocalContext.current, teamId = teamId) )
) {

    var invitationResult by remember { mutableStateOf<AddingMemberErrorType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        when(invitationResult){
            AddingMemberErrorType.SUCCESS -> {
                Text("You have successfully joined the team!",
                    style = MaterialTheme.typography.headlineLarge
                )
                Button(
                    onClick = { navController.navigate("showTeam/$teamId") },
                    modifier = Modifier.padding(top = 32.dp),
                ){
                    Text("Go to the group")
                }
            }
            AddingMemberErrorType.USER_NOT_FOUND -> {
                Text("You are not registered in the system!",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            AddingMemberErrorType.USER_ALREADY_IN_TEAM -> {
                Text("You are already in the team!",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            else -> {
                Text("You have been invited to join the team!",
                    style = MaterialTheme.typography.headlineLarge
                )
                Button(
                    onClick = {
                    invitationResult = vm.acceptInvitation(teamId!!, role)
                    },
                    modifier = Modifier.padding(top = 32.dp)
                ) {
                    Text("Accept Invitation")
                }
            }
        }
    }
}

class InviteViewModel(private val myModel : MyModel, teamId: Long? ) : ViewModel(){

    //the viewModel gets the Person object of the logged user from the model
    private var loggedUser by mutableStateOf( myModel.getLoggedUser() )

    fun acceptInvitation(teamId: Long, role: Role) : AddingMemberErrorType{
        Log.d("DEBUGZ", "THE USER WHO ACCEPTED IS: $loggedUser")
        return myModel.addMemberToTeam(teamId, loggedUser.email, role)
    }

}


