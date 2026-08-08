package com.govformai.indicqa.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.govformai.indicqa.engine.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// --- THEME ---
val AppPrimary = Color(0xFF2563EB)
val AppSecondary = Color(0xFF7C3AED)
val AppAccent = Color(0xFF06B6D4)
val AppBackground = Color(0xFFF8FAFC)
val AppSurface = Color(0xFFFFFFFF)

@Composable
fun GovFormTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = AppPrimary,
        secondary = AppSecondary,
        tertiary = AppAccent,
        background = AppBackground,
        surface = AppSurface
    )
    MaterialTheme(colorScheme = colorScheme, content = content)
}

// --- NAVIGATION ---
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Upload : Screen("upload")
    object DocViewer : Screen("viewer/{docId}") {
        fun createRoute(docId: String) = "viewer/$docId"
    }
    object Chat : Screen("chat/{docId}") {
        fun createRoute(docId: String) = "chat/$docId"
    }
    object Benchmark : Screen("benchmark")
    object Results : Screen("results")
}

@Composable
fun GovFormApp() {
    val navController = rememberNavController()
    val qaHistory = remember { mutableStateListOf<QAResponse>() }

    GovFormTheme {
        NavHost(navController = navController, startDestination = Screen.Splash.route) {
            composable(Screen.Splash.route) { SplashScreen(navController) }
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Upload.route) { UploadScreen(navController) }
            composable(Screen.DocViewer.route) { backStackEntry ->
                val docId = backStackEntry.arguments?.getString("docId")
                DocViewerScreen(navController, docId)
            }
            composable(Screen.Chat.route) { backStackEntry ->
                val docId = backStackEntry.arguments?.getString("docId")
                ChatScreen(navController, docId, onNewResponse = { qaHistory.add(0, it) })
            }
            composable(Screen.Benchmark.route) { BenchmarkScreen(navController) }
            composable(Screen.Results.route) { HistoryScreen(navController, qaHistory) }
        }
    }
}

// --- SCREENS ---

@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AppPrimary, AppSecondary))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(24.dp).fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "GovFormAI",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Offline Government Form Intelligence",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(48.dp))
            LinearProgressIndicator(
                modifier = Modifier.width(140.dp).clip(CircleShape),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    var helloTokenStatus by remember { mutableStateOf("Loading AI Core...") }
    
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            val status = NativeInferenceBridge.helloTokenTest()
            withContext(Dispatchers.Main) {
                helloTokenStatus = status
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("GovFormAI", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Results.route) }) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(onClick = { navController.navigate(Screen.Benchmark.route) }) {
                        Icon(Icons.Default.BarChart, contentDescription = "Analytics")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Hero Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AppPrimary)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(AppPrimary, AppSecondary))))
                        
                        Column(modifier = Modifier.padding(24.dp).align(Alignment.CenterStart)) {
                            Text("Welcome Back", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                            Text("Empowering Citizen\nIntelligence", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)) {
                                Text("100% Offline", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Text("Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Upload",
                        icon = Icons.Default.CloudUpload,
                        color = AppAccent,
                        onClick = { navController.navigate(Screen.Upload.route) }
                    )
                    ActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Scan",
                        icon = Icons.Default.QrCodeScanner,
                        color = AppSecondary,
                        onClick = { /* Demo */ }
                    )
                }
            }

            item {
                Text("AI Core Status", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Text(
                        text = helloTokenStatus,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF334155)
                    )
                }
            }

            item {
                Text("Sample Documents", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(FormEngine.documents) { doc ->
                DocumentSampleCard(doc) {
                    navController.navigate(Screen.DocViewer.createRoute(doc.id))
                }
            }

            item {
                ImpactSection()
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun ActionCard(modifier: Modifier, title: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun DocumentSampleCard(doc: DocumentSample, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(12.dp),
                color = AppPrimary.copy(alpha = 0.1f)
            ) {
                Icon(Icons.Default.Description, contentDescription = null, tint = AppPrimary, modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(doc.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(doc.category, color = Color.Gray, fontSize = 12.sp)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.LightGray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(navController: NavHostController) {
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableFloatStateOf(0f) }
    var fileName by remember { mutableStateOf("") }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                fileName = "Doc_${System.currentTimeMillis() % 1000}.pdf"
                isUploading = true
            }
        }
    )

    LaunchedEffect(isUploading) {
        if (isUploading) {
            while (uploadProgress < 1f) {
                delay(50)
                uploadProgress += 0.05f
            }
            delay(500)
            navController.navigate(Screen.DocViewer.createRoute("sample-memo"))
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Upload Document", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!isUploading) {
                Surface(
                    modifier = Modifier.size(200.dp).clickable { launcher.launch(arrayOf("application/pdf", "image/*")) },
                    shape = RoundedCornerShape(32.dp),
                    color = AppPrimary.copy(alpha = 0.05f),
                    border = BorderStroke(2.dp, AppPrimary.copy(alpha = 0.3f))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(64.dp), tint = AppPrimary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Upload PDF / Image", color = AppPrimary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
                Text("Supports PDF, JPG, PNG", color = Color.Gray, fontSize = 14.sp)
            } else {
                Text("Processing $fileName", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(20.dp))
                LinearProgressIndicator(
                    progress = { uploadProgress },
                    modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                    color = AppPrimary,
                    trackColor = AppPrimary.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Running Offline OCR...", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocViewerScreen(navController: NavHostController, docId: String?) {
    val doc = FormEngine.documents.find { it.id == docId } ?: FormEngine.documents[0]
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Preview", "Text", "Insights")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Document Info", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(Screen.Chat.createRoute(doc.id)) },
                containerColor = AppPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ask Questions")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab, containerColor = AppBackground) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
            
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                when (selectedTab) {
                    0 -> { // Preview
                        Card(
                            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.AutoMirrored.Filled.InsertDriveFile, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Visual Preview", color = Color.Gray)
                                }
                            }
                        }
                    }
                    1 -> { // Text
                        Card(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(16.dp)) {
                            SelectionContainer {
                                Text(
                                    text = doc.rawContentBoilerplate,
                                    modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    2 -> { // Insights
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            InsightCard("Detected Language", "English / Hindi", Icons.Default.Language, AppPrimary)
                            InsightCard("Entities Found", "5 Clauses, 2 Dates", Icons.AutoMirrored.Filled.Label, AppSecondary)
                            InsightCard("Complexity", "Standard", Icons.Default.Speed, AppAccent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InsightCard(label: String, value: String, icon: ImageVector, color: Color) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.05f))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, fontSize = 12.sp, color = Color.Gray)
                Text(value, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController, docId: String?, onNewResponse: (QAResponse) -> Unit) {
    val doc = FormEngine.documents.find { it.id == docId } ?: FormEngine.documents[0]
    var questionInput by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var selectedLang by remember { mutableStateOf(FormEngine.supportedLanguages[0]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(doc.title, maxLines = 1, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("AI Assistant", fontSize = 11.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(AppBackground)) {
            // Language Picker in Chat
            LazyRow(modifier = Modifier.fillMaxWidth().background(Color.White).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(FormEngine.supportedLanguages) { lang ->
                    val isSel = lang.code == selectedLang.code
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedLang = lang },
                        label = { Text(lang.nativeName, fontSize = 11.sp) }
                    )
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(msg)
                }
            }

            Column(modifier = Modifier.background(Color.White).padding(bottom = 8.dp)) {
                LazyRow(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val suggestions = doc.sampleQuestions[selectedLang.code] ?: doc.sampleQuestions["en"] ?: emptyList()
                    items(suggestions) { suggestion ->
                        SuggestionChip(onClick = { questionInput = suggestion }, label = { Text(suggestion, fontSize = 11.sp) })
                    }
                }

                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Voice */ }) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = AppPrimary)
                    }
                    OutlinedTextField(
                        value = questionInput,
                        onValueChange = { questionInput = it },
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        placeholder = { Text("Ask in ${selectedLang.nativeName}...") },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )
                    FloatingActionButton(
                        onClick = {
                            if (questionInput.isNotBlank()) {
                                val q = questionInput
                                messages.add(ChatMessage(q, true))
                                questionInput = ""
                                scope.launch {
                                    listState.animateScrollToItem(messages.size - 1)
                                    delay(800)
                                    val resp = FormEngine.evaluateQuestion(doc, q, selectedLang.code, 10)
                                    messages.add(ChatMessage(resp.answerText, false, resp))
                                    onNewResponse(resp)
                                    listState.animateScrollToItem(messages.size - 1)
                                }
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        containerColor = AppPrimary,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

data class ChatMessage(val text: String, val isUser: Boolean, val response: QAResponse? = null)

@Composable
fun ChatBubble(msg: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (msg.isUser) AppPrimary else Color.White,
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (msg.isUser) 20.dp else 4.dp,
                bottomEnd = if (msg.isUser) 4.dp else 20.dp
            ),
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = msg.text,
                    color = if (msg.isUser) Color.White else Color.Black,
                    fontSize = 14.sp
                )
                msg.response?.let { resp ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(color = Color.Black.copy(alpha = 0.05f), shape = RoundedCornerShape(8.dp)) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PinDrop, contentDescription = null, modifier = Modifier.size(12.dp), tint = AppPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(resp.clausePointer, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AppPrimary)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenchmarkScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("System Performance", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState())) {
            Text("Hardware Acceleration", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Snapdragon 662 / Exynos 850 Profile", color = Color.Gray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(modifier = Modifier.weight(1f), "TTFT", "185ms", Icons.Default.Timer, AppPrimary)
                StatCard(modifier = Modifier.weight(1f), "Tokens/sec", "18.5", Icons.Default.Bolt, AppAccent)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(modifier = Modifier.weight(1f), "RAM (RSS)", "780MB", Icons.Default.Memory, AppSecondary)
                StatCard(modifier = Modifier.weight(1f), "Quantization", "Q4_K_M", Icons.Default.Layers, Color(0xFF64748B))
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            Text("Inference Distribution", fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth().height(140.dp).padding(top = 12.dp), shape = RoundedCornerShape(20.dp)) {
                Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
                    Text("Analytics Chart View", color = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = color)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavHostController, history: List<QAResponse>) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Query History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No queries in this session", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
                items(history) { resp ->
                    HistoryItem(resp)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(resp: QAResponse) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = AppPrimary.copy(alpha = 0.1f), shape = CircleShape) {
                    Text(resp.languageCode.uppercase(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AppPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(resp.clausePointer, fontSize = 11.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(resp.answerText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun ImpactSection() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Why GovFormAI?", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            ImpactRow(Icons.Default.WifiOff, "Works 100% Offline")
            ImpactRow(Icons.Default.Translate, "7+ Indic Languages")
            ImpactRow(Icons.Default.PhoneAndroid, "Low-End Device Optimized")
            ImpactRow(Icons.AutoMirrored.Filled.FactCheck, "Verifiable Clause Pointers")
        }
    }
}

@Composable
fun ImpactRow(icon: ImageVector, text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = AppAccent, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
    }
}

// --- MOCK MODIFIER HELPERS ---
fun Modifier.size(size: androidx.compose.ui.unit.Dp) = this.width(size).height(size)
