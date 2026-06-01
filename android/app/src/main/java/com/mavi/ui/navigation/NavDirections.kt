import android.content.Intent

val LoginFragmentDirections.Companion.actionLoginFragmentToRegisterFragment: ()
    get() = { }

val LoginFragmentDirections.Companion.actionLoginFragmentToChatListFragment: ()
    get() = { }

val RegisterFragmentDirections.Companion.actionRegisterFragmentToLoginFragment: ()
    get() = { }

val ChatListFragmentDirections.Companion.actionChatListFragmentToChatDetailFragment: (Int, String) -> Bundle
    get() = { userId, username ->
        Bundle().apply {
            putInt("user_id", userId)
            putString("username", username)
        }
    }
