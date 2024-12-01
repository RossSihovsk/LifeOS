package com.project.lifeos.ai

import co.touchlab.kermit.Logger
import com.project.lifeos.data.Duration
import com.project.lifeos.utils.getTomorrowDate
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Part(val text: String)

@Serializable
data class Content(val parts: List<Part>)

@Serializable
data class Candidate(val content: Content)

@Serializable
data class Error(val message: String)

@Serializable
data class GenerateContentResponse(val error: Error? = null, val candidates: List<Candidate>? = null)

@Serializable
data class GenerateContentRequest(val contents: Content)

@Serializable
data class TaskResponse(
    val title: String,
    val description: String,
    val dates: List<String>,
    val time: String,
    val priority: String,
)

class GeminiApi {
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models"
    private val apiKey = "AIzaSyBX3H2zIKrXPqyiAPnGc8AwxSGdW2JzzHQ"
    private val logger = Logger.withTag("GeminiApi")
    private val startDay: String = getTomorrowDate()

    @OptIn(ExperimentalSerializationApi::class)
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true; explicitNulls = false })
        }
    }

    suspend fun generateTasksForGoal(
        title: String,
        description: String,
        duration: Duration
    ): List<TaskResponse> {
        val prompt = """
            Create 4 tasks that will help achieve the goal: '$title'.
            Goal Description: '$description'.
            You should provide short task title(Max 3-4 words).
            You must provide task description, that might be a little bit longer(max 8-10 words).
            Provide list of specific dates for each task. Dates should be in next format: yyyy-MM-dd, like 2024-11-30, 2024-12-03.
            Dates should be latest than $startDay.
            Dates should be regular during entire period.
            You should create personal schedule for user, so tasks should repeats regular like twice a week, depend on the context.
            This schedule should ends in ${duration.title}
            You should also provide time when task should be done (19:00, etc.) In ms format.
            You should chose priority for each task from this values (High, Medium, Low).
            Return each task in the following JSON format strictly:
        [
            {
             "title": "Task title"
             "description": "Task description"
             "dates": ["YYYY-MM-DD", "YYYY-MM-DD"]
             "time": "HH:mm"
             "priority": "High/Medium/Low"
             }
        ]     
        """.trimIndent()

        val part = Part(text = prompt)
        val contents = Content(listOf(part))
        val request = GenerateContentRequest(contents)

        val response: GenerateContentResponse = client.post("$baseUrl/gemini-pro:generateContent") {
            contentType(ContentType.Application.Json)
            url { parameters.append("key", apiKey) }
            setBody(request)
        }.body()

        val resultList = mutableListOf<TaskResponse>()

        try {
            response.candidates?.flatMap { candidate ->
                candidate.content.parts.mapIndexed { _, part ->
                    val tasks: List<TaskResponse> = Json.decodeFromString(part.text.trimIndent())
                    logger.d("tasks: $tasks")
                    resultList.addAll(tasks)
                }
            }
        } catch (e: Exception) {
            logger.e("Exception: ${e.message}")
        }

        return resultList
    }
}