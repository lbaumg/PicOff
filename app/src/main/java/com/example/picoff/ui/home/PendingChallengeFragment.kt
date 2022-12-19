package com.example.picoff.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picoff.MainViewModel
import com.example.picoff.R
import com.example.picoff.adapters.PendingChallengesAdapter
import com.example.picoff.databinding.FragmentPendingChallengeBinding
import kotlinx.coroutines.launch


class PendingChallengeFragment : Fragment() {

    private var _binding: FragmentPendingChallengeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var rvPendingChallenges: RecyclerView
    private lateinit var tvLoadingPendingChallenges: TextView

    private val pendingChallengesAdapter = PendingChallengesAdapter()

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPendingChallengeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        rvPendingChallenges = root.findViewById(R.id.rvPendingChallenges)
        rvPendingChallenges.layoutManager = LinearLayoutManager(context)
        rvPendingChallenges.setHasFixedSize(true)

        tvLoadingPendingChallenges = root.findViewById(R.id.tvLoadingPendingChallenges)

        rvPendingChallenges.adapter = pendingChallengesAdapter

        pendingChallengesAdapter.setOnItemClickListener(object :
            PendingChallengesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // TODO add on click handling
            }
        })

        // Observe mainViewModel.pendingChallengesList and update recycler view on change
        lifecycleScope.launch {
            mainViewModel.pendingChallengesList.collect {
                pendingChallengesAdapter.updatePendingChallengeList(it)
                rvPendingChallenges.visibility = View.VISIBLE
                tvLoadingPendingChallenges.visibility = View.GONE
            }
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class ActiveFragment {
    RECEIVED, SENT
}
