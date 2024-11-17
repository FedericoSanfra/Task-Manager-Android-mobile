package com.teamify

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.ViewModel


import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.teamify.ui.theme.AppTheme
//import com.teamify.ui.theme.getOnSecondaryLight

import kotlinx.coroutines.launch


class InputSearchViewModel(val model: MyModel) : ViewModel() {
    val inputField = model.inputSearch
    fun setInput(s: String) = model.setS(s)

    val teamsList = model.teamsList

    val personList = model.personList

    val recentlyClickedTeams = model.recentClickedTeams
    fun setNewRecentTeam( team: Team)= model.newRecentTeam(team)
    fun reorderRecentTeam( team: Team)= model.reorderRecentTeam( team)

    var recentTeamsExpanded=model.recentTeamsExpanded

    fun setRecentTeamsExpanded()=model.setListExpansion()
}

class Factory(context: Context) : ViewModelProvider.Factory {
    // if the class of the viewModel im creating is THAT im assigning it
    val model: MyModel = (context.applicationContext as? MyApplication)?.model
        ?: throw IllegalArgumentException("Bad applcation class")

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(InputSearchViewModel::class.java))
            InputSearchViewModel(model) as T
        // else if (modelClass.isAssignableFrom(CountViewModel::class.java))
        //   CountViewModel(model) as T
        else throw java.lang.IllegalArgumentException("Unknown ViewModel class")//i dont have nothing else of view model
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SideBar(
    inputString: String,
    setInput: (String) -> Unit,
    teamsList: List<Team>,
    personList: List<Person>,
    actions: Actions,
    recentlyClickedTeams: List<Team>,
    newRecentTeam: (team: Team)->Unit,
    reorderRecentTeam: (team: Team)->Unit,
    recentListExpanded: Boolean,
    setRecentTeamExpanded: ()->Unit
) {




    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val itemHeight = if (isPortrait) 60.dp else 20.dp
    var recentMenuHeight=itemHeight*recentlyClickedTeams.size

    val filteredList: State<List<Team>> = remember(inputString, teamsList) {
        derivedStateOf {
            val nameList = personList.map { it.firstName + it.lastName }
            teamsList.filter { team ->
                team.name.lowercase().contains(inputString.lowercase()) ||
                        team.category.lowercase().contains(inputString.lowercase()) ||
                        team.roles.keys.any { id ->
                            nameList.getOrElse((id - 1).toInt()) { "" }.lowercase()
                                .contains(inputString.lowercase())
                        }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                SearchBar(
                    inputField = {
                        InputField(
                            query = inputString,
                            onQueryChange = { s -> setInput(s) },
                            onSearch = {/* */ },
                            expanded = true,
                            placeholder = { Text(text="Search teams") },
                            onExpandedChange = {/**/ },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { setInput("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear"
                                    )
                                }
                            }
                        )
                    }, expanded = true, onExpandedChange = {/**/ }

                ) {//RECENT CLICKED TEAMS
                    Row(modifier=Modifier.fillMaxWidth()){
                        IconButton(onClick = { setRecentTeamExpanded() }) {
                            if(recentListExpanded){
                                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Expanded recent list")
                            }else{
                                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Not Expanded recent list")
                            }
                        }
                        Text(text="Recent teams", modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp), style=MaterialTheme.typography.titleMedium)
                    }
                    AnimatedVisibility(recentListExpanded && recentlyClickedTeams.size > 0){
                    LazyColumn(modifier = Modifier
                        //.background() choose color
                        .height(recentMenuHeight)) {
                        items(recentlyClickedTeams.reversed()) { team ->
                            NavigationDrawerItem(
                                label = {
                                    var expanded by remember { mutableStateOf(false) }
                                  /*  DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier
                                            .fillMaxWidth(0.2f)
                                            .background(onSecondaryLight),

                                        ) {
                                        DropdownMenuItem(text = { Text("Send invitation") },

                                            onClick = {
                                                expanded = false
                                                //TODO MANAGE DEEP LINKING
                                            })
                                    }*/
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Text(text = team.name, style= MaterialTheme.typography.titleMedium)
                                   /* IconButton(onClick = { /*expanded= true*/ }) {
                                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Send invitation link")
                                    }*/

                                }

                                }, // You can customize the label here
                                selected = false,
                                icon = { ProfilePictureIcon( team.teamImage, 32.dp, true) },
                                onClick = {
                                    //gestisco recent teams clicked

                                    actions.showTeam(team.id)
                                   reorderRecentTeam(team)
                                    scope.launch {
                                        drawerState.apply {
                                            close()
                                        }
                                    }

                                   }
                            )
                        }

                    }}
                        Spacer(modifier = Modifier.padding(16.dp))
                        HorizontalDivider(thickness = 2.dp)
                        LazyColumn(modifier = Modifier.fillMaxSize()) {

                            items(filteredList.value) { team ->
                                NavigationDrawerItem(
                                    label = {
                                        var expanded by remember { mutableStateOf(false) }
                                       /* DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false },
                                            modifier = Modifier
                                                .fillMaxWidth(0.2f)
                                                .background(MaterialTheme.colorScheme.onSecondary),

                                            ) {
                                            DropdownMenuItem(text = { Text("Send invitation") },

                                                onClick = {
                                                    expanded = false
                                                    //TODO MANAGE DEEP LINKING
                                                })
                                        }*/

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Text(
                                                text = team.name,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            IconButton(onClick = { expanded = true }) {
                                                Icon(
                                                    imageVector = Icons.Default.MoreVert,
                                                    contentDescription = "Send invitation link"
                                                )
                                            }


                                        }

                                    }, // You can customize the label here
                                    selected = false,
                                    icon = {
                                        ProfilePictureIcon( team.teamImage, 32.dp, true)
                                    },
                                    onClick = {
                                        actions.showTeam(team.id)
                                        val teamFound=recentlyClickedTeams.firstOrNull{it.id== team.id}
                                        if(teamFound!=null){
                                            reorderRecentTeam(team)
                                        }else{
                                            newRecentTeam( team)
                                        }
                                        scope.launch {
                                            drawerState.apply {
                                                close()
                                            }
                                        }



                                        /* Handle item click here */
                                    }
                                )

                            }


                        }

                }

                // ...other drawer items
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
        ) {
            // Screen content

            TopAppBar(
                title = {

                    Text(text = "Home")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarColors(
                    titleContentColor = Color.Black,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black,
                    containerColor = Color.White
                )
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Bottom
            ) {
                CreateTeamScreen()
            }


        }
    }

}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ColumnLayout(actions: Actions, vm: InputSearchViewModel = viewModel(factory = Factory(LocalContext.current.applicationContext))) {
    val inputField by vm.inputField.collectAsState()
    val recentlyClickedTeams by vm.recentlyClickedTeams.collectAsState()
    val recentTeamExpanded by vm.recentTeamsExpanded.collectAsState()

    val teamsList by vm.teamsList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        SideBar(inputField, vm::setInput, teamsList, vm.personList,actions,recentlyClickedTeams, vm::setNewRecentTeam, vm::reorderRecentTeam, recentTeamExpanded, vm::setRecentTeamsExpanded)

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RowLayout(actions: Actions, vm: InputSearchViewModel = viewModel(factory = Factory(LocalContext.current.applicationContext))) {
    val inputField by vm.inputField.collectAsState()
    val recentlyClickedTeams by vm.recentlyClickedTeams.collectAsState()
    val recentTeamExpanded by vm.recentTeamsExpanded.collectAsState()
    val teamsList by vm.teamsList.collectAsState()
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        SideBar(inputField, vm::setInput, teamsList, vm.personList, actions, recentlyClickedTeams, vm::setNewRecentTeam, vm::reorderRecentTeam, recentTeamExpanded, vm::setRecentTeamsExpanded)

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(actions: Actions) {

    AppTheme {


        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            if (this.maxHeight > this.maxWidth)
                ColumnLayout(actions)
            else
                RowLayout(actions)
        }
    }
}