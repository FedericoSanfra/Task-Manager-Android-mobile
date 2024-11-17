package com.teamify


import android.content.ClipData
import android.net.Uri
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.teamify.ui.theme.AppTheme

import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


//USED ONLY TO SEE THE PREVIEW
@Preview
@Composable
fun TeamDetailsPanePreview() {
    // Create an instance of MyModel
    val myModel = MyModel()

    // Create an instance of TeamDetailsVM with a specific teamId
    val vm = TeamDetailsVM(myModel, teamId = 1L) // Replace 1L with the id of the team you want to preview

    // Pass the ViewModel to TeamDetailsPane
    TeamDetailsPane(
        teamId = 1L, // Replace 1L with the id of the team you want to preview
        navController = rememberNavController(),
        vm = vm
    )
}


@Composable
fun TeamDetailsPane(teamId : Long?,
                    navController : NavController,
                    vm: TeamDetailsVM = viewModel(factory = TeamDetailsVMFactory(LocalContext.current, teamId))
) {
    AppTheme {


    //if (teamId == null) display an error message
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        if (this.maxHeight > this.maxWidth)
            TeamDetailsColumnLayout(vm, navController)
        else
            TeamDetailsRowLayout(vm, navController)
    }}
}

@Composable
fun TeamDetailsRowLayout(
    vm: TeamDetailsVM,
    navController: NavController
){
    AppTheme {

    val pagerState = rememberPagerState(pageCount = {3})
    val coroutineScope = rememberCoroutineScope()

    Column (
        modifier = Modifier.fillMaxSize(),

    ) {
        TeamDetailsTopAppBar(vm, navController)

            TabRow(

                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color.Black,
                divider = {},
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        height = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    text = { Text("Task") }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = { Text("Details") }
                )
                Tab(
                    selected = pagerState.currentPage == 2,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    },
                    text = { Text("Achievements") }
                )
            }

            HorizontalPager(state = pagerState, userScrollEnabled = true) { page ->
                when (page) {
                    0 -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("TaskList")
                        }
                    }
                    1 -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            DetailsTab(vm = vm, navController = navController)
                        }
                    }
                    2 -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AchievementsGui(team = vm.team.value)
                        }
                    }
                }
            }
        //TeamDetailsBottomAppBar(navController)
    }
}
}

@Composable
fun DetailsTab( vm : TeamDetailsVM,
                navController: NavController){
        LazyColumn(modifier=Modifier.fillMaxSize()){
            item {
                Spacer(modifier = Modifier.height(16.dp))
                TeamDetailsDescriptionElement(vm.team.value.description)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item{
                TeamDetailsCategoryElement(vm.team.value.category)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TeamDetailsMembersList(
                    vm.team.value.id,
                    vm.teamMembers.value,
                    vm._newMemberEmail,
                    vm._newMemberEmailError,
                    vm::updateNewMemberEmail,
                    vm::checkNewMemberEmail,
                    vm.selectedRole,
                    vm::selectRole,
                    vm::deselectRole,
                    vm.isAddMembersBottomSheetExpanded,
                    vm::closeAddMembersBottomSheet,
                    vm::openAddMembersBottomSheet,
                    vm::addMemberToTeam,
                    navController
                )
            }

            item {
                TeamDetailsCreationDateElement(vm.team.value.creationDate)
                //so that the lazyColumn doesn't take the whole screen
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

}

@Composable
fun TeamDetailsColumnLayout(
    vm: TeamDetailsVM,
    navController: NavController
) {
    val pagerState = rememberPagerState(pageCount = {3})
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TeamDetailsTopAppBar(vm, navController)

        Column() {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color.Black,
                divider = {},
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        height = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    text = { Text("Task") }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    text = { Text("Details") }
                )
                Tab(
                    selected = pagerState.currentPage == 2,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    },
                    text = { Text("Achievements") }
                )
            }

            HorizontalPager(state = pagerState, userScrollEnabled = true) { page ->
                when (page) {
                    0 -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("TaskList")
                        }
                    }
                    1 -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            DetailsTab(vm = vm, navController = navController)
                        }
                    }
                    2 -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AchievementsGui(team = vm.team.value)
                        }
                    }
                }
            }
        }
        // TeamDetailsBottomAppBar(navController,actions)
    }
}

//todo: navigation through buttons
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailsTopAppBar(
    vm : TeamDetailsVM,
    navController: NavController
){
    val team by vm.team
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Team", style = MaterialTheme.typography.titleLarge) },
            text = { Text("Are you sure you want to delete this team? This action cannot be undone.", style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.removeTeam(team)
                        showDialog = false
                        Toast.makeText(context, "Team deleted successfully", Toast.LENGTH_SHORT).show()
                        navController.popBackStack() // Go back to the previous screen
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        Toast.makeText(context, "Deletion cancelled", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                team.let {
                    ProfilePictureIcon( it.teamImage, size = 42.dp, isView = true)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(it.name)
                }
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate("teamChat/${team.id}") }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = "Team Chat")
            }
            IconButton(
                onClick =
                { navController.navigate("editTeam/${team.id}") }
            ){
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Team Edit")
            }
            IconButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Team Delete")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    )

}
/*

//todo: navigation through buttons
@Composable
fun TeamDetailsBottomAppBar(
    navController: NavController,
    actions: Actions
){
    BottomAppBar(
        modifier = Modifier.height(48.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(onClick = { /* Handle home icon click here */ }) {
                Icon(painter = painterResource(id = R.drawable.home), contentDescription = "Home")
            }
            IconButton(onClick = { /* Handle chat icon click here */ }) {
                Icon(painter = painterResource(id = R.drawable.chat), contentDescription = "Chat")
            }
            IconButton(onClick = { /* Handle notifications icon click here */ }) {
                Icon(painter = painterResource(id = R.drawable.notifications), contentDescription = "Notifications")
            }
            IconButton(onClick = { /* Handle profile icon click here */ }) {
                Icon(painter = painterResource(id = R.drawable.account), contentDescription = "Profile")
            }
        }
    }
}*/

@Composable
fun TeamDetailsDescriptionElement(
    description : String
){
    Column (
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun TeamDetailsCategoryElement(
    category : String
){
    Column (
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Category",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun TeamDetailsMembersList(
    teamId: Long,
    teamMembers : Map<Person, Role>,
    //for the bottom sheet
    emailText : String,
    emailErrorText : String,
    updateNewMemberEmail : (String) -> Unit,
    checkNewMemberEmail : () -> Unit,
    selectedRole: Role?,
    selectRole : (Role) -> Unit,
    deselectRole : () -> Unit,
    expanded : Boolean,
    closeSheet : () -> Unit,
    //used by the add members button
    openAddMembersBottomSheet : () -> Unit,
    addMemberToTeam: (String, Role) -> AddingMemberErrorType,
    navController: NavController
){
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp),
    ){
        Text(
            text = "Members",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        TeamDetailsAddMembersButton(
            openAddMembersBottomSheet
        )

        if(expanded)
            TeamDetailsAddMemberBottomSheet(
                teamId,
                emailText,
                emailErrorText,
                updateNewMemberEmail,
                checkNewMemberEmail,
                selectedRole,
                selectRole,
                deselectRole,
                expanded,
                addMemberToTeam,
                closeSheet
            )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer) //todo: make this color lighter maybe?
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        teamMembers.forEach {
            TeamDetailsMembersItem(it.key, it.value, navController )
        }
    }
}

//todo: check the navigation of the clickable element
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TeamDetailsMembersItem(
    person : Person,
    role : Role,
    navController: NavController
){
    val selected = remember { mutableStateOf(false) }
    val scale = animateFloatAsState(if (selected.value) 2f else 1f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        selected.value = true
                    }

                    MotionEvent.ACTION_UP -> {
                        selected.value = false
                        navController.navigate("profileDetails/${person.id}")
                    }
                }
                true
            }
         //   .scale(scale.value),
                   ,verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.scale(scale.value),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            if(person.imageFile!=null){
                Image(bitmap = person.imageFile, contentDescription ="Image person file",  modifier = Modifier
                    .width(42.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer))
            }
            else{
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Default icon account",  modifier = Modifier
                    .width(42.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer))
            }

            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = person.firstName + " " + person.lastName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = role.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

        }

    }
}


@Composable
fun TeamDetailsAddMembersButton(
    openAddMembersBottomSheet : () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
    ){
        TextButton(
            onClick = openAddMembersBottomSheet
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add a new member",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Add a new member",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailsAddMemberBottomSheet(
    teamId : Long,
    emailText : String,
    emailErrorText : String,
    updateNewMemberEmail : (String) -> Unit,
    checkNewMemberEmail : () -> Unit,
    selectedRole: Role?,
    selectRole : (Role) -> Unit,
    deselectRole : () -> Unit,
    expanded : Boolean,
    addMemberToTeam : (String, Role) -> AddingMemberErrorType,
    closeSheet : () -> Unit
){
    val context = LocalContext.current                                      //used for the toast
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var dropDownExpanded by remember { mutableStateOf(false) }

    if(expanded){
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { closeSheet() },
        ){
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp, 16.dp, 16.dp, 35.dp)
            ) {
                item{
                    Text(
                        text = "Select the role of the new member and enter their email or send them an invitation link:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box{               //this box is used to make the dropdown appear on top of the button
                            TextButton(
                                onClick = { dropDownExpanded = true },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp)),
                            ) {
                                Text(
                                    text = selectedRole?.toString() ?: "Select a role",
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                            DropdownMenu(
                                expanded = dropDownExpanded,
                                onDismissRequest = { dropDownExpanded = false }
                            ) {
                                Role.entries.forEach { role ->
                                    DropdownMenuItem(
                                        onClick = {
                                            selectRole(role)
                                            dropDownExpanded = false
                                        },
                                        text = { Text(role.toString()) }
                                    )
                                }
                            }
                        }
                    }
                }
                item{
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ){
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            TextField(
                                value = emailText,
                                onValueChange = updateNewMemberEmail,
                                placeholder = { Text("johndoe@email.com") },
                                label = { if(emailErrorText.isBlank()) Text("Email") else Text(emailErrorText) },
                                isError = emailErrorText.isNotBlank(),
                            )
                        }
                        Column(
                            modifier = Modifier.weight(0.3f)
                        ){

                            Button(
                                onClick = {
                                    checkNewMemberEmail()
                                    if(emailErrorText.isNotBlank() || selectedRole == null){
                                        Toast.makeText(context, "You need to enter an email and select a role!", Toast.LENGTH_LONG).show()
                                    }
                                    else{
                                        val res = addMemberToTeam(emailText, selectedRole)
                                        when(res){
                                            AddingMemberErrorType.USER_NOT_FOUND -> {
                                                Toast.makeText(context, "User not found, try again.", Toast.LENGTH_LONG).show()
                                            }
                                            AddingMemberErrorType.USER_ALREADY_IN_TEAM -> {
                                                Toast.makeText(context, "User is already in the team", Toast.LENGTH_LONG).show()
                                            }
                                            AddingMemberErrorType.SUCCESS -> {
                                                closeSheet()
                                                updateNewMemberEmail("")
                                                deselectRole()
                                                Toast.makeText(context, "Member added successfully!", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                }
                            ){
                                Text("Invite")
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 50.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        DeepLinkButton(teamId = teamId , role = selectedRole)
                    }
                }
            }
        }
    }
}

@Composable
fun DeepLinkButton(
    teamId : Long,
    role : Role?
){
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    Button(
        onClick = {
            val deeplink = generateDeepLinkInvite(teamId, role!!)
            val clip = ClipEntry(ClipData.newPlainText("DeepLink", deeplink))
            clipboardManager.setClip(clip)

            Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
        },
        enabled = (role != null)
    ){
        Text("Create an invitation link")
    }
}

@Composable
fun TeamDetailsCreationDateElement(
    creationDate : Date
){
    Column (
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        Text(
            text = "Creation Date : ${formatter.format(creationDate)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

class TeamDetailsVM( private val myModel: MyModel, teamId : Long?) : ViewModel() {

    private val _team = mutableStateOf(myModel.getTeamById(teamId!!)!!)
    val team : State<Team> = _team

    private val _teamMembers = mutableStateOf(myModel.getTeamMembers(_team.value))
    val teamMembers: State<Map<Person, Role>> = _teamMembers

    var _newMemberEmail by mutableStateOf("")
        private set
    
    fun updateNewMemberEmail(email : String){
        _newMemberEmail = email
    }
    var _newMemberEmailError by mutableStateOf("")
        private set
    
    fun checkNewMemberEmail(){
        _newMemberEmailError = if(_newMemberEmail.isEmpty()){
            "Email cannot be empty"
        } else if(!_newMemberEmail.isEmailValid()){
            "Invalid email"
        } else{
            ""
        }
    }

    var selectedRole by mutableStateOf<Role?>(null)
        private set

    fun selectRole(role : Role){
        selectedRole = role
    }
    fun deselectRole(){
        selectedRole = null
    }

    var isAddMembersBottomSheetExpanded by mutableStateOf(false)
        private set

    fun openAddMembersBottomSheet(){
        isAddMembersBottomSheetExpanded = true
    }
    fun closeAddMembersBottomSheet(){
        isAddMembersBottomSheetExpanded = false
    }


    fun addMemberToTeam(email: String, role: Role) : AddingMemberErrorType{
        val result = myModel.addMemberToTeam(_team.value.id, email, role)
        _teamMembers.value = myModel.getTeamMembers(team.value)
        return result
    }

    fun removeTeam(team: Team) = myModel.removeTeam(team)

}

//used to display different Toasts to the user when adding a new member to the team
enum class AddingMemberErrorType {
    USER_NOT_FOUND,
    USER_ALREADY_IN_TEAM,
    SUCCESS
}


fun generateDeepLinkInvite(teamId : Long, role : Role) : String {
    val builder = Uri.Builder()
    builder.scheme("teamify")
        .authority("invite")
        .appendPath(teamId.toString())
        .appendPath(role.toString())
    return builder.build().toString()

}