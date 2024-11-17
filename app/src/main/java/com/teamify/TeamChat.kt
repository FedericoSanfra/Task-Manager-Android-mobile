package com.teamify

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.teamify.ui.theme.AppTheme
import kotlinx.coroutines.flow.StateFlow
import java.text.DateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

data class Message(
    val id: Long,
    val teamId: Long,
    val sender: Person,
    val content: String,
    val timestamp: Long,
    val taggedUsers: List<Person> = emptyList()
)


class ChatViewModel(private val model: MyModel, teamId: Long?) : ViewModel() {
    private val _team = mutableStateOf(model.getTeamById(teamId!!)!!)
    val team : State<Team> = _team

    val person = model.personList

    val messages: StateFlow<List<Message>> = model.messages
    val unreadCount: StateFlow<Int> = model.unreadCount
    fun sendMessage(message: Message) = model.sendMessage(message)
    fun markMessagesAsRead() = model.markMessagesAsRead()

    fun getPersonFromTeam(team: Team) = model.getPersonsFromTeam(team)

}

class ChatVMFactory(context: Context,val teamId: Long?) : ViewModelProvider.Factory {
    val model: MyModel = (context.applicationContext as? MyApplication)?.model
        ?: throw IllegalArgumentException("Bad applcation class")

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ChatViewModel::class.java))
            ChatViewModel(model, teamId) as T
        else throw java.lang.IllegalArgumentException("Unknown ViewModel class")//i dont have nothing else of view model
    }

}

    @Composable
    fun MessageCard(message: Message, currentUser: Person) {
        val isCurrentUser = message.sender.id.toInt() == currentUser.id.toInt()
        val alignment = if (isCurrentUser) Alignment.End else Alignment.Start
        val bubbleColor = if (isCurrentUser) Color.Green else Color(0xFFE0E0E0)
        val bubbleShape = RoundedCornerShape(12.dp)

        AppTheme {

        Column(
            horizontalAlignment = alignment,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row {
                if (!isCurrentUser) {
                    Column(
                        modifier = Modifier.padding(end = 8.dp, top = 8.dp)
                    ) {
                        if(message.sender.imageFile!=null){
                            Image(bitmap = message.sender.imageFile, contentDescription = "Image sender file",
                                modifier = Modifier.size(24.dp)
                            )
                        }else{
                            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Default account icon person",modifier = Modifier.size(24.dp) )
                        }

                    }
                }

                Box(
                    modifier = Modifier
                        .background(bubbleColor, shape = bubbleShape)
                        .padding(12.dp)
                ) {
                    Column {
                        if (!isCurrentUser) {
                            Text(
                                text = "${message.sender.firstName} ${message.sender.lastName}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = DateFormat.getTimeInstance(DateFormat.SHORT)
                                .format(Date(message.timestamp)),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

            }

        }

    }}

    @Composable
    fun keyboardAsState(): State<Boolean> {
        val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
        return rememberUpdatedState(isImeVisible)
    }

    @Composable
    fun NotificationBadge(count: Int) {
        if (count > 0) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Red, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatScreen(
        teamId: Long?,
        navController: NavController,
        vm: ChatViewModel = viewModel(factory = ChatVMFactory(LocalContext.current, teamId))
    ) {
        AppTheme {
        val messages by vm.messages.collectAsState()
        val unreadCount by vm.unreadCount.collectAsState()
        val listState = rememberLazyListState()
        val isKeyboardOpen by keyboardAsState()
        val team by vm.team
        val currentUser = Person(
            1,
            "Alice",
            "Reyes",
            "alice@libero.it",
            isChecked = false,
            imageFile = null
        )
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row {
                            Column(
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                team.teamImage?.let {
                                    Image(bitmap = it, contentDescription = "image picture",modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)  )
                                }
                            }
                            Column {
                                Text(text = team.name, color = Color.White)
                            }

                        }

                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },

                    modifier = Modifier.statusBarsPadding(),
                    colors = TopAppBarDefaults.topAppBarColors(Color.Gray)
                )
            },
            content = { paddingValues ->
                Spacer(modifier = Modifier.padding(paddingValues))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    ) {
                        items(messages) { message ->
                            MessageCard(message = message, currentUser = currentUser)
                        }
                    }
                    LaunchedEffect(messages, isKeyboardOpen) {
                        listState.animateScrollToItem(messages.size - 1)
                    }

                }
            },
            bottomBar = {
                MessageInput(
                    onSend = { content, taggedUsers ->
                        val message = Message(
                            id = kotlin.random.Random.nextLong(),
                            teamId = team.id,
                            sender = currentUser,
                            content = content,
                            timestamp = System.currentTimeMillis(),
                            taggedUsers = taggedUsers
                        )
                        vm.sendMessage(message)
                    },
                    teamMembers = vm.getPersonFromTeam(team),
                    currentUser = currentUser
                )
            }
        )
    }}


    @Composable
    fun MessageInput(
        onSend: (String, List<Person>) -> Unit,
        teamMembers: List<Person>,
        currentUser: Person
    ) {
        var content by remember { mutableStateOf("") }
        val taggedUsers = remember { mutableStateListOf<Person>() }
        var showUserDropdown by remember { mutableStateOf(false) }
        var filteredUsers by remember { mutableStateOf(listOf<Person>()) }

        LaunchedEffect(content) {
            val atIndex = content.lastIndexOf('@')
            if (atIndex != -1) {
                val query = content.substring(atIndex + 1)
                filteredUsers =
                    teamMembers.filter { it.firstName.startsWith(query, ignoreCase = true) }
                showUserDropdown = filteredUsers.isNotEmpty()
            } else {
                showUserDropdown = false
            }
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = content,
                        onValueChange = { content = it },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .imePadding(), // Ensure the text field respects the IME padding
                        placeholder = { Text("Enter message...") }
                    )
                    Button(
                        onClick = {
                            if (content.isNotBlank()) {
                                onSend(content, taggedUsers)
                                content = ""
                                taggedUsers.clear()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color.Gray),
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .imePadding()
                    ) {
                        Text("Send", color = Color.White)
                    }
                }
                if (showUserDropdown) {
                    DropdownMenu(
                        expanded = showUserDropdown,
                        onDismissRequest = { showUserDropdown = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                    ) {
                        filteredUsers.forEach { user ->
                            DropdownMenuItem(text = { Text(user.firstName) }, onClick = {
                                val atIndex = content.lastIndexOf('@')
                                content =
                                    content.replaceRange(
                                        atIndex,
                                        content.length,
                                        "@${user.firstName} "
                                    )
                                taggedUsers.add(user)
                                showUserDropdown = false
                            })
                        }
                    }
                }
            }
        }

    }
