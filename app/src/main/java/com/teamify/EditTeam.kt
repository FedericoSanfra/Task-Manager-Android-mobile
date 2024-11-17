package com.teamify

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.teamify.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class EditTeamViewModel(val model: MyModel, teamId: Long) : ViewModel() {

    fun fetchTeamData(teamId: Long) = model.fetchTeamData(teamId)

    init {
        fetchTeamData(teamId)
    }

    private val _team = MutableStateFlow(model.getTeamById(teamId))
    val team: StateFlow<Team?> = _team.asStateFlow()

    val teamNameValue  = model.teamNameValue
    val teamNameError = model.teamNameError
    val teamDescriptionValue = model.teamDescriptionValue
    val teamCategoryValue = model.teamCategoryValue
    val teamCategoryError = model.teamCategoryError
    val teamImageFile = model.teamImageFile
    val teamRoles = model.teamRoles

    fun removeTeam(team: Team) = model.removeTeam(team)

    fun setTeamName(name: String) = model.setTeamName(name)
    fun setTeamDescription(description: String) = model.setTeamDescription(description)
    fun setTeamCategory(category: String) = model.setTeamCategory(category)
    fun setTeamImageFile(imageId: ImageBitmap) = model.setTeamImageFile(imageId)
    fun removeTeamMember(personId: Long) = model.removeTeamMember(personId)
    fun setRole(personId: Long, role: Role) = model.setRole(personId, role)
    fun saveEditTeam(teamId: Long) = model.saveEditTeam(teamId)
    fun cancelEditTeam(teamId: Long) = model.cancelEditTeam(teamId)
    fun getPersonById(personId: Long) = model.getPersonById(personId)


}

class Factory2(private val context: Context, private val teamId: Long) : ViewModelProvider.Factory {

    val model: MyModel = (context as? MyApplication)?.model ?: throw IllegalArgumentException("Bad application")

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(EditTeamViewModel::class.java))
            EditTeamViewModel(model, teamId) as T
        else throw IllegalArgumentException("Unknown ViewModel class")
    }
}



@Composable
fun EditTeamScreen(
    teamId: Long,
    actions: Actions,
    navController: NavController,
    vm: EditTeamViewModel = viewModel(factory = Factory2(LocalContext.current.applicationContext, teamId))
) {
    AppTheme {

    var selectedImageBitmap by remember {mutableStateOf<ImageBitmap?>(null)}

    val teamName by vm.teamNameValue.collectAsState()
    val teamNameError by vm.teamNameError.collectAsState()
    val teamDescription by vm.teamDescriptionValue.collectAsState()
    val teamCategory by vm.teamCategoryValue.collectAsState()
    val teamCategoryError by vm.teamCategoryError.collectAsState()
    val teamImageFile by vm.teamImageFile.collectAsState()
    val teamRoles by vm.teamRoles.collectAsState()

    var showImageOptionDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val coroutineScope = rememberCoroutineScope()
    var showImagePicker by remember { mutableStateOf(false) }

    val isSaveEnabled = teamNameError.isBlank() && teamCategoryError.isBlank()
    var showWarningDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current



    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Set the selected image URI
            val bitmap = uriToBitmap(context, uri)
            // Set the selected image bitmap
            selectedImageBitmap=bitmap
        }
    }
    selectedImageBitmap?.let { bitmap -> vm.setTeamImageFile(bitmap)}

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview(),
    ){ newImage ->
        if(newImage!=null) {
            //qui noi chiamiamo una funzione del viewmodel
            vm.setTeamImageFile(newImage.asImageBitmap())
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        }
    }


        BoxWithConstraints(modifier = Modifier.padding(16.dp)) {
            if (maxWidth < maxHeight) {
                EditTeamColumnLayout(
                    vm = vm,
                    teamName = teamName,
                    teamNameError = teamNameError,
                    teamDescription = teamDescription,
                    teamCategory = teamCategory,
                    teamCategoryError = teamCategoryError,
                    teamImageFile = teamImageFile,
                    teamRoles = teamRoles,
                    isSaveEnabled = isSaveEnabled,
                    onShowImagePicker = { showImagePicker = true },
                    onShowImageOptionDialog = { showImageOptionDialog = true },
                    onShowWarningDialog = { showWarningDialog = true },
                    onCancelEditTeam = {
                        navController.popBackStack("showTeam/{teamId}", inclusive = true)
                        actions.showTeam(teamId)
                        vm.cancelEditTeam(teamId)
                    }
                )
            } else {
                EditTeamRowLayout(
                    vm = vm,
                    teamName = teamName,
                    teamNameError = teamNameError,
                    teamDescription = teamDescription,
                    teamCategory = teamCategory,
                    teamCategoryError = teamCategoryError,
                    teamImageFile = teamImageFile,
                    teamRoles = teamRoles,
                    isSaveEnabled = isSaveEnabled,
                    onShowImagePicker = { showImagePicker = true },
                    onShowImageOptionDialog = { showImageOptionDialog = true },
                    onShowWarningDialog = { showWarningDialog = true },
                    onCancelEditTeam = {
                        navController.popBackStack("showTeam/{teamId}", inclusive = true)
                        actions.showTeam(teamId)
                        vm.cancelEditTeam(teamId)
                    }
                )
            }}

    if (showImageOptionDialog) {
        AlertDialog(
            onDismissRequest = { showImageOptionDialog = false },
            title = { Text(text = "Select Image Source") },
            text = {
                Column(modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = {
                        showImageOptionDialog = false
                        //showImagePicker = true
                        val permissionCheckResult =
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch()
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }


                    }) {
                        Text("Take picture")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        showImageOptionDialog = false
                        activityResultLauncher.launch("image/*")
                    }) {
                        Text("Choose from Gallery")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
//    if (showImageOptionDialog) {
//        AlertDialog(
//            onDismissRequest = { showImageOptionDialog = false },
//            title = { Text(text = "Select Image Source") },
//            text = {
//                Column(modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally) {
//                    Button(onClick = {
//                        showImageOptionDialog = false
//                        showImagePicker = true
//                    }) {
//                        Text("Pick from Image Picker")
//                    }
//                    Spacer(modifier = Modifier.height(8.dp))
//                    Button(onClick = {
//                        showImageOptionDialog = false
//                        launcher.launch("image/*")
//                    }) {
//                        Text("Pick from Gallery")
//                    }
//                }
//            },
//            confirmButton = {},
//            dismissButton = {}
//        )
//    }
/*
    if (showImagePicker) {
        ImagePickerDialog(
            model = MyModel(),
            onImageSelected = { imageResId ->
                vm.setTeamImageResId(imageResId)
                showImagePicker = false
            },
            onDismissRequest = { showImagePicker = false }
        )
    }*/

            if (showWarningDialog) {
                AlertDialog(
                    onDismissRequest = { showWarningDialog = false },
                    title = { Text(text = "Confirm Save") },
                    text = { Text(text = "Are you sure you want to save changes?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showWarningDialog = false
                            coroutineScope.launch {
                                vm.saveEditTeam(teamId)
                                Toast.makeText(
                                    context,
                                    "Team edited successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack("showTeam/{teamId}", inclusive = true)
                                actions.showTeam(teamId) // Navigate to the team details screen
                            }
                        }) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showWarningDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

        }}



@Composable
fun EditTeamColumnLayout(
    vm: EditTeamViewModel,
    teamName: String,
    teamNameError: String,
    teamDescription: String,
    teamCategory: String,
    teamCategoryError: String,
    teamImageFile: ImageBitmap?,
    teamRoles: Map<Long, Role>,
    isSaveEnabled: Boolean,
    onShowImagePicker: () -> Unit,
    onShowImageOptionDialog: () -> Unit,
    onShowWarningDialog: () -> Unit,
    onCancelEditTeam: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(32.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clickable { onShowImageOptionDialog() }
                ) {
                    ProfilePictureIcon(teamImageFile, 64.dp, false)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    OutlinedTextField(
                        value = teamName,
                        onValueChange = vm::setTeamName,
                        label = { Row {
                            Text("Team name")
                            Text(
                                text = "*",
                                color = MaterialTheme.colorScheme.error, // Red color
                                modifier = Modifier.padding(start = 4.dp) // Optional padding for better spacing
                            )
                        } },
                        isError = teamNameError.isNotBlank(),
                        trailingIcon = {
                            if (teamName.isNotEmpty()) {
                                Icon(
                                    modifier = Modifier.clickable { vm.setTeamName("") },
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Icon"
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )

                    if (teamNameError.isNotEmpty()) {
                        Text(
                            text = teamNameError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier,
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = teamDescription,
                    onValueChange = vm::setTeamDescription,
                    label = { Text("Team Description") },
                    trailingIcon = {
                        if (teamDescription.isNotEmpty()) {
                            Icon(
                                modifier = Modifier.clickable { vm.setTeamDescription("") },
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Icon"
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .width(414.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    value = teamCategory,
                    onValueChange = vm::setTeamCategory,
                    label = { Row {
                        Text("Team category")
                        Text(
                            text = "*",
                            color = MaterialTheme.colorScheme.error, // Red color
                            modifier = Modifier.padding(start = 4.dp) // Optional padding for better spacing
                        )
                    } },
                    isError = teamCategoryError.isNotBlank(),
                    trailingIcon = {
                        if (teamCategory.isNotEmpty()) {
                            Icon(
                                modifier = Modifier.clickable { vm.setTeamCategory("") },
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Icon"
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )

                if (teamCategoryError.isNotEmpty()) {
                    Text(
                        text = teamCategoryError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier,
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "Members",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                //.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                teamRoles.forEach { (personId, role) ->
                    val person = vm.getPersonById(personId)
                    if (person != null) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Chip(
                                fullName = "${person.firstName} ${person.lastName}",
                                email = person.email,
                                imageFile = person.imageFile,
                                onClose = { vm.removeTeamMember(person.id) },
                                selectedRole = role, // Default to "User" if no role is selected,
                                onRoleSelected = { newRole ->
                                    vm.setRole(
                                        person.id,
                                        newRole
                                    )
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Button(
                        onClick = onShowWarningDialog,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        enabled = isSaveEnabled
                    ) { Text(text = "Save") }
                    Button(
                        onClick = onCancelEditTeam,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) { Text(text = "Cancel") }
                }
            }
        }
    }
}



@Composable
fun EditTeamRowLayout(
    vm: EditTeamViewModel,
    teamName: String,
    teamNameError: String,
    teamDescription: String,
    teamCategory: String,
    teamCategoryError: String,
    teamImageFile: ImageBitmap?,
    teamRoles: Map<Long, Role>,
    isSaveEnabled: Boolean,
    onShowImagePicker: () -> Unit,
    onShowImageOptionDialog: () -> Unit,
    onShowWarningDialog: () -> Unit,
    onCancelEditTeam: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clickable { onShowImageOptionDialog() }
                ) {
                    ProfilePictureIcon(teamImageFile, 64.dp, false)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    OutlinedTextField(
                        value = teamName,
                        onValueChange = vm::setTeamName,
                        label = { Row {
                            Text("Team name")
                            Text(
                                text = "*",
                                color = MaterialTheme.colorScheme.error, // Red color
                                modifier = Modifier.padding(start = 4.dp) // Optional padding for better spacing
                            )
                        } },
                        isError = teamNameError.isNotBlank(),
                        trailingIcon = {
                            if (teamName.isNotEmpty()) {
                                Icon(
                                    modifier = Modifier.clickable { vm.setTeamName("") },
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Icon"
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )

                    if (teamNameError.isNotEmpty()) {
                        Text(
                            text = teamNameError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier,
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = teamDescription,
                    onValueChange = vm::setTeamDescription,
                    label = { Text("Team Description") },
                    trailingIcon = {
                        if (teamDescription.isNotEmpty()) {
                            Icon(
                                modifier = Modifier.clickable { vm.setTeamDescription("") },
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Icon"
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .width(414.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    value = teamCategory,
                    onValueChange = vm::setTeamCategory,
                    label = { Row {
                        Text("Team category")
                        Text(
                            text = "*",
                            color = MaterialTheme.colorScheme.error, // Red color
                            modifier = Modifier.padding(start = 4.dp) // Optional padding for better spacing
                        )
                    } },
                    isError = teamCategoryError.isNotBlank(),
                    trailingIcon = {
                        if (teamCategory.isNotEmpty()) {
                            Icon(
                                modifier = Modifier.clickable { vm.setTeamCategory("") },
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Icon"
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )

                if (teamCategoryError.isNotEmpty()) {
                    Text(
                        text = teamCategoryError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier,
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "Members",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        items(teamRoles.toList().chunked(2)) { chunk ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                chunk.forEach { (personId, role) ->
                    val person = vm.getPersonById(personId)
                    if (person != null) {
                        Chip(
                            fullName = "${person.firstName} ${person.lastName}",
                            email = person.email,
                            imageFile = person.imageFile,
                            onClose = { vm.removeTeamMember(person.id) },
                            selectedRole = role,
                            onRoleSelected = { newRole -> vm.setRole(person.id, newRole) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Button(
                        onClick = onShowWarningDialog,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        enabled = isSaveEnabled
                    ) { Text(text = "Save") }
                    Button(
                        onClick = onCancelEditTeam,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) { Text(text = "Cancel") }
                }
            }
        }
    }
}

@Composable
fun Chip(
    fullName: String,
    email: String,
    imageFile: ImageBitmap?,
    onClose: () -> Unit,
    roles: List<Role> = listOf(Role.Teacher, Role.Admin, Role.User), // Define roles here
    selectedRole: Role, // Currently selected role
    onRoleSelected: (Role) -> Unit // Callback when a role is selected
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), // Adjust padding for slimmer look
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfilePictureIcon(imageFile, size = 36.dp, true)
            Column(modifier = Modifier.padding(start = 6.dp, end = 6.dp)) {
                Text(text = fullName, style = MaterialTheme.typography.bodyLarge)
                Text(text = email, style = MaterialTheme.typography.bodyMedium)
            }

            Row {

                // Dropdown Menu
                Box {

                    Row(modifier = Modifier
                        .clickable { expanded = true }
                        .padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            text = selectedRole.toString(),
                            style = MaterialTheme.typography.bodyLarge,

                            )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Arrow DropDown"
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                text = {
                                    Row {
                                        Text(text = role.toString(), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(6.dp))
                                    }

                                },
                                onClick = {
                                    onRoleSelected(role)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(18.dp) // Adjust size of the close button for slimmer look
            ) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White)
            }
        }
    }
}


