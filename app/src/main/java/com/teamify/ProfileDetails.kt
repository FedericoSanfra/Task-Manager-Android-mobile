package com.teamify

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.teamify.ui.theme.AppTheme


//USED ONLY TO SEE THE PREVIEW
@Preview(showBackground = true)
@Composable
fun ProfileDetailsPanePreview() {
    val mockPerson = Person(
        id = 1L,
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@example.com",
        isChecked = false,
        imageFile = null // replace with your actual drawable resource
    )
    val mockModel = MyModel()
    val mockViewModel = ProfileViewModel(mockModel, mockPerson.id)
    ProfileDetailsPane(
        personId = mockPerson.id,
        navController = rememberNavController(),
        vm = mockViewModel
    )
}

@Composable
fun CircleProfileComponent(
    vm: ProfileViewModel
) {
    AppTheme {

    if(vm.user.value.imageFile!=null){
        Image(bitmap = vm.user.value.imageFile!!, contentDescription ="Image person file",  modifier = Modifier
            .size(200.dp) // Adjust the size as needed
            .clip(CircleShape))
    }
    else{
        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Default icon account",  modifier = Modifier
            .size(200.dp) // Adjust the size as needed
            .clip(CircleShape))
    }
}}

@Composable
fun UserInformationPane(vm: ProfileViewModel) {
    AppTheme {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 0.dp, 0.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = (vm.user.value.firstName) + " " + (vm.user.value.lastName),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        2.dp,
                        RoundedCornerShape(8.dp)
                    ) // Applica un'ombra con bordi arrotondati
                    .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(8.dp))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "email",
                        tint= MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 0.dp, end = 16.dp)
                    )
                    Text(
                        text = vm.user.value.email,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileColumnLayout(
    vm: ProfileViewModel,
    navController: NavController
){
    AppTheme {
    val c=MaterialTheme.colorScheme.primaryContainer
    Column (modifier = Modifier
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        TopAppBar(title = {
            Text(text = "Personal info")},
            colors = TopAppBarDefaults.topAppBarColors(containerColor = c),
            actions= {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Navigate back")
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.surface), //0xFFFEF7FF
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircleProfileComponent(vm)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .padding(horizontal = 16.dp)
        ) {
            UserInformationPane(vm)
        }
    }
}}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileRowLayout(
    vm: ProfileViewModel,
    navController: NavController
){
    AppTheme {
    val c=MaterialTheme.colorScheme.primaryContainer
    Column(
        modifier = Modifier
            .fillMaxSize()
           // .padding(top = 16.dp)
    ) {
        TopAppBar(
            title = { Text(text = "Personal info") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = c),
            modifier = Modifier.fillMaxWidth(),
            actions = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate back")
                }
            }
        )
        Row(modifier= Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(start = 16.dp)
                    .background(MaterialTheme.colorScheme.surface),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircleProfileComponent(vm)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                UserInformationPane(vm)
            }
        }
    }
}}

@Composable
fun ProfileDetailsPane(
    personId: Long?,
    navController: NavController,
    vm: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(LocalContext.current, personId))
) {
    AppTheme {

    BoxWithConstraints (
        modifier= Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (this.maxHeight > this.maxWidth)
            ProfileColumnLayout(vm, navController)
        else
            ProfileRowLayout(vm, navController)
    }
}}

class ProfileViewModel(private val myModel: MyModel, personId : Long?): ViewModel() {

    private val _user = mutableStateOf<Person>( myModel.findPersonById(personId!!)!! )
    val user : State<Person> = _user

}