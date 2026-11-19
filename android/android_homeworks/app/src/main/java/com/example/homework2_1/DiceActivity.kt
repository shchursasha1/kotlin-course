package com.example.homework2_1

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.homework2_1.databinding.ActivityDiceBinding

// View: no threads and no random numbers here, only UI
class DiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiceBinding
    private lateinit var imageViews: Array<ImageView>
    private val drawables = arrayOf(
        R.drawable.die_1, R.drawable.die_2,
        R.drawable.die_3, R.drawable.die_4,
        R.drawable.die_5, R.drawable.die_6
    )

    private val viewModel: DiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageViews = arrayOf(binding.die1, binding.die2, binding.die3, binding.die4, binding.die5)

        viewModel.diceValues.observe(this) { values ->
            for (i in values.indices) {
                // Value 1..6 -> index 0..5 in drawables
                imageViews[i].setImageResource(drawables[values[i] - 1])
            }
        }

        viewModel.isRolling.observe(this) { isRolling ->
            binding.rollButton.isEnabled = !isRolling
        }

        binding.rollButton.setOnClickListener {
            viewModel.rollDice()
        }
    }
}
