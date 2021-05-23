package com.wolfsea.defineviewgroupdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.wolfsea.defineviewgroupdemo.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var dataBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        define_vg.itemCountPerRow = 2
        define_vg.setDataSourceByNewView(createDataSet(12))

        dataBinding.roundRectPb.updateProgress(45F)
    }

    override fun onDestroy() {

        super.onDestroy()
        dataBinding.unbind()
    }

    private fun createDataSet(itemCount: Int): MutableList<String> {

        val dataSet = mutableListOf<String>()

        for (i in 0 until itemCount) {

            dataSet.add("${i + 11111100000}")
        }

        return dataSet
    }
}