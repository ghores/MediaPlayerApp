package com.example.mediaplayerapp.presentation.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.mediaplayerapp.adapter.ViewPagerAdapter
import com.example.mediaplayerapp.databinding.FragmentViewPagerBinding
import com.example.mediaplayerapp.presentation.allmusic.AllMusicFragment
import com.example.mediaplayerapp.presentation.favorite.FavoriteMusicFragment
import com.google.android.material.tabs.TabLayout

class ViewPagerFragment : Fragment() {

    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(
            mutableListOf(AllMusicFragment(), FavoriteMusicFragment()),
            childFragmentManager,
            viewLifecycleOwner.lifecycle
        )

        binding.viewPager.adapter = adapter
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("تمامی آهنگ ها"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("آهنگ های مورد علاقه"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab?.position ?: 0
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
            override fun onTabReselected(tab: TabLayout.Tab?) = Unit
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
