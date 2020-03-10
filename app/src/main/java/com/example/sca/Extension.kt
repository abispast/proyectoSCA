package com.example.sca

import android.view.LayoutInflater
import android.view.ViewGroup

fun ViewGroup.inflate(id:Int)=LayoutInflater.from(this.context).inflate(id,this,false)