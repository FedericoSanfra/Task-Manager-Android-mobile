package com.teamify


import android.media.Image
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Date

class MyModel {

    // db connection, lazy initialization because the model tries to access it before the app is initialized
    private val db by lazy { Firebase.firestore }

    private val _inputSearch: MutableStateFlow<String> = MutableStateFlow("")
    val inputSearch: StateFlow<String> = _inputSearch // public counterpart

    private val _recentClickedTeams: MutableStateFlow<List<Team>> = MutableStateFlow(emptyList())
    val recentClickedTeams: StateFlow<List<Team>> = _recentClickedTeams

    private val _recentTeamsExpanded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val recentTeamsExpanded: StateFlow<Boolean> = _recentTeamsExpanded
    fun setListExpansion(){
        _recentTeamsExpanded.value=!_recentTeamsExpanded.value
    }
    fun newRecentTeam( team: Team){
        _recentClickedTeams.value += team
        var firstIndex = _recentClickedTeams.value.first().id
        var uniqueElements =
            _recentClickedTeams.value.toSet().toList()
        if (uniqueElements.size > 5) {
            uniqueElements =
                uniqueElements.filter { id -> firstIndex != id.id }
        }
        _recentClickedTeams.value = uniqueElements

    }

    fun reorderRecentTeam( team: Team){
        val reOrderedTeams= _recentClickedTeams.value.filter { it-> it.id!=team.id }.toMutableList()
        reOrderedTeams+= team
        _recentClickedTeams.value=reOrderedTeams
    }

    fun setS(s: String) {
        _inputSearch.value = s
    }

    //todo: db access read
    fun addMemberToTeam(teamId: Long, email: String, role: Role) : AddingMemberErrorType{

        val personToAdd = findPersonByEmail(email)

        if (personToAdd != null) {
            val team = getTeamById(teamId)

            //checks if the person is already in the team
            if(team?.roles?.containsKey(personToAdd.id) == true){
                return AddingMemberErrorType.USER_ALREADY_IN_TEAM
            }

            team?.roles?.put(personToAdd.id, role)
            return AddingMemberErrorType.SUCCESS
        }
        return AddingMemberErrorType.USER_NOT_FOUND
    }



    //todo: db access read
    private fun findPersonByEmail(email: String): Person? {
        return persons.find { it.email == email }
    }

    //todo: db access read
    fun findPersonById(id: Long): Person? {
        return persons.find { it.id == id }
    }

    //todo: db access
    fun getLoggedUser(): Person {
        return persons.random()
    }

        val personList = mutableListOf<Person>(
            Person(
                id = 1,
                firstName = "Alice",
                lastName = "Reyes",
                email = "alice@libero.it",
                isChecked = true,
                imageFile = null
            ),
            Person(
                id = 2,
                firstName = "Jane",
                lastName = "Smith",
                email = "jane.smith@example.com",
                isChecked = false,
                imageFile = null
            ),
            Person(
                id = 3,
                firstName = "Alice",
                lastName = "Johnson",
                email = "alice.johnson@example.com",
                isChecked = true,
                imageFile = null
            ),
            Person(
                id = 4,
                firstName = "Bob",
                lastName = "Brown",
                email = "bob.brown@example.com",
                isChecked = false,
                imageFile = null
            ),
            Person(
                id = 5,
                firstName = "Emily",
                lastName = "Davis",
                email = "emily.davis@example.com",
                isChecked = true,
                imageFile = null
            ),
            Person(
                id = 6,
                firstName = "Michael",
                lastName = "Wilson",
                email = "michael.wilson@example.com",
                isChecked = false,
                imageFile = null
            ),
            Person(
                id = 7,
                firstName = "Sophia",
                lastName = "Martinez",
                email = "sophia.martinez@example.com",
                isChecked = true,
                imageFile = null
            ),
            Person(
                id = 8,
                firstName = "David",
                lastName = "Anderson",
                email = "david.anderson@example.com",
                isChecked = false,
                imageFile = null
            ),
            Person(
                id = 9,
                firstName = "Olivia",
                lastName = "Taylor",
                email = "olivia.taylor@example.com",
                isChecked = true,
                imageFile = null
            ),
            Person(
                id = 10,
                firstName = "James",
                lastName = "White",
                email = "james.white@example.com",
                isChecked = false,
                imageFile = null
            )
        )

        // List of teams
        private val _teamsList: MutableStateFlow<List<Team>> = MutableStateFlow(
            listOf(
                Team(
                    id = 1,
                    name = "Team A",
                    teamImage = null,
                    description = "Description for Team A",
                    category = "Category X",
                    roles = mutableMapOf(
                        1L to Role.Teacher,
                        2L to Role.Admin,
                        3L to Role.User
                    ),
                    tasks = mutableListOf(
                        Task(
                            1,
                            "Task 1",
                            "Task description 1 for Team A",
                            emptyList(),
                            Date("24/11/2025"),
                            Date(),
                            0.5f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        ),
                        Task(
                            2,
                            "Task 2",
                            "Task description 2 for Team A",
                            emptyList(),
                            Date("05/10/2024"),
                            Date(),
                            0.7f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        )
                    ),
                    messages = mutableListOf(
                        Message(id = 2, 1, sender = Person(2, "Alice", "Alice", "alice2@libero.it", isChecked = false, imageFile = null), content = "Hello, how are you?", timestamp = System.currentTimeMillis()),
                        Message(id = 3, 1,sender = Person(3, "Bob", "Bob", "bob@libero.it", isChecked = false, imageFile = null), content = "I'm good, thanks!", timestamp = System.currentTimeMillis()),
                        Message(id = 4, 1, sender = Person(4, "Charlie", "Charlie", "charlie@libero.it", isChecked = false, imageFile = null), content = "Great!", timestamp = System.currentTimeMillis()),
                        Message(id = 5, 2, sender = Person(5, "Eve", "Eve", "eve@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 6, 3, sender = Person(6, "Fred", "Fred", "fred@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 7, 4, sender = Person(7, "Grace", "Grace", "grace@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 8, 2, sender = Person(8, "Hazel", "Hazel", "hazel@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 9, 3, sender = Person(9, "Ian", "Ian", "ian@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 10, 1, sender = Person(1, "Alice", "Reyes", "alice@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                    ),
                    creationDate = Date()
                ),
                Team(
                    id = 2,
                    name = "Team B",
                    teamImage =null,
                    description = "Description for Team B",
                    category = "Category Y",
                    roles = mutableMapOf(
                        4L to Role.Teacher,
                        5L to Role.Admin,
                        6L to Role.User
                    ),
                    tasks = mutableListOf(
                        Task(
                            1,
                            "Task 1",
                            "Task description 1 for Team B",
                            emptyList(),
                            Date("24/11/2025"),
                            Date(),
                            0.5f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        ),
                        Task(
                            2,
                            "Task 2",
                            "Task description 2 for Team B",
                            emptyList(),
                            Date("05/10/2024"),
                            Date(),
                            0.7f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        )
                    ),
                    messages = mutableListOf(
                        Message(id = 2, 1, sender = Person(2, "Alice", "Alice", "alice2@libero.it", isChecked = false, imageFile = null), content = "Hello, how are you?", timestamp = System.currentTimeMillis()),
                        Message(id = 3, 1,sender = Person(3, "Bob", "Bob", "bob@libero.it", isChecked = false, imageFile = null), content = "I'm good, thanks!", timestamp = System.currentTimeMillis()),
                        Message(id = 4, 1, sender = Person(4, "Charlie", "Charlie", "charlie@libero.it", isChecked = false, imageFile = null), content = "Great!", timestamp = System.currentTimeMillis()),
                        Message(id = 5, 2, sender = Person(5, "Eve", "Eve", "eve@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 6, 3, sender = Person(6, "Fred", "Fred", "fred@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 7, 4, sender = Person(7, "Grace", "Grace", "grace@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 8, 2, sender = Person(8, "Hazel", "Hazel", "hazel@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 9, 3, sender = Person(9, "Ian", "Ian", "ian@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 10, 1, sender = Person(1, "Alice", "Reyes", "alice@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                    ),
                    creationDate = Date()
                ),
                // Add other teams here
                Team(
                    id = 3,
                    name = "Team C",
                    teamImage = null,
                    description = "Description for Team C",
                    category = "Category Z",
                    roles = mutableMapOf(
                        7L to Role.Teacher,
                        8L to Role.Admin,
                        9L to Role.User
                    ),
                    tasks = mutableListOf(
                        Task(
                            1,
                            "Task 1",
                            "Task description 1 for Team C",
                            emptyList(),
                            Date("24/11/2025"),
                            Date(),
                            0.5f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        ),
                        Task(
                            2,
                            "Task 2",
                            "Task description 2 for Team C",
                            emptyList(),
                            Date("05/10/2024"),
                            Date(),
                            0.7f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        )
                    ),
                    messages = mutableListOf(
                        Message(id = 2, 1, sender = Person(2, "Alice", "Alice", "alice2@libero.it", isChecked = false, imageFile = null), content = "Hello, how are you?", timestamp = System.currentTimeMillis()),
                        Message(id = 3, 1,sender = Person(3, "Bob", "Bob", "bob@libero.it", isChecked = false, imageFile = null), content = "I'm good, thanks!", timestamp = System.currentTimeMillis()),
                        Message(id = 4, 1, sender = Person(4, "Charlie", "Charlie", "charlie@libero.it", isChecked = false, imageFile = null), content = "Great!", timestamp = System.currentTimeMillis()),
                        Message(id = 5, 2, sender = Person(5, "Eve", "Eve", "eve@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 6, 3, sender = Person(6, "Fred", "Fred", "fred@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 7, 4, sender = Person(7, "Grace", "Grace", "grace@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 8, 2, sender = Person(8, "Hazel", "Hazel", "hazel@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 9, 3, sender = Person(9, "Ian", "Ian", "ian@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 10, 1, sender = Person(1, "Alice", "Reyes", "alice@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                    ),
                    creationDate = Date()
                ),
                Team(
                    id = 4,
                    name = "Team D",
                    teamImage = null,
                    description = "Description for Team D",
                    category = "Category X",
                    roles = mutableMapOf(
                        10L to Role.Teacher,
                        9L to Role.Admin,
                        8L to Role.User
                    ),
                    tasks = mutableListOf(
                        Task(
                            1,
                            "Task 1",
                            "Task description 1 for Team D",
                            emptyList(),
                            Date("24/11/2025"),
                            Date(),
                            0.5f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        ),
                        Task(
                            2,
                            "Task 2",
                            "Task description 2 for Team D",
                            emptyList(),
                            Date("05/10/2024"),
                            Date(),
                            0.7f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        )
                    ),
                    messages = mutableListOf(
                        Message(id = 2, 1, sender = Person(2, "Alice", "Alice", "alice2@libero.it", isChecked = false, imageFile = null), content = "Hello, how are you?", timestamp = System.currentTimeMillis()),
                        Message(id = 3, 1,sender = Person(3, "Bob", "Bob", "bob@libero.it", isChecked = false, imageFile = null), content = "I'm good, thanks!", timestamp = System.currentTimeMillis()),
                        Message(id = 4, 1, sender = Person(4, "Charlie", "Charlie", "charlie@libero.it", isChecked = false, imageFile = null), content = "Great!", timestamp = System.currentTimeMillis()),
                        Message(id = 5, 2, sender = Person(5, "Eve", "Eve", "eve@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 6, 3, sender = Person(6, "Fred", "Fred", "fred@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 7, 4, sender = Person(7, "Grace", "Grace", "grace@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 8, 2, sender = Person(8, "Hazel", "Hazel", "hazel@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 9, 3, sender = Person(9, "Ian", "Ian", "ian@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 10, 1, sender = Person(1, "Alice", "Reyes", "alice@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                    ),
                    creationDate = Date()
                ),
                Team(
                    id = 5,
                    name = "Team E",
                    teamImage =null,
                    description = "Description for Team E",
                    category = "Category Y",
                    roles = mutableMapOf(
                        7L to Role.Teacher,
                        4L to Role.Admin,
                        5L to Role.User
                    ),
                    tasks = mutableListOf(
                        Task(
                            1,
                            "Task 1",
                            "Task description 1 for Team E",
                            emptyList(),
                            Date("24/11/2025"),
                            Date(),
                            0.5f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        ),
                        Task(
                            2,
                            "Task 2",
                            "Task description 2 for Team E",
                            emptyList(),
                            Date("05/10/2024"),
                            Date(),
                            0.7f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        )
                    ),
                    messages = mutableListOf(
                        Message(id = 2, 1, sender = Person(2, "Alice", "Alice", "alice2@libero.it", isChecked = false, imageFile = null), content = "Hello, how are you?", timestamp = System.currentTimeMillis()),
                        Message(id = 3, 1,sender = Person(3, "Bob", "Bob", "bob@libero.it", isChecked = false, imageFile = null), content = "I'm good, thanks!", timestamp = System.currentTimeMillis()),
                        Message(id = 4, 1, sender = Person(4, "Charlie", "Charlie", "charlie@libero.it", isChecked = false, imageFile = null), content = "Great!", timestamp = System.currentTimeMillis()),
                        Message(id = 5, 2, sender = Person(5, "Eve", "Eve", "eve@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 6, 3, sender = Person(6, "Fred", "Fred", "fred@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 7, 4, sender = Person(7, "Grace", "Grace", "grace@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 8, 2, sender = Person(8, "Hazel", "Hazel", "hazel@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 9, 3, sender = Person(9, "Ian", "Ian", "ian@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 10, 1, sender = Person(1, "Alice", "Reyes", "alice@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                    ),
                    creationDate = Date()
                ),
                Team(
                    id = 6,
                    name = "Team F",
                    teamImage = null,
                    description = "Description for Team F",
                    category = "Category Z",
                    roles = mutableMapOf(
                        6L to Role.Teacher,
                        7L to Role.Admin,
                        8L to Role.User
                    ),
                    tasks = mutableListOf(
                        Task(
                            1,
                            "Task 1",
                            "Task description 1 for Team F",
                            emptyList(),
                            Date("24/11/2025"),
                            Date(),
                            0.5f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        ),
                        Task(
                            2,
                            "Task 2",
                            "Task description 2 for Team F",
                            emptyList(),
                            Date("05/10/2024"),
                            Date(),
                            0.7f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        )
                    ),
                    messages = mutableListOf(
                        Message(id = 2, 1, sender = Person(2, "Alice", "Alice", "alice2@libero.it", isChecked = false, imageFile = null), content = "Hello, how are you?", timestamp = System.currentTimeMillis()),
                        Message(id = 3, 1,sender = Person(3, "Bob", "Bob", "bob@libero.it", isChecked = false, imageFile = null), content = "I'm good, thanks!", timestamp = System.currentTimeMillis()),
                        Message(id = 4, 1, sender = Person(4, "Charlie", "Charlie", "charlie@libero.it", isChecked = false, imageFile = null), content = "Great!", timestamp = System.currentTimeMillis()),
                        Message(id = 5, 2, sender = Person(5, "Eve", "Eve", "eve@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 6, 3, sender = Person(6, "Fred", "Fred", "fred@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 7, 4, sender = Person(7, "Grace", "Grace", "grace@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 8, 2, sender = Person(8, "Hazel", "Hazel", "hazel@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 9, 3, sender = Person(9, "Ian", "Ian", "ian@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 10, 1, sender = Person(1, "Alice", "Reyes", "alice@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                    ),
                    creationDate = Date()
                ),
                Team(
                    id = 7,
                    name = "Team G",
                    teamImage = null,
                    description = "Description for Team G",
                    category = "Category X",
                    roles = mutableMapOf(
                        9L to Role.Teacher,
                        10L to Role.Admin,
                        1L to Role.User
                    ),
                    tasks = mutableListOf(
                        Task(
                            1,
                            "Task 1",
                            "Task description 1 for Team G",
                            emptyList(),
                            Date("24/11/2025"),
                            Date(),
                            0.5f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        ),
                        Task(
                            2,
                            "Task 2",
                            "Task description 2 for Team G",
                            emptyList(),
                            Date("05/10/2024"),
                            Date(),
                            0.7f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        )
                    ),
                    messages = mutableListOf(
                        Message(id = 2, 1, sender = Person(2, "Alice", "Alice", "alice@libero.it", isChecked = false, imageFile = null), content = "Hello, how are you?", timestamp = System.currentTimeMillis()),
                        Message(id = 3, 1,sender = Person(3, "Bob", "Bob", "bob@libero.it", isChecked = false, imageFile = null), content = "I'm good, thanks!", timestamp = System.currentTimeMillis()),
                        Message(id = 4, 1, sender = Person(4, "Charlie", "Charlie", "charlie@libero.it", isChecked = false, imageFile = null), content = "Great!", timestamp = System.currentTimeMillis()),
                        Message(id = 5, 2, sender = Person(5, "Eve", "Eve", "eve@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 6, 3, sender = Person(6, "Fred", "Fred", "fred@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 7, 4, sender = Person(7, "Grace", "Grace", "grace@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 8, 2, sender = Person(8, "Hazel", "Hazel", "hazel@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 9, 3, sender = Person(9, "Ian", "Ian", "ian@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 10, 1, sender = Person(10, "Jack", "Jack", "jack@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                    ),
                    creationDate = Date()
                ),
                Team(
                    id = 8,
                    name = "Team H",
                    teamImage = null,
                    description = "Description for Team H",
                    category = "Category Y",
                    roles = mutableMapOf(
                        2L to Role.Teacher,
                        3L to Role.Admin,
                        4L to Role.User
                    ),
                    tasks = mutableListOf(
                        Task(
                            1,
                            "Task 1",
                            "Task description 1 for Team H",
                            emptyList(),
                            Date("24/11/2025"),
                            Date(),
                            0.5f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        ),
                        Task(
                            2,
                            "Task 2",
                            "Task description 2 for Team H",
                            emptyList(),
                            Date("05/10/2024"),
                            Date(),
                            0.7f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        )
                    ),
                    messages = mutableListOf(
                        Message(id = 2, 1, sender = Person(2, "Alice", "Alice", "alice2@libero.it", isChecked = false, imageFile = null), content = "Hello, how are you?", timestamp = System.currentTimeMillis()),
                        Message(id = 3, 1,sender = Person(3, "Bob", "Bob", "bob@libero.it", isChecked = false, imageFile = null), content = "I'm good, thanks!", timestamp = System.currentTimeMillis()),
                        Message(id = 4, 1, sender = Person(4, "Charlie", "Charlie", "charlie@libero.it", isChecked = false, imageFile = null), content = "Great!", timestamp = System.currentTimeMillis()),
                        Message(id = 5, 2, sender = Person(5, "Eve", "Eve", "eve@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 6, 3, sender = Person(6, "Fred", "Fred", "fred@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 7, 4, sender = Person(7, "Grace", "Grace", "grace@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 8, 2, sender = Person(8, "Hazel", "Hazel", "hazel@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 9, 3, sender = Person(9, "Ian", "Ian", "ian@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 10, 1, sender = Person(1, "Alice", "Reyes", "alice@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                    ),
                    creationDate = Date()
                ),
                Team(
                    id = 9,
                    name = "Team I",
                    teamImage = null,
                    description = "Description for Team I",
                    category = "Category Z",
                    roles = mutableMapOf(
                        5L to Role.Teacher,
                        6L to Role.Admin,
                        7L to Role.User
                    ),
                    tasks = mutableListOf(
                        Task(
                            1,
                            "Task 1",
                            "Task description 1 for Team I",
                            emptyList(),
                            Date("24/11/2025"),
                            Date(),
                            0.5f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        ),
                        Task(
                            2,
                            "Task 2",
                            "Task description 2 for Team I",
                            emptyList(),
                            Date("05/10/2024"),
                            Date(),
                            0.7f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        )
                    ),
                    messages = mutableListOf(
                        Message(id = 2, 1, sender = Person(2, "Alice", "Alice", "alice2@libero.it", isChecked = false, imageFile = null), content = "Hello, how are you?", timestamp = System.currentTimeMillis()),
                        Message(id = 3, 1,sender = Person(3, "Bob", "Bob", "bob@libero.it", isChecked = false, imageFile = null), content = "I'm good, thanks!", timestamp = System.currentTimeMillis()),
                        Message(id = 4, 1, sender = Person(4, "Charlie", "Charlie", "charlie@libero.it", isChecked = false, imageFile = null), content = "Great!", timestamp = System.currentTimeMillis()),
                        Message(id = 5, 2, sender = Person(5, "Eve", "Eve", "eve@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 6, 3, sender = Person(6, "Fred", "Fred", "fred@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 7, 4, sender = Person(7, "Grace", "Grace", "grace@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 8, 2, sender = Person(8, "Hazel", "Hazel", "hazel@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 9, 3, sender = Person(9, "Ian", "Ian", "ian@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 10, 1, sender = Person(1, "Alice", "Reyes", "alice@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                    ),
                    creationDate = Date()
                ),
                Team(
                    id = 10,
                    name = "Team J",
                    teamImage = null,
                    description = "Description for Team J",
                    category = "Category X",
                    roles = mutableMapOf(
                        8L to Role.Teacher,
                        9L to Role.Admin,
                        10L to Role.User
                    ),
                    tasks = mutableListOf(
                        Task(
                            1,
                            "Task 1",
                            "Task description 1 for Team J",
                            emptyList(),
                            Date("24/11/2025"),
                            Date(),
                            0.5f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        ),
                        Task(
                            2,
                            "Task 2",
                            "Task description 2 for Team J",
                            emptyList(),
                            Date("05/10/2024"),
                            Date(),
                            0.7f,
                            "",
                            emptyList(),
                            Recurring.None,
                            Status.OnHold
                        )
                    ),
                    messages = mutableListOf(
                        Message(id = 2, 1, sender = Person(2, "Alice", "Alice", "alice2@libero.it", isChecked = false, imageFile = null), content = "Hello, how are you?", timestamp = System.currentTimeMillis()),
                        Message(id = 3, 1,sender = Person(3, "Bob", "Bob", "bob@libero.it", isChecked = false, imageFile = null), content = "I'm good, thanks!", timestamp = System.currentTimeMillis()),
                        Message(id = 4, 1, sender = Person(4, "Charlie", "Charlie", "charlie@libero.it", isChecked = false, imageFile = null), content = "Great!", timestamp = System.currentTimeMillis()),
                        Message(id = 5, 2, sender = Person(5, "Eve", "Eve", "eve@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 6, 3, sender = Person(6, "Fred", "Fred", "fred@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 7, 4, sender = Person(7, "Grace", "Grace", "grace@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 8, 2, sender = Person(8, "Hazel", "Hazel", "hazel@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 9, 3, sender = Person(9, "Ian", "Ian", "ian@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                        Message(id = 10, 1, sender = Person(1, "Alice", "Reyes", "alice@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
                    ),
                    creationDate = Date()
                )
            )
        )

        val teamsList: StateFlow<List<Team>> = _teamsList // public counterpart

    //todo: db access write
        fun addTeam(team: Team) {
            _teamsList.value += team
        }

    //todo: db access write
        fun removeTeam(team: Team) {
            _teamsList.value -= team
        }
    //supposing these are the only persons in the app, a new person is added to the list when a new user is created
    val persons = listOf(
        Person(1, "John", "Doe", "john.doe@example.com", false, null),
        Person(2, "Jane", "Doe", "jane.doe@example.com", false, null),
        Person(3, "Alice", "Smith", "alice.smith@example.com", false, null),
        Person(4, "Bob", "Johnson", "bob.johnson@example.com", false, null),
        Person(5, "Charlie", "Williams", "charlie.williams@example.com", false, null),
        Person(6, "David", "Brown", "david.brown@example.com", false, null),
        Person(7, "Eve", "Jones", "eve.jones@example.com", false, null),
        Person(8, "Frank", "Miller", "frank.miller@example.com", false, null),
        Person(9, "Grace", "Davis", "grace.davis@example.com", false, null),
        Person(10, "Hank", "Garcia", "h.garcia@example.com", false, null),
        Person(11, "Hank", "Chavez", "hank@example.com", false, null),
        Person(12, "Toy", "Example", "email@example.com", false, null),
        Person(13, "Name", "Surname", "h@example.com", false, null),
        Person(14, "Dude", "Bro", "test@example.com", false, null),
        Person(15, "Added", "Person", "hank.garcia@example.com", false, null)
    )

    // Team properties
    private val _teamNameValue = MutableStateFlow("")
    val teamNameValue: StateFlow<String> get() = _teamNameValue

    private val _teamNameError = MutableStateFlow("")
    val teamNameError: StateFlow<String> get() = _teamNameError

    private val _teamDescriptionValue = MutableStateFlow("")
    val teamDescriptionValue: StateFlow<String> get() = _teamDescriptionValue

    private val _teamCategoryValue = MutableStateFlow("")
    val teamCategoryValue: StateFlow<String> get() = _teamCategoryValue

    private val _teamCategoryError = MutableStateFlow("")
    val teamCategoryError: StateFlow<String> get() = _teamCategoryError

    private val _teamImage = MutableStateFlow<ImageBitmap?>(null)
    val teamImageFile: StateFlow<ImageBitmap?> = _teamImage

    private val _teamRoles = MutableStateFlow<Map<Long, Role>>(emptyMap())
    val teamRoles: StateFlow<Map<Long, Role>> get() = _teamRoles

    // Methods to update state
    fun setTeamName(name: String) {
        _teamNameValue.value = name
        _teamNameError.value = if (name.isBlank()) "Team name cannot be empty" else ""
    }
    fun checkTeamName() {
        if(_teamNameValue.value.isBlank()){
            _teamNameError.value = "Team name cannot be empty"
        }
        else{
            ""
        }
    }
    fun checkTeamCategory() {
        if(_teamCategoryValue.value.isBlank()){
            _teamCategoryError.value = "Team category cannot be empty"
        }
        else{
            ""
        }
    }

    fun setTeamDescription(description: String) {
        _teamDescriptionValue.value = description
    }

    fun setTeamCategory(category: String) {
        _teamCategoryValue.value = category
        _teamCategoryError.value = if (category.isBlank()) "Category cannot be empty" else ""
    }

    fun setTeamImageFile(imageFile: ImageBitmap) {
        _teamImage.value = (imageFile ?: 0) as ImageBitmap?
    }

    fun setTeamRoles(roles: Map<Long, Role>) {
        _teamRoles.value = roles
    }

    fun removeTeamMember(personId: Long) {
        _teamRoles.value = _teamRoles.value.toMutableMap().apply { remove(personId) }
    }

    fun setRole(personId: Long, role: Role) {
        _teamRoles.value = _teamRoles.value.toMutableMap().apply { this[personId] = role }
    }

    //todo: db access read
    fun fetchTeamData(teamId: Long) {
        val team = getTeamById(teamId)
        team?.let {
            _teamNameValue.value = it.name
            _teamDescriptionValue.value = it.description
            _teamCategoryValue.value = it.category
            _teamImage.value = it.teamImage
            _teamRoles.value = it.roles ?: mutableMapOf()
        }
    }

    //todo: db access read
    fun getTeamById(id: Long): Team? {
        return _teamsList.value.find { it.id == id }
    }

    //todo: db access read
    fun getTeamMembers(team: Team): Map<Person, Role> {
        val result = mutableMapOf<Person, Role>()
        for ((personId, role) in team.roles) {
            val person = persons.find { it.id == personId }
            if (person != null) {
                result[person] = role
            }
        }
        return result
    }

    fun addTeamMember(teamId: Long, person: Person, role: Role) {
        /* look for team id in teamsList then add the person*/
        val team = _teamsList.value.find { it.id == teamId }
        if (team != null) {
            team.roles[person.id] = role
        }
    }

    //todo: db access read
    fun getPersonById(personId: Long): Person? {
        return persons.firstOrNull { it.id == personId }
    }

    //TEAM IMAGES
    val teamImages = listOf(
        R.drawable.image1,
        R.drawable.image2,
        R.drawable.image3,
        R.drawable.image4,
        )

    //todo: db access write
    fun saveEditTeam(teamId: Long) {
        if (teamNameError.value.isBlank() && teamCategoryError.value.isBlank()) {
        val teamIndex = _teamsList.value.indexOfFirst { it.id == teamId }
        if (teamIndex != -1) {
            // Update the team details
            val updatedTeam = _teamsList.value[teamIndex].copy(
                name = _teamNameValue.value,
                teamImage = _teamImage.value,
                description = _teamDescriptionValue.value,
                category = _teamCategoryValue.value,
                roles = _teamRoles.value.toMutableMap()
            )
            // Update the team in the list
            val updatedList = _teamsList.value.toMutableList().apply {
                this[teamIndex] = updatedTeam
            }
            _teamsList.value = updatedList
        }
            resetFields()
    }}

    // Cancel edited team
    fun cancelEditTeam(teamId: Long) {
        fetchTeamData(teamId) // Reset to original state
        resetFields()
    }


    private val _isCreateBottomSheetOpen = MutableStateFlow(false)
    val isCreateBottomSheetOpen: StateFlow<Boolean> = _isCreateBottomSheetOpen

    fun openCreateBottomSheet() {
        _isCreateBottomSheetOpen.value = true
    }

    fun closeCreateBottomSheet() {
        _isCreateBottomSheetOpen.value = false
    }


    private fun generateUniqueId(existingIds: Set<Long>): Long {
        var newId: Long
        do {
            newId = (1..Long.MAX_VALUE).random()
        } while (newId in existingIds)
        return newId
    }

    fun resetFields() {
        _teamNameValue.value = ""
        _teamImage.value = null
        _teamDescriptionValue.value = ""
        _teamCategoryValue.value = ""
        _teamNameError.value= ""
        _teamCategoryError.value= ""
        _teamRoles.value = emptyMap()
        _newMemberEmailError.value = ""
    }

    //todo: db access write
    fun createTeam(
        pendingMembers: List<Pair<String, Role>>
    ): Team? {
        if (_teamNameValue.value.isBlank() || _teamCategoryValue.value.isBlank()) {
            return null
        }

        val existingIds = _teamsList.value.map { it.id }.toSet()
        val newTeamId = generateUniqueId(existingIds)

        val creationDate = Date()
        val tasks = mutableListOf<Task>()

        val newTeam = Team(
            id = newTeamId,
            name = _teamNameValue.value,
            teamImage = _teamImage.value,
            description = _teamDescriptionValue.value,
            category = _teamCategoryValue.value,
            roles = _teamRoles.value.toMutableMap(),
            tasks = tasks.toMutableList(),
                messages = mutableListOf(),
            creationDate = creationDate

             )
            // Add the new team to the list
            _teamsList.value += newTeam
            resetFields()

        pendingMembers.forEach { (email, role) ->
            addMemberToTeam(newTeamId, email, role)
        }

        return newTeam
    }


    fun cancelNewTeam() {
       resetFields()
    }



    private val _messages = MutableStateFlow<List<Message>>(listOf(
        Message(id = 2, 1, sender = Person(2, "Alice", "Alice", "alice@libero.it", isChecked = false, imageFile = null), content = "Hello, how are you?", timestamp = System.currentTimeMillis()),
        Message(id = 3, 1,sender = Person(3, "Bob", "Bob", "bob@libero.it", isChecked = false, imageFile = null), content = "I'm good, thanks!", timestamp = System.currentTimeMillis()),
        Message(id = 4, 1, sender = Person(4, "Charlie", "Charlie", "charlie@libero.it", isChecked = false, imageFile = null), content = "Great!", timestamp = System.currentTimeMillis()),
        Message(id = 5, 2, sender = Person(5, "Eve", "Eve", "eve@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
        Message(id = 6, 3, sender = Person(6, "Fred", "Fred", "fred@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
        Message(id = 7, 4, sender = Person(7, "Grace", "Grace", "grace@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
        Message(id = 8, 2, sender = Person(8, "Hazel", "Hazel", "hazel@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
        Message(id = 9, 3, sender = Person(9, "Ian", "Ian", "ian@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),
        Message(id = 10, 1, sender = Person(10, "Jack", "Jack", "jack@libero.it", isChecked = false, imageFile = null), content = "Glad to hear!", timestamp = System.currentTimeMillis()),

        ))
    val messages: StateFlow<List<Message>> = _messages

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    //todo: db access write
    fun sendMessage(message: Message) {
        _messages.update { it + message }
        _unreadCount.value += 1
    }

    fun markMessagesAsRead() {
        _unreadCount.value = 0
    }


    //todo: db access read
    fun getPersonsFromTeam(team: Team): List<Person> {
        val list = mutableListOf<Person>()
        team.roles.forEach { role ->
            val person = personList.find { person -> person.id == role.key }
            if (person != null) {
                list.add(person)
            }
        }
        return list
    }




    //INVITE MEMBER
    private val _newMemberEmail = MutableStateFlow("")
    val newMemberEmail: StateFlow<String> get() = _newMemberEmail

    private val _newMemberEmailError = MutableStateFlow("")
    val newMemberEmailError: StateFlow<String> get() = _newMemberEmailError

    private val _selectedRole = MutableStateFlow<Role?>(null)
    val selectedRole: StateFlow<Role?> get() = _selectedRole

    private val _isAddMembersBottomSheetExpanded = MutableStateFlow(false)
    val isAddMembersBottomSheetExpanded: StateFlow<Boolean> get() = _isAddMembersBottomSheetExpanded

    private val _teamMembers = MutableStateFlow<Map<Person, Role>>(emptyMap())
    val teamMembers: StateFlow<Map<Person, Role>> get() = _teamMembers

    fun updateNewMemberEmail(email: String) {
        _newMemberEmail.value = email
    }

    fun checkNewMemberEmail() {
        _newMemberEmailError.value = if (_newMemberEmail.value.isEmpty()) {
            "Email cannot be empty"
        } else if (!_newMemberEmail.value.isEmailValid()) {
            "Invalid email"
        } else {
            ""
        }
    }

    fun selectRole(role: Role) {
        _selectedRole.value = role
    }

    fun deselectRole() {
        _selectedRole.value = null
    }

    fun openAddMembersBottomSheet() {
        _isAddMembersBottomSheetExpanded.value = true
    }

    fun closeAddMembersBottomSheet() {
        _isAddMembersBottomSheetExpanded.value = false
    }


    // Assuming this is a placeholder for the actual implementation
    private fun addMemberToTeamInternal(teamId: Long, email: String, role: Role): AddingMemberErrorType {
        // Implement the logic to add a member to the team and return the appropriate result
        return AddingMemberErrorType.SUCCESS // Example result
    }

    // Assuming this is a placeholder for the actual implementation
    fun getTeamMembers(teamId: Long): Map<Person, Role> {
        // Implement the logic to get the team members for the given teamId
        return emptyMap() // Example result
    }

    // Extension function to validate email addresses
    private fun String.isEmailValid(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }


}
