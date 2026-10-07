package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.SupabaseApiService
import com.omsworld.familycare.data.remote.dto.SupabaseChatDto
import com.omsworld.familycare.data.remote.dto.SupabaseUserDto
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    @Named("supabase") private val supabase: SupabaseApiService
) {

    // ============================================================
    // GET CHAT LIST (user_chat_list.php)
    // Returns latest message per friend.
    // ============================================================
    suspend fun getChatList(userId: String): ApiResult<List<ChatListItem>> = safeApiCall {
        // Get all messages where I'm either sender or receiver
        val orFilter = "(user_id.eq.$userId,friend_id.eq.$userId)"
        val messages = supabase.getChatHistory(or = orFilter)

        // Group by friend and take the latest message
        val byFriend = messages
            .groupBy { if (it.userId == userId) it.friendId else it.userId }
            .mapValues { (_, msgs) -> msgs.maxByOrNull { it.sentAt ?: "" } }

        // Fetch friend user details
        byFriend.map { (friendId, lastMsg) ->
            val friend = supabase.getUserById(idEq = "eq.$friendId").firstOrNull()
            ChatListItem(
                friendId = friendId,
                friendFullName = friend?.userName ?: "Unknown",
                friendImg = friend?.userImg ?: "",
                friendPhone = friend?.mobileNo ?: "",
                message = lastMsg?.message ?: "",
                messageId = lastMsg?.messageId ?: "",
                senderId = lastMsg?.userId ?: "",
                time = lastMsg?.sentAt ?: "",
                type = lastMsg?.type ?: "text"
            )
        }
    }

    // ============================================================
    // GET CHAT DETAILS (user_chat_details.php)
    // All messages between me and a friend.
    // ============================================================
    suspend fun getChatDetails(
        userId: String,
        friendId: String
    ): ApiResult<List<ChatMessageItem>> = safeApiCall {
        // Two-directional filter
        val orFilter = "(and(user_id.eq.$userId,friend_id.eq.$friendId)," +
                "and(user_id.eq.$friendId,friend_id.eq.$userId))"
        val messages = supabase.getChatHistory(or = orFilter)

        messages.map {
            ChatMessageItem(
                messageId = it.messageId ?: "",
                senderId = it.userId,
                friendId = it.friendId,
                message = it.message ?: "",
                time = it.sentAt ?: "",
                type = it.type ?: "text"
            )
        }
    }

    // ============================================================
    // SEND CHAT (demo_user_chat.php)
    // ============================================================
    suspend fun sendChat(
        userId: String,
        friendId: String,
        message: String
    ): ApiResult<Unit> = safeApiCall {
        Timber.d("sendChat request: userId=$userId friendId=$friendId message=$message")
        val chat = SupabaseChatDto(
            messageId = null,
            userId = userId,
            friendId = friendId,
            message = message,
            type = "text",
            sentAt = nowIso()
        )
        Timber.d("sendChat payload: $chat")
        val response = supabase.sendChat(chat)
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: "(no error body)"
            Timber.e("sendChat failed: code=${response.code()} error=$errorBody")
            throw IllegalStateException("Send chat failed: ${response.code()} — $errorBody")
        }
    }

    private fun nowIso(): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .format(Date())
}

// Local data classes
data class ChatListItem(
    val friendId: String,
    val friendFullName: String,
    val friendImg: String,
    val friendPhone: String,
    val message: String,
    val messageId: String,
    val senderId: String,
    val time: String,
    val type: String
)

data class ChatMessageItem(
    val messageId: String,
    val senderId: String,
    val friendId: String,
    val message: String,
    val time: String,
    val type: String
)