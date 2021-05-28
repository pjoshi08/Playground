package com.pj.playground.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pj.playground.data.LoggerDataSource
import com.pj.playground.databinding.FragmentLogsBinding
import com.pj.playground.di.InMemoryLogger
import com.pj.playground.util.DateFormatter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogsFragment : Fragment() {

    /**
     * Under the hood, [Hilt] will populate these fields in the onAttach() [lifecycle]
     * method with instances built in the dependencies [container] that Hilt automatically
     * generated for [LogsFragment].
     */
    @InMemoryLogger
    @Inject lateinit var logger: LoggerDataSource

    @Inject lateinit var dateFormatter: DateFormatter

    lateinit var binding: FragmentLogsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLogsBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        logger.getAllLogs { logs ->
            binding.recyclerView.adapter = LogsViewAdapter(
                logs,
                dateFormatter
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.setHasFixedSize(true)
    }
}