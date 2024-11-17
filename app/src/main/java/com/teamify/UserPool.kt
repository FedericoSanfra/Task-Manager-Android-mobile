package com.teamify

//Used to set a fake logged in user among the ones present

object UserPool {
    private val users = listOf(
        Person(1, "John", "Doe", "john.doe@example.com", false, null),
        Person(2, "Jane", "Doe", "jane.doe@example.com", false, null),
        Person(3, "Alice", "Smith", "alice.smith@example.com", false, null),
        Person(4, "Bob", "Johnson", "bob.johnson@example.com", false, null),
        Person(
            5,
            "Charlie",
            "Williams",
            "charlie.williams@example.com",
            false,
            null
        ),
        Person(6, "David", "Brown", "david.brown@example.com", false, null),
        Person(7, "Eve", "Jones", "eve.jones@example.com", false, null),
        Person(
            8,
            "Frank",
            "Miller",
            "frank.miller@example.com",
            false,
            null
        ),
        Person(9, "Grace", "Davis", "grace.davis@example.com", false, null),
        Person(10, "Hank", "Garcia", "hank.garcia@example.com", false, null),
        Person(11, "Hank", "Garcia", "hank.garcia@example.com", false, null),
        Person(12, "Hank", "Garcia", "hank.garcia@example.com", false, null),
        Person(13, "Hank", "Garcia", "hank.garcia@example.com", false, null),
        Person(14, "Hank", "Garcia", "hank.garcia@example.com", false, null),
        Person(15, "Added", "Person", "hank.garcia@example.com", false, null)
    )

    fun getRandomUser(): Person {
        return users.random()
    }
}