package com.insa.mygamelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import com.insa.mygamelist.data.Cover
import com.insa.mygamelist.data.Game
import com.insa.mygamelist.data.Genre
import com.insa.mygamelist.data.IGDBCoroutineService
import com.insa.mygamelist.data.Logo
import com.insa.mygamelist.data.Platform
import com.insa.mygamelist.ui.theme.MyGamesListTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    // sets up the main activity of the application.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Upload data from JsonFile
        // IGDB.load(this)
        // Fetch data from API
        // Active fullscreen display
        enableEdgeToEdge()
        // Call setContent to display the interface using JetPack Compose
        setContent {
            MyGamesListTheme {
                // Create a single instance of the ViewModel to be shared across screens
                // Since both screens share the same ViewModel instance, changes in one screen are immediately reflected in the other
                val viewModel: GameViewModel = viewModel()
                // initializes a NavController for managing navigation between screens
                val navController = rememberNavController() // Initialized NavController


                Scaffold(
                    modifier = Modifier.fillMaxSize()
                )
                { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = GameList, // Start screen
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<GameList> {
                            GameListScreen(navController = navController, viewModel = viewModel)
                        }
                        composable<GameDetail> { backStackEntry ->
                            GameDetailScreen(
                                gameId = backStackEntry.toRoute<GameDetail>().gameId,
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }

    @Serializable
    data object GameList

    @Serializable
    data class GameDetail(val gameId: Long)

    // Composable for a single game item
    // It is clickable, navigating to the GameDetail screen when clicked
    @Composable
    fun GameItem(
        game: Game,
        covers: List<Cover>,
        genres: List<Genre>,
        isFavorite: Boolean,
        onClick: () -> Unit,
        onFavoriteClick: () -> Unit
    ) {
        val lambdaCoverUrl = covers.find { it.id == game.cover }?.url?.let { "https:$it" }
            ?: "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg"
        val lambdaGenres = game.genres.mapNotNull { id -> genres.find { it.id == id }?.name }

        Row(
            modifier = Modifier
                .padding(8.dp) // Add padding around the entire row
                .fillMaxWidth()
                .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
                .clickable { onClick() }
                .border( // Optional: Add a border to make the rounded corners more visible
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(12.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = lambdaCoverUrl,
                contentDescription = game.name,
                modifier = Modifier
                    .padding(24.dp)
                    .size(80.dp) // Set a fixed size for the cover image
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = game.name,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
                Text(text = "Genres: ${lambdaGenres.joinToString(", ")}")
            }
            // Make sure the IconButton is clearly visible and clickable
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.Star,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color.Yellow else Color.Gray // Use gold color for filled star
                )
            }
        }
    }

    // This composable function displays a list of games from GameViewModel
    // Each game item is represented by the GameItem composable
    @Composable
    fun GameListScreen(navController: NavController, viewModel: GameViewModel) {
        val games by viewModel.games.collectAsState()
        val covers by viewModel.covers.collectAsState()
        val genres by viewModel.genres.collectAsState()
        val searchQuery by viewModel.searchQuery.collectAsState() // recent keyword from the GameViewModel
        val filteredGames by viewModel.filteredGames.collectAsState() // List of Game already filtered
        var isSearchVisible by rememberSaveable { mutableStateOf(false) } // Display or hide the search bar
        val favoriteGameIds by viewModel.favoriteGameIds.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = topAppBarColors(
                        containerColor = Color.Magenta,
                        titleContentColor = Color.Black,
                    ),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "My Games List",
                                modifier = Modifier.weight(1f) // Take up the rest of the space
                            )
                            IconButton(
                                onClick = { isSearchVisible = !isSearchVisible },
                                modifier = Modifier.padding(end = 8.dp) // Add padding to the right margin
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            // Assure that Search Bar and Game List won't be hidden by the TopAppBar
            Column(modifier = Modifier.padding(innerPadding)) {
                // Display the search bar if  isSearchVisible = true
                if (isSearchVisible) {
                    TextField(
                        value = searchQuery,
                        // when users enters sth, search Query will be updated thanks to setSearchQuery function
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search games...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        )
                    )
                }

                // Display "No match :(" screen only if the search query is not empty
                if (filteredGames.isEmpty() && searchQuery.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f), // Take up the rest of the space
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No match :(", fontSize = 24.sp)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        // iterate each game in the filteredGames List
                        items(filteredGames) { game ->
                            GameItem(
                                game = game,
                                covers = covers,
                                genres = genres,
                                isFavorite = favoriteGameIds.contains(game.id),
                                onClick = {
                                    navController.navigate(GameDetail(game.id))
                                },
                                onFavoriteClick = {
                                    viewModel.toggleFavorite(game.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    class GameViewModel : ViewModel() {
        private val _games = MutableStateFlow<List<Game>>(emptyList())
        val games: StateFlow<List<Game>> = _games
        private val _covers = MutableStateFlow<List<Cover>>(emptyList())
        val covers: StateFlow<List<Cover>> = _covers
        private val _genres = MutableStateFlow<List<Genre>>(emptyList())
        val genres: StateFlow<List<Genre>> = _genres
        private val _platforms = MutableStateFlow<List<Platform>>(emptyList())
        val platforms: StateFlow<List<Platform>> = _platforms
        private val _logos = MutableStateFlow<List<Logo>>(emptyList())
        val logos: StateFlow<List<Logo>> = _logos
        fun fetchAll() {
            viewModelScope.launch {
                val fetchedGames = IGDBCoroutineService.fetchGames(20)

                if (fetchedGames != null) {
                    _games.value = fetchedGames
                    _filteredGames.value = fetchedGames

                    fetchedGames.forEach { game ->
                        game.cover?.let { coverId ->
                            val fetchedCovers = IGDBCoroutineService.fetchCovers(coverId)
                            fetchedCovers?.let { covers ->
                                val existingCovers = _covers.value
                                val newCovers = covers.filter { cover ->
                                    existingCovers.none { it.id == cover.id }
                                }
                                if (newCovers.isNotEmpty()) {
                                    _covers.value += newCovers
                                }
                            }
                        }

                        if (game.genres.isNotEmpty()) {
                            val fetchedGenres = IGDBCoroutineService.fetchGenres(game.genres)
                            fetchedGenres?.let { genres ->
                                val existingGenres = _genres.value
                                val newGenres = genres.filter { genre ->
                                    existingGenres.none { it.id == genre.id }
                                }
                                if (newGenres.isNotEmpty()) {
                                    _genres.value += newGenres
                                }
                            }
                        }

                        if (game.platforms.isNotEmpty()) {
                            val fetchedPlatforms =
                                IGDBCoroutineService.fetchPlatforms(game.platforms)
                            fetchedPlatforms?.let { platforms ->
                                val existingPlatforms = _platforms.value
                                val newPlatforms = platforms.filter { platform ->
                                    existingPlatforms.none { it.id == platform.id }
                                }
                                if (newPlatforms.isNotEmpty()) {
                                    _platforms.value += newPlatforms

                                    newPlatforms.forEach { platform ->
                                        platform.platform_logo?.let { logoId ->
                                            val fetchedLogos =
                                                IGDBCoroutineService.fetchLogos(logoId)
                                            fetchedLogos?.let { logos ->
                                                _logos.value += logos
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        init {
            fetchAll()
        }

        private val _searchQuery = MutableStateFlow("")

        // Store the search keyword
        val searchQuery: StateFlow<String> get() = _searchQuery
        private val _filteredGames = MutableStateFlow<List<Game>>(_games.value)
        val filteredGames: StateFlow<List<Game>> get() = _filteredGames

        // When user enter sth, UI call setSearchQuery
        fun setSearchQuery(query: String) {
            _searchQuery.value = query // update _searchQuery.value
            _filteredGames.value =
                filterGames(query) // Binding the result to _filteredGames.value to update automatically
        }


        private fun filterGames(query: String): List<Game> {
            return if (query.isEmpty()) {
                _games.value // return the whole list of games
            } else {
                // filter list of games by checking the conditions
                _games.value.filter { game ->
                    game.name.contains(query, ignoreCase = true) ||
                            // Checks if any genre in game.genres matches the search query.
                            game.genres.any { genreId ->
                                _genres.value.find { it.id == genreId }?.name?.contains(
                                    query,
                                    ignoreCase = true
                                ) == true
                            } ||
                            game.platforms.any { platformId ->
                                _platforms.value.find { it.id == platformId }?.name?.contains(
                                    query,
                                    ignoreCase = true
                                ) == true
                            }
                }
            }
        }

        // Store favorite game IDs in a Set for efficient lookup
        private val _favoriteGameIds = MutableStateFlow<Set<Long>>(emptySet())
        val favoriteGameIds: StateFlow<Set<Long>> get() = _favoriteGameIds

        // Toggle favorite status of a game
        fun toggleFavorite(gameId: Long) {
            val currentFavorites = _favoriteGameIds.value
            _favoriteGameIds.value = if (currentFavorites.contains(gameId)) {
                currentFavorites - gameId // Remove gameId from favorites
            } else {
                currentFavorites + gameId // Add gameId to favorites
            }
        }
    }

    // Show detailed information about a selected game
    @Composable
    fun GameDetailScreen(gameId: Long, navController: NavController, viewModel: GameViewModel) {
        val games by viewModel.games.collectAsState()
        val covers by viewModel.covers.collectAsState()
        val genres by viewModel.genres.collectAsState()
        val platforms by viewModel.platforms.collectAsState()
        val logos by viewModel.logos.collectAsState()
        val game: Game? = games.find { it.id == gameId }
        val favoriteGameIds by viewModel.favoriteGameIds.collectAsState()
        val isFavorite = favoriteGameIds.contains(gameId) // boolean variable
        val coverUrl = covers.find { it.id == game?.cover }?.url?.let { "https:$it" } ?: " "
        // Retrieve the genres for the game by mapping genre IDs to genre names from the genres list (using mapNotNull)
        val genresGame =
            game?.genres?.mapNotNull { id -> genres.find { it.id == id }?.name } ?: emptyList()
        // Retrieve the platform logos for the game by mapping platform IDs to platform logos
        val platformLogos = game?.platforms?.mapNotNull { platformId ->
            platforms.find { it.id == platformId }?.platform_logo?.let { logoId ->
                logos.find { it.id == logoId }?.url?.let { "https:$it" }
                    ?: "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg?20200913095930"
            }
        } ?: emptyList()

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = topAppBarColors(
                        containerColor = Color.Magenta,
                        titleContentColor = Color.Black,
                    ),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = game?.name ?: "Unknown Game",
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.toggleFavorite(gameId) }
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.Star,
                                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                    tint = if (isFavorite) Color.Yellow else Color.Gray // Gold color for filled star
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Display the game's name
                Text(
                    text = game?.name ?: "Unknown",
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally) // Center align the text
                )
                // Add a spacer to create vertical space between the name and the cover image
                Spacer(modifier = Modifier.height(16.dp))
                // Display the game's cover image using AsyncImage
                AsyncImage(
                    model = coverUrl, // URL of the cover image
                    contentDescription = game?.name, // Accessibility description
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally) // Center align the image
                )
                // Add a spacer to create vertical space between the cover image and genres
                Spacer(modifier = Modifier.height(16.dp))
                // Display the game's genres
                Text(
                    text = "Genres: ${genresGame.joinToString(", ")}",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally) // Center align the text
                )
                // Add a spacer to create vertical space between genres and platform logos
                Spacer(modifier = Modifier.height(8.dp))
                // Display the list of platform logos
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth() // Make the LazyRow fill the maximum available width
                        .height(90.dp), // Set a fixed height for slider
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Add horizontal spacing between logos in the LazyRow
                ) {
                    // Iterate over the platformLogos list and display each logo
                    items(platformLogos) { logoUrl ->
                        AsyncImage(
                            model = logoUrl, // URL of the platform logo
                            contentDescription = "Platform Logo", // Accessibility description
                            modifier = Modifier
                                .size(70.dp) // Set a fixed size for the logos
                                .padding(4.dp) // Add padding arround each logo to create spacing
                        )
                    }
                }
                // Add a spacer to create vertical space between platform logos and summary
                Spacer(modifier = Modifier.height(16.dp))
                // Display the game's summary
                Text(
                    text = game?.summary ?: "No description available",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.Start) // Left align the text
                        .padding(12.dp)
                )
            }
        }
    }
}