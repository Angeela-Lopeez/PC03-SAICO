package com.lopez.angela.laboratoriocalificado3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lopez.angela.laboratoriocalificado3.databinding.ItemTeacherBinding // Importante: esta clase se genera por ViewBinding

class TeacherAdapter(
    private var teachers: List<Teacher>,
    private val onItemClick: (Teacher) -> Unit,
    private val onItemLongClick: (Teacher) -> Unit
) : RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val binding = ItemTeacherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeacherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val teacher = teachers[position]
        holder.bind(teacher)
    }

    override fun getItemCount(): Int = teachers.size

    fun updateData(newTeachers: List<Teacher>) {
        teachers = newTeachers
        notifyDataSetChanged()
    }

    inner class TeacherViewHolder(private val binding: ItemTeacherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(teacher: Teacher) {
            binding.textViewName.text = teacher.name
            binding.textViewLastName.text = teacher.lastname

            Glide.with(binding.imageViewTeacher.context)
                .load(teacher.photoUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
                .into(binding.imageViewTeacher)

            binding.root.setOnClickListener {
                onItemClick(teacher)
            }

            binding.root.setOnLongClickListener {
                onItemLongClick(teacher)
                true
            }
        }
    }
}