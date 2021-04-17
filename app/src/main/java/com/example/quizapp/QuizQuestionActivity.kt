package com.example.quizapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat

class QuizQuestionActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var progressBar: ProgressBar
    private lateinit var progressView: TextView
    private lateinit var currentLinkView: TextView
    private lateinit var toFoundView: TextView

    private lateinit var optionOneView: TextView
    private lateinit var optionTwoView: TextView
    private lateinit var optionThreeView: TextView
    private lateinit var optionFourView: TextView
    private lateinit var optionFiveView: TextView
    private var options: ArrayList<TextView> = ArrayList()

    private lateinit var buttonNext: Button
    private lateinit var buttonPrevious: Button

    private var mQuestionsList:ArrayList<Question>? = null
    private var mSelectedPositionOption:Int = 0
    private var mTotalMoves:Int = 0
    private var mGoalTitle:String? = null
    private var mStartTitle:String? = null

    private lateinit var webParsing: WebParsing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)

        progressBar = findViewById(R.id.progessBar)
        progressView = findViewById(R.id.tv_progress)
        currentLinkView = findViewById(R.id.tv_current_link)
        toFoundView = findViewById(R.id.tv_to_found)

        optionOneView = findViewById(R.id.tv_option_one)
        optionTwoView = findViewById(R.id.tv_option_two)
        optionThreeView = findViewById(R.id.tv_option_three)
        optionFourView = findViewById(R.id.tv_option_four)
        optionFiveView = findViewById(R.id.tv_option_five)

        buttonNext = findViewById(R.id.btn_next)
        buttonPrevious = findViewById(R.id.btn_previous)

        mStartTitle = intent.getStringExtra(Constants.TITLE_START)
        mGoalTitle = intent.getStringExtra(Constants.TITLE_GOAL)

        mQuestionsList = Constants.getQuestions()

        options.add(optionOneView)
        options.add(optionTwoView)
        options.add(optionThreeView)
        options.add(optionFourView)
        options.add(optionFiveView)

        for(option in options) {
            option.setOnClickListener(this)
        }
        buttonNext.setOnClickListener(this)
        buttonPrevious.setOnClickListener(this)

        toFoundView.setText(toFoundView.text.toString() + mGoalTitle)
        webParsing = WebParsing(this)
        webParsing.getHtmlFromUrl(Constants.BASIC_LINK + mStartTitle, currentLinkView, options)
    }


    private fun defaultOptionsView(){
        val options = ArrayList<TextView>()
        options.add(optionFourView)
        options.add(optionThreeView)
        options.add(optionTwoView)
        options.add(optionOneView)

        for (option in options) {
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this,R.drawable.defulat_option_border_bg)
        }


    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_option_one-> {
                selectedOptionView(optionOneView, 1)
            }
            R.id.tv_option_two-> {
                selectedOptionView(optionTwoView, 2)
            }
            R.id.tv_option_three-> {
                selectedOptionView(optionThreeView, 3)
            }
            R.id.tv_option_four-> {
                selectedOptionView(optionFourView, 4)
            }
            R.id.btn_next-> {
                webParsing.setNextLinks(options)
            }
            R.id.btn_previous-> {
                webParsing.setPreviousLinks(options)
            }
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int)
    {
        defaultOptionsView()
        mSelectedPositionOption = selectedOptionNum

        tv.setTextColor(Color.parseColor("#363A43"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this,R.drawable.selected_option_border_bg)

        webParsing.getHtmlFromUrl("https://en.wikipedia.org/wiki/" + tv.text, currentLinkView, options)

        if(tv.text == mGoalTitle)
        {
            endQuiz()
        }
        else
        {
            mTotalMoves++
            progressBar.progress = mTotalMoves
            progressView.text = "$mTotalMoves" + "/" + progressBar.max
            if(mTotalMoves > 10)
            {
                endQuiz()
            }
        }

    }

    private fun endQuiz() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(Constants.TITLE_START, mStartTitle)
        intent.putExtra(Constants.TITLE_GOAL, mGoalTitle)
        intent.putExtra(Constants.TOTAL_MOVES, mTotalMoves)
        startActivity(intent)
        finish()
    }
}