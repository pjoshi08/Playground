package com.pj.playground.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pj.playground.data.LoggerDataSource
import com.pj.playground.databinding.FragmentButtonsBinding
import com.pj.playground.di.InMemoryLogger
import com.pj.playground.navigator.AppNavigator
import com.pj.playground.navigator.Screens
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ButtonsFragment : Fragment() {

    @InMemoryLogger
    @Inject lateinit var logger: LoggerDataSource
    @Inject lateinit var navigator: AppNavigator

    private lateinit var binding: FragmentButtonsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentButtonsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        with(binding) {
            button1.setOnClickListener {
                logger.addLog("Interaction with 'Button 1'")
            }

            button2.setOnClickListener {
                logger.addLog("Interaction with 'Button 2'")
            }

            button3.setOnClickListener {
                logger.addLog("Interaction with 'Button 3'")
            }

            allLogs.setOnClickListener {
                navigator.navigateTo(Screens.LOGS)
            }

            deleteLogs.setOnClickListener {
                logger.removeLogs()
            }
        }
    }
}