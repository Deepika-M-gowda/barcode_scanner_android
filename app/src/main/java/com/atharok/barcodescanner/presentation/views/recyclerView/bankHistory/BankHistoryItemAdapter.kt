/*
 * Barcode Scanner
 * Copyright (C) 2021  Atharok
 *
 * This file is part of Barcode Scanner.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.atharok.barcodescanner.presentation.views.recyclerView.bankHistory

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.databinding.RecyclerViewItemHistoryBankBinding
import com.atharok.barcodescanner.domain.entity.bank.Bank

class BankHistoryItemAdapter(private val callback: OnBankItemListener): RecyclerView.Adapter<BankHistoryItemHolder>() {

    interface OnBankItemListener {
        fun onItemClick(view: View?, bank: Bank)
        fun onItemSelect(view: View?, bank: Bank, isSelected: Boolean)
        fun isSelectedMode(): Boolean
    }

    private var items = listOf<BankHistoryItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankHistoryItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = RecyclerViewItemHistoryBankBinding.inflate(inflater, parent, false)
        return BankHistoryItemHolder(viewBinding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BankHistoryItemHolder, position: Int) {
        holder.update(items[position], callback)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(bankList: List<Bank>){
        items = bankList.map { BankHistoryItem(it) }
        this.notifyDataSetChanged()
    }
    
    fun getBank(position: Int): Bank = this.items[position].bank
}