package com.mavi.ui.fragments

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mavi.databinding.FragmentCallBinding
import com.mavi.service.CallService
import com.mavi.ui.viewmodel.CallViewModel

class CallFragment : Fragment() {

    private lateinit var binding: FragmentCallBinding
    private val callViewModel: CallViewModel by viewModels()
    private var callService: CallService? = null
    private var isServiceBound = false
    private val handler = Handler(Looper.getMainLooper())
    private var callDurationRunnable: Runnable? = null
    private var isMuted = false
    private var isSpeaker = false
    private var callId: String? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as CallService.CallServiceBinder
            callService = binder.getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            callService = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Display on lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            requireActivity().window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        }

        arguments?.let {
            callId = it.getString("call_id")
            binding.callerNameTextView.text = it.getString("caller_name", "Unknown")
        }

        requestPermissions()
        bindCallService()
        setupListeners()
        setupObservers()
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.WAKE_LOCK
        )

        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(permission),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun bindCallService() {
        val intent = Intent(requireContext(), CallService::class.java)
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun setupListeners() {
        binding.acceptButton.setOnClickListener {
            callId?.let { id ->
                callViewModel.acceptCall(id)
                startCallDurationTimer()
                startCallService()
            }
        }

        binding.rejectButton.setOnClickListener {
            callId?.let { id ->
                callViewModel.rejectCall(id)
                stopCallService()
            }
        }

        binding.endButton.setOnClickListener {
            callId?.let { id ->
                callViewModel.endCall(id)
                stopCallDurationTimer()
                stopCallService()
            }
        }

        binding.muteButton.setOnClickListener {
            isMuted = !isMuted
            toggleMute(isMuted)
            binding.muteButton.text = if (isMuted) "Unmute" else "Mute"
        }

        binding.speakerButton.setOnClickListener {
            isSpeaker = !isSpeaker
            toggleSpeaker(isSpeaker)
            binding.speakerButton.text = if (isSpeaker) "Speaker Off" else "Speaker On"
        }
    }

    private fun setupObservers() {
        callViewModel.currentCall.observe(viewLifecycleOwner) { call ->
            if (call != null) {
                binding.callStatusTextView.text = call.status
                when (call.status) {
                    "accepted" -> {
                        binding.acceptButton.visibility = View.GONE
                        binding.rejectButton.visibility = View.GONE
                        binding.endButton.visibility = View.VISIBLE
                        binding.muteButton.visibility = View.VISIBLE
                        binding.speakerButton.visibility = View.VISIBLE
                    }
                    "ended" -> {
                        stopCallDurationTimer()
                        stopCallService()
                    }
                }
            }
        }

        callViewModel.callDuration.observe(viewLifecycleOwner) { duration ->
            val minutes = duration / 60
            val seconds = duration % 60
            binding.durationTextView.text = String.format("%02d:%02d", minutes, seconds)
        }

        callViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCallDurationTimer() {
        callDurationRunnable = object : Runnable {
            override fun run() {
                callViewModel.updateCallDuration()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(callDurationRunnable!!)
    }

    private fun stopCallDurationTimer() {
        callDurationRunnable?.let {
            handler.removeCallbacks(it)
        }
    }

    private fun startCallService() {
        val intent = Intent(requireContext(), CallService::class.java).apply {
            action = "START_CALL"
        }
        requireContext().startService(intent)
    }

    private fun stopCallService() {
        val intent = Intent(requireContext(), CallService::class.java).apply {
            action = "END_CALL"
        }
        requireContext().startService(intent)
    }

    private fun toggleMute(isMuted: Boolean) {
        val intent = Intent(requireContext(), CallService::class.java).apply {
            action = if (isMuted) "MUTE" else "UNMUTE"
        }
        requireContext().startService(intent)
    }

    private fun toggleSpeaker(isSpeaker: Boolean) {
        val intent = Intent(requireContext(), CallService::class.java).apply {
            action = if (isSpeaker) "SPEAKER_ON" else "SPEAKER_OFF"
        }
        requireContext().startService(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopCallDurationTimer()
        stopCallService()
        if (isServiceBound) {
            requireContext().unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}
