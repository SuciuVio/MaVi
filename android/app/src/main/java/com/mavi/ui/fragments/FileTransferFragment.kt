package com.mavi.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mavi.R
import com.mavi.data.local.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class FileTransferFragment : Fragment() {
    private lateinit var incomingListView: ListView
    private lateinit var outgoingListView: ListView
    private var incomingTransfers: MutableList<String> = mutableListOf()
    private var outgoingTransfers: MutableList<String> = mutableListOf()
    private lateinit var incomingAdapter: ArrayAdapter<String>
    private lateinit var outgoingAdapter: ArrayAdapter<String>
    private var currentUserId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_file_transfer, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        incomingListView = view.findViewById(R.id.incoming_transfers_list)
        outgoingListView = view.findViewById(R.id.outgoing_transfers_list)

        incomingAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, incomingTransfers)
        outgoingAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, outgoingTransfers)

        incomingListView.adapter = incomingAdapter
        outgoingListView.adapter = outgoingAdapter

        loadTransfers()
    }

    private fun loadTransfers() {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            
            launch {
                db.fileTransferDao().getIncomingTransfers(currentUserId).collectLatest { transfers ->
                    incomingTransfers.clear()
                    incomingTransfers.addAll(transfers.map { "${it.filename} - ${it.status}" })
                    incomingAdapter.notifyDataSetChanged()
                }
            }
            
            launch {
                db.fileTransferDao().getOutgoingTransfers(currentUserId).collectLatest { transfers ->
                    outgoingTransfers.clear()
                    outgoingTransfers.addAll(transfers.map { "${it.filename} - ${it.status}" })
                    outgoingAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
