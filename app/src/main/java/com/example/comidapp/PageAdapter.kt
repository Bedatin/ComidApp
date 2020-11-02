package com.example.comidapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class PageAdapter constructor(
    fm: FragmentManager,
    private var count: Int
) :
    FragmentPagerAdapter(fm) {



    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return Calendario()
            1 -> return Listado()
            else -> return Calendario()
        }
    }


    override fun getCount(): Int = count

}
