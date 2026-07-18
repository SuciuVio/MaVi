package com.mavi.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mavi.R
import com.mavi.data.repository.AuthRepository
import com.mavi.data.local.AppDatabase
import com.mavi.ui.viewmodel.AuthViewModel
import com.mavi.ui.viewmodel.AuthUiState
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class ChatListFragment : Fragment() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var usersListView: ListView
    private lateinit var logoutButton: Button
    private var users: MutableList<String> = mutableListOf()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_chat_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getInstance(requireContext())
        val authRepository = AuthRepository(db)
        authViewModel = ViewModelProvider(this, AuthViewModelFactory(authRepository))
            .get(AuthViewModel::class.java)

        usersListView = view.findViewById(R.id.users_list)
        logoutButton = view.findViewById(R.id.logout_button)

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, users)
        usersListView.adapter = adapter

        usersListView.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = users[position]
            val bundle = Bundle().apply {
                putString("userName", selectedUser)
            }
            findNavController().navigate(R.id.action_chatListFragment_to_chatDetailFragment, bundle)
        }

        logoutButton.setOnClickListener {
            authViewModel.logout()
            findNavController().navigate(R.id.action_chatListFragment_to_loginFragment)
        }

        loadUsers()
    }

    private fun loadUsers() {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val allUsers = db.userDao().getAllUsers()
            allUsers.collectLatest { userList ->
                users.clear()
                users.addAll(userList.map { it.username })
                adapter.notifyDataSetChanged()
            }
        }
    }
}
