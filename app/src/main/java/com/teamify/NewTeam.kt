package com.teamify

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.StateFlow

import androidx.core.content.ContextCompat.checkSelfPermission
import com.teamify.ui.theme.AppTheme

fun uriToBitmap(context: Context, uri: Uri): ImageBitmap {
    // Use content resolver to open input stream from the URI
    val inputStream = context.contentResolver.openInputStream(uri)
    // Decode the input stream into a Bitmap
    val bitmap = BitmapFactory.decodeStream(inputStream)
    // Convert the Bitmap to ImageBitmap
    return bitmap.asImageBitmap()
}

class TeamViewModel(val model: MyModel) : ViewModel() {

    val isSheetOpen = model.isCreateBottomSheetOpen
    fun openSheet() = model.openCreateBottomSheet()
    fun closeSheet() = model.closeCreateBottomSheet()


    val teamsList = model.teamsList
    private val allPersons = model.persons

    val teamNameValue = model.teamNameValue
    val teamNameError = model.teamNameError
    val teamDescriptionValue = model.teamDescriptionValue
    val teamCategoryValue = model.teamCategoryValue
    val teamCategoryError = model.teamCategoryError
    val teamImageFile = model.teamImageFile
    val teamRoles = model.teamRoles

    private val _pendingMembers = MutableStateFlow(listOf<Pair<String, Role>>())
    val pendingMembers: StateFlow<List<Pair<String, Role>>> = _pendingMembers

    fun setTeamName(name: String) = model.setTeamName(name)
    fun setTeamDescription(description: String) = model.setTeamDescription(description)
    fun setTeamCategory(category: String) = model.setTeamCategory(category)
    fun setTeamImageFile(imageFile: ImageBitmap) = model.setTeamImageFile(imageFile)
    fun setTeamRoles(roles: Map<Long, Role>) = model.setTeamRoles(roles)
    fun removeTeamMember(personId: Long) = model.removeTeamMember(personId)
    fun setRole(personId: Long, role: Role) = model.setRole(personId, role)

    fun checkTeamName() = model.checkTeamName()
    fun checkTeamCategory() = model.checkTeamCategory()

    //fun createTeam() = model.createTeam()
    fun cancelNewTeam() {
        _pendingMembers.value = emptyList()
        model.cancelNewTeam()
    }

    fun getPersonById(personId: Long) = model.getPersonById(personId)


    // New properties from MyModel
    val newMemberEmail = model.newMemberEmail
    val newMemberEmailError = model.newMemberEmailError
    val selectedRole = model.selectedRole
    val isAddMembersBottomSheetExpanded = model.isAddMembersBottomSheetExpanded
    val teamMembers = model.teamMembers

    // Delegate methods to MyModel
    fun updateNewMemberEmail(email: String) {
        model.updateNewMemberEmail(email)
    }
    fun checkNewMemberEmail() {
        model.checkNewMemberEmail()
    }
    fun selectRole(role: Role) {
        model.selectRole(role)
    }
    fun deselectRole() {
        model.deselectRole()
    }
    fun addPendingMember(email: String, role: Role) {
        _pendingMembers.value += (email to role)
    }

    fun updatePendingMemberRole(email: String, newRole: Role) {
        _pendingMembers.value = _pendingMembers.value.map { if (it.first == email) email to newRole else it }
    }

    fun removePendingMember(email: String) {
        _pendingMembers.value = _pendingMembers.value.filter { it.first != email }
    }
    fun createTeam() {
        if (teamNameError.value.isBlank() && teamCategoryError.value.isBlank()) {
            val newTeam = model.createTeam(
                pendingMembers = _pendingMembers.value
            )

            if (newTeam != null) {
                model.resetFields()
                _pendingMembers.value = emptyList()
            }
        }
    }
}

class Factory3(context: Context) : ViewModelProvider.Factory {

    val model: MyModel = (context as? MyApplication)?.model ?: throw IllegalArgumentException("Bad application")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(TeamViewModel::class.java))
            TeamViewModel(model) as T
        else throw java.lang.IllegalArgumentException("Unknown ViewModel class")
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CreateTeamScreen(vm: TeamViewModel = viewModel(factory = Factory3(LocalContext.current.applicationContext))) {

    AppTheme {

    val isSheetOpen by vm.isSheetOpen.collectAsState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()
    var selectedImageBitmap by remember {mutableStateOf<ImageBitmap?>(null)}

    // Context
    val context = LocalContext.current


    val teamName by vm.teamNameValue.collectAsState()
    val teamNameError by vm.teamNameError.collectAsState()
    val teamDescription by vm.teamDescriptionValue.collectAsState()
    val teamCategory by vm.teamCategoryValue.collectAsState()
    val teamCategoryError by vm.teamCategoryError.collectAsState()
    val teamImageFile by vm.teamImageFile.collectAsState()


  //  var showImagePicker by remember { mutableStateOf(false) }

    val isSaveEnabled = teamName.isNotEmpty()&& teamCategory.isNotEmpty()
    var showWarningDialog by remember { mutableStateOf(false) }


    var showImageOptionDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    //val bitmap = remember { mutableStateOf<Bitmap?>(null) }

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

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        AddFloatingButton(
            onClick = { vm.openSheet()}
        )
    }

    if (isSheetOpen) {

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            vm.cancelNewTeam()
            vm.closeSheet()
            },
        modifier = Modifier.fillMaxSize(),

        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Take available horizontal space
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item{
                    Text("New Team", style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider(modifier = Modifier.padding(9.dp))
                }
                item{
                    Row(modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        //  .size(60.dp)
                                        //  .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                                        .clickable {  showImageOptionDialog = true }
                                ) { ProfilePictureIcon(teamImageFile, 80.dp , false) }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    TextField(
                                        value = teamName,
                                        onValueChange = vm::setTeamName,
                                        label = {
                                            Row {
                                                Text("Team name")
                                                Text(
                                                    text = "*",
                                                    color = MaterialTheme.colorScheme.error, // Red color
                                                    modifier = Modifier.padding(start = 4.dp) // Optional padding for better spacing
                                                )
                                            }
                                                },
                                        modifier = Modifier.fillMaxWidth(0.9f),
                                        isError = teamNameError.isNotBlank(),
                                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                            )
                            if (teamNameError.isNotEmpty()) {
                            Text(text = teamNameError, color = MaterialTheme.colorScheme.error, modifier=Modifier,)
                            }
                        }
                    }
                }
                item {
                    TextField(
                        value = teamDescription,
                        onValueChange = vm::setTeamDescription,
                        label = { Text("Team Description") },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(100.dp)
                            .width(414.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    TextField(
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
                    modifier = Modifier.fillMaxWidth(0.9f),
                    isError = teamCategoryError.isNotBlank(),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                    )
                    if (teamCategoryError.isNotEmpty()) {
                    Text(text = teamCategoryError, color = MaterialTheme.colorScheme.error, modifier=Modifier,)
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Add members", style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,)
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AddMembersManualSection(vm)
                    }
                }

                item {
                    Column(modifier = Modifier.weight(0.4f)){
                    Row(){
                    Button(
                        onClick = {
                            vm.checkTeamName()
                            vm.checkTeamCategory()
                            if(isSaveEnabled){
                                showWarningDialog = true
                            }
                        },
                        modifier = Modifier.padding(16.dp),
                        enabled = isSaveEnabled) { Text(text = "Save") }
                    Button(
                        onClick = {
                            vm.cancelNewTeam()
                            vm.closeSheet()
                        },
                        modifier = Modifier.padding(16.dp)) { Text(text = "Cancel") }
                    }
                    }
                }
    }
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
                            val permissionCheckResult = checkSelfPermission( context , Manifest.permission.CAMERA)

                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                cameraLauncher.launch()
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
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


        if (showWarningDialog) {
            AlertDialog(
                onDismissRequest = { showWarningDialog = false },
                confirmButton = {
                    Button(onClick = {
                        if (teamNameError.isBlank() && teamCategoryError.isBlank()) {
                            vm.createTeam()
                            Toast.makeText(context, "Team created successfully!", Toast.LENGTH_SHORT).show()
                            vm.closeSheet()
                            showWarningDialog = false
                        } else{
                            vm.checkTeamName()
                            vm.checkTeamCategory()
                        }
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(onClick = { showWarningDialog = false }) {
                        Text("Cancel")
                    }
                },
                title = { Text("Confirmation") },
                text = { Text("Are you sure you want to save?") }
            )
        }

    }}}
}

@Composable
fun ProfilePictureIcon(imageFile: androidx.compose.ui.graphics.ImageBitmap?, size: Dp, isView:Boolean) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape), // Use CircleShape to clip the image into a circle
        contentAlignment = Alignment.Center

    ) {
        if (imageFile != null) {
            Image(bitmap = imageFile, contentDescription = "Profile picture",
                modifier= Modifier.size(size)
                )
        } else {
            if(isView){
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = "Default Icon",
                modifier = Modifier.size(42.dp)
            )}
            else{
                Box(  modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.secondaryContainer
                        ), // Adjust thickness and color of the border as needed
                        CircleShape
                    ), contentAlignment = Alignment.Center){
                Icon(
                imageVector = Icons.Default.AddPhotoAlternate,
                contentDescription = "Default Icon",
                modifier = Modifier.size(48.dp)
            )
            }}
        }
    }
}


@Composable
fun AddFloatingButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = Modifier.padding(28.dp),
        icon = { Icon(Icons.Filled.Add, "Add floating button") },
        text = { Text(text = "New team") },
    )
}
/*
@Composable
fun ImagePickerDialog(
    model: MyModel = MyModel(),
    onImageSelected: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Select Image") },
        text = { LazyRow {

            model.teamImages.forEach {imageId ->
                item {
                    ImageItem(imageId, onImageSelected)
                }

            }

        }
        },
        confirmButton = {},
        dismissButton = {}
    )
}*/

@Preview
@Composable
fun PickImageFromGallery() {

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        imageUri?.let {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap.value = MediaStore.Images
                    .Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }

            bitmap.value?.let { btm ->
                Image(
                    bitmap = btm.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(400.dp)
                        .padding(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { launcher.launch("image/*") }) {
            Text(text = "Pick Image")
        }
    }

}

@Composable
fun ImageItem(drawableResId: Int, onImageSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onImageSelected(drawableResId) }
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = drawableResId),
            contentDescription = null,
            modifier = Modifier.size(70.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

    }
}

@Composable
fun AddMembersManualSection(vm: TeamViewModel) {
    val context = LocalContext.current
    val emailText by vm.newMemberEmail.collectAsState()
    val emailErrorText by vm.newMemberEmailError.collectAsState()
    val selectedRole by vm.selectedRole.collectAsState()
    val dropDownExpanded = remember { mutableStateOf(false) }
    val pendingMembers by vm.pendingMembers.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = emailText,
                onValueChange = { vm.updateNewMemberEmail(it) },
                label = { if (emailErrorText.isBlank()) Text("Email") else Text(emailErrorText) },
                isError = emailErrorText.isNotBlank(),
                placeholder = { Text("johndoe@email.com") },
                modifier = Modifier.weight(1f)
            )
            Box {
                TextButton(onClick = { dropDownExpanded.value = true }) {
                    Text(text = selectedRole?.toString() ?: "Select a role", style = MaterialTheme.typography.bodyLarge)
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
                DropdownMenu(
                    expanded = dropDownExpanded.value,
                    onDismissRequest = { dropDownExpanded.value = false }
                ) {
                    Role.entries.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.toString()) },
                            onClick = {
                                vm.selectRole(role)
                                dropDownExpanded.value = false
                            }
                        )
                    }
                }
            }
            Button(onClick = {
                vm.checkNewMemberEmail()
                if (emailErrorText.isBlank() && selectedRole != null) {
                    vm.addPendingMember(emailText, selectedRole!!)
                    vm.updateNewMemberEmail("")
                    vm.deselectRole()
                    Toast.makeText(context, "Member added to pending list!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Please enter a valid email and select a role", Toast.LENGTH_LONG).show()
                }
            }) {
                Text("Invite")
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = emailText,
                onValueChange = { vm.updateNewMemberEmail(it) },
                label = { if (emailErrorText.isBlank()) Text("Email") else Text(emailErrorText) },
                isError = emailErrorText.isNotBlank(),
                placeholder = { Text("johndoe@email.com") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row{
            Box {
                TextButton(onClick = { dropDownExpanded.value = true }) {
                    Text(text = selectedRole?.toString() ?: "Select a role", style = MaterialTheme.typography.bodyLarge)
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
                DropdownMenu(
                    expanded = dropDownExpanded.value,
                    onDismissRequest = { dropDownExpanded.value = false }
                ) {
                    Role.entries.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.toString()) },
                            onClick = {
                                vm.selectRole(role)
                                dropDownExpanded.value = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                vm.checkNewMemberEmail()
                if (emailErrorText.isBlank() && selectedRole != null) {
                    vm.addPendingMember(emailText, selectedRole!!)
                    vm.updateNewMemberEmail("")
                    vm.deselectRole()
                    Toast.makeText(context, "Member added to pending list!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Please enter a valid email and select a role", Toast.LENGTH_LONG).show()
                }
            }) {
                Text("Invite")
            }}
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Display pending members
    Column(modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        pendingMembers.forEach { (email, role) ->
            PendingMemberChip(
                email = email,
                role = role,
                onRoleSelected = { newRole ->
                    // Update the role of the pending member
                    vm.updatePendingMemberRole(email, newRole)
                },
                onRemove = { vm.removePendingMember(email) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun PendingMemberChip(
    email: String,
    role: Role,
    roles: List<Role> = listOf(Role.Teacher, Role.Admin, Role.User),
    onRoleSelected: (Role) -> Unit,
    onRemove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        LazyRow(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
            Column(modifier = Modifier.padding(start = 6.dp, end = 6.dp)) {
                Text(text = email, style = MaterialTheme.typography.bodyLarge)
            }
            }
            item {
            Box {
                Row(
                    modifier = Modifier
                        .clickable { expanded = true }
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = role.toString(),
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
                    roles.forEach { r ->
                        DropdownMenuItem(
                            text = {
                                Row {
                                    Text(
                                        text = r.toString(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            },
                            onClick = {
                                onRoleSelected(r)
                                expanded = false
                            }
                        )
                    }
                }
            }
            }
            item {
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.padding(start = 6.dp, end = 6.dp)) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(18.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White)
            }}
        }}
    }
}

