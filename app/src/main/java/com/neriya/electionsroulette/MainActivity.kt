package com.neriya.electionsroulette

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.animation.*
import android.widget.ImageView
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider.getUriForFile
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private var tries = 0
    private var throwTries = 0
    private lateinit var boxAnimation: Animation
    private lateinit var cardAnimation: Animation
    private lateinit var throwCardAnimation: Animation
    private lateinit var boxWelcomeAnimator : ObjectAnimator
    private lateinit var newCardSfx: MediaPlayer
    private lateinit var pushBoxSfx: MediaPlayer
    private lateinit var binButton: ImageView
    private lateinit var shareButton: ImageView
    private lateinit var resetButton: ImageView
//    private lateinit var pushImage: ImageView

    private lateinit var emptyCard: CardView
    private var firstMove = true
    private var welcome = true
    private val zDiff = 5
    private val z0 = 15.0F
    private val z1 = z0 - zDiff
    private val z2 = z1 - zDiff
    private lateinit var box: ViewFlipper
    private val cardStack =  ArrayDeque<CardView>()

    private var cards =mutableListOf(
        R.id.card_00,
        R.id.card_01,
        R.id.card_02,
        R.id.card_03,
        R.id.card_04,
        R.id.card_05,
        R.id.card_06,
        R.id.card_07,
        R.id.card_08,
        R.id.card_09,
        R.id.card_10,
        R.id.card_11,
        R.id.card_12,
        R.id.card_13,
        R.id.card_14,
        R.id.card_15,
        R.id.card_16,
        R.id.card_17,
        R.id.card_18,
        R.id.card_19,
        R.id.card_20,
        R.id.card_21,
        R.id.card_22,
        R.id.card_23,
        R.id.card_24,
        R.id.card_25,
        R.id.card_26,
        R.id.card_27,
        R.id.card_28,
        R.id.card_29,
    )
    private var boxSounds = arrayOf(
        R.raw.box_00_ship,
        R.raw.box_00_ship,
        R.raw.box_00_ship,
        R.raw.box_00_ship,
        R.raw.box_01_sneeze,
        R.raw.box_01_sneeze,
        R.raw.box_02_high,
        R.raw.box_03_zipper,
        R.raw.box_04_car,
        R.raw.box_05_sonar,
        R.raw.box_06_duck,
        R.raw.box_07_down,
        R.raw.box_08_santa,
        R.raw.box_08_santa,


        )
    private var binSound = arrayOf(
        R.raw.bin_00,
        R.raw.bin_00,
        R.raw.bin_00,
        R.raw.bin_01,
        R.raw.bin_03,
        R.raw.bin_04,
        R.raw.bin_05,


        )
    private lateinit var firstCardAnimationSet : AnimatorSet
    @SuppressLint("ClickableViewAccessibility", "Recycle", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_main)
        changeBox(R.id.kalpi_full)
//        box.setBackgroundResource(R.id.spin_animation)




//1392



        binButton = findViewById<ImageView>(R.id.bin)
        binButton.z = 70F
        shareButton = findViewById<ImageView>(R.id.share)
        shareButton.z = 70F
        resetButton = findViewById(R.id.reset)
//        pushImage = findViewById(R.id.touch)
        emptyCard = findViewById(R.id.empty_card)
        resetButton.setOnClickListener{ reset() }

        newCardSfx = MediaPlayer.create(this, R.raw.pop)
        throwCardAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.throw_card_animator)
        cardAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.card_animator)
        boxAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.box_animator)
        boxAnimation.setRepeatCount(5)

//        boxAnimationDrawable = box.background as AnimationDrawable

//        box.setOnTouchListener { v, event ->
//            when (event?.action) {
//
//
//                MotionEvent.ACTION_DOWN ->{
//
//                }
//
//                MotionEvent.ACTION_UP -> {
//                    pushBoxSfx.stop()
//                    pushBoxSfx.release()
//
//                    scaleDown(box)
//                }
//
//            }
//
//            v?.onTouchEvent(event) ?: true
//        }
    }
    private fun changeBox(recourceId:Int){
        if(::box.isInitialized){
            box.visibility = View.GONE
        }
        box = findViewById<ViewFlipper>(recourceId)
        box.visibility = View.VISIBLE

        box.flipInterval = 150
        box.setOnClickListener { view ->
            if(::pushBoxSfx.isInitialized) {
                pushBoxSfx.stop()
                pushBoxSfx.release()
            }

//            if (boxWelcomeAnimator.isRunning) boxWelcomeAnimator.cancel()
            if (welcome) {
                welcome = false
//                pushImage.visibility = View.GONE
            }
            pushBoxSfx = MediaPlayer.create(this, getRandomBoxSoundId())

            pushBoxSfx.start()
            scaleUp(box, boxAnimation)

        }

    }
//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//
////        if (hasFocus && welcome) {
////            val welcomeScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.8f, 1f)
////            val welcomeScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.8F, 1f)
//////            val welcomePivotY = PropertyValuesHolder.ofFloat("pivotY", box.y+box.height)
////            // and more ,welcomePivotY,welcomePivotX
////            boxWelcomeAnimator = ObjectAnimator.ofPropertyValuesHolder(pushImage, welcomeScaleX, welcomeScaleY).apply {
////                interpolator = AccelerateDecelerateInterpolator()
////                // duration of each animation
////                duration = 1500
////                // repeat infinite count (you can put n times)
////                repeatCount = 200
////                // reverse animation after finish
////                repeatMode = ValueAnimator.REVERSE
////                // start animation\
////            }
////            boxWelcomeAnimator.start()
////
////        }
//    }


    private fun scaleUp(imageView: ViewFlipper, animation: Animation){
        animation.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {

                box.startFlipping()

            }

            override fun onAnimationEnd(p0: Animation) {
                if (p0.hasEnded()){return}
                box.stopFlipping()

                tries++
                newCardSfx.start()

                addCard(getNextCard())
                if(cards.size > 25){
                    changeBox(R.id.kalpi_full)
                }
                else if(cards.size > 0){
                    changeBox(R.id.kalpi_half)
                }
                else{
                    changeBox(R.id.kalpi_empty)
                }

            }


            override fun onAnimationRepeat(p0: Animation?) {

            }
        })
        imageView.startAnimation(animation)

    }

    private fun badNewCard(card: CardView): Boolean{
        for(i in cardStack) {
            if(card == i){
                return true
            }
        }
        return false
    }
    private fun getNextCard() :CardView{
        var newCard = getRandomCard()
        var retries = 25
        while(badNewCard(newCard) && retries > 0) {
            newCard = getRandomCard()
            retries--
        }
        if(retries <= 0){
            return emptyCard
        }
        return newCard
    }
    private fun getRandomCard():CardView {
        return try{
            findViewById(cards[(cards.indices).random()])
        }
        catch(_:NoSuchElementException){
            emptyCard
        }
    }

    private fun generateRandomAngle(startAngle: Int=-1,endAngle: Int =1): Float {
        return Random.nextInt(startAngle,endAngle).toFloat()
    }
    private fun getRandomBoxSoundId(): Int {
        if(tries < 3) return R.raw.box_02_high
        return boxSounds[(boxSounds.indices).random()]
    }
    private fun getRandomBinSoundId(): Int {
        if(throwTries < 3) return R.raw.bin_00
        return binSound[(binSound.indices).random()]
    }
    private fun stackMaintenance(){
        try{
            var z = z2
            for(i in cardStack.dropLast(1)){
                i.translationZ = z
                z --
                i.animate()
                    .translationX(0F)
                    .translationY(0F)

                    .setDuration(500)
                    .start()
            }

        }

        catch (_: IndexOutOfBoundsException){}
        try {
            cardStack.last().translationZ = z1
        }
        catch (_: NoSuchElementException){}
    }
    private fun addCard(newCard: CardView){
        stackMaintenance()

        cardStack.addLast(newCard)
        if(cardStack.size >= 7){
            cardStack.removeFirst().visibility = View.INVISIBLE
        }
        cardAnimation.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {
                newCard.rotation = generateRandomAngle()
                newCard.rotationY = generateRandomAngle()
                newCard.rotationX = generateRandomAngle()
                newCard.translationZ = z0
                newCard.visibility = View.VISIBLE

            }

            @SuppressLint("ClickableViewAccessibility")
            override fun onAnimationEnd(p0: Animation?) {
                if(firstMove) {
                    firstTurn(newCard)
                    firstMove = false
                }
                newCard.setOnTouchListener(object: View.OnTouchListener {
                    private var dX: Float = 0f
                    private var dY: Float = 0f
                    private var cancel: Boolean = false

                    @SuppressLint("ClickableViewAccessibility")

                    override fun onTouch(view: View, event: MotionEvent): Boolean {

                        val newX: Float
                        val newY: Float

                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                if(firstCardAnimationSet.isRunning)firstCardAnimationSet.cancel()

                                dX = view.x - event.rawX
                                dY = view.y - event.rawY
                            }
                            MotionEvent.ACTION_MOVE -> {
                                newX = event.rawX + dX
                                newY = event.rawY + dY



                                view.animate()
                                    .x(newX)
                                    .y(newY)
                                    .setDuration(0)
                                    .start()

                            }
                            MotionEvent.ACTION_UP ->{
                                newX = event.rawX + dX
                                if(newX < -10F && view != emptyCard){
                                    showButton(binButton)
                                    throwCard(view)
                                    cancel = true
                                    return true
                                }
                                if(newX > 400F){
                                    showButton(shareButton)
                                    shareCard(view as CardView)

                                }

                                view.animate()
                                    .translationX(0F)
                                    .translationY(0F)
                                    .setDuration(300)
                                    .start()
                            }
                        }
                        return true
                    }

                }
                )
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }


        })

        newCard.startAnimation(cardAnimation)


    }

    @SuppressLint("ClickableViewAccessibility")
    private fun shareCard(card: CardView) {

        val height = card.height
        val width = card.width
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        card.draw(c)


        val fOut: FileOutputStream = openFileOutput("temp_share.jpeg", Context.MODE_PRIVATE)
        b.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
        fOut.flush()
        fOut.close()
        val file = File(filesDir, "temp_share.jpeg")
        val uri: Uri = getUriForFile(this,  BuildConfig.APPLICATION_ID + ".provider", file)

        val shareIntent = ShareCompat.IntentBuilder.from(this)
            .setType("image/jpeg") //This is the MIME type
            .setStream(uri)
            .setText(getText(R.string.share_card))
            .intent
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(shareIntent)


    }

    private fun firstTurn(card: CardView){
        firstCardAnimationSet = AnimatorSet()



        val moveLeft = ObjectAnimator.ofFloat(card, "translationX", -150f)
        val moveRight = ObjectAnimator.ofFloat(card, "translationX", 150f)
        val moveBack = ObjectAnimator.ofFloat(card, "translationX", 0f)


        firstCardAnimationSet.duration = 2000

        firstCardAnimationSet.play(moveLeft)
            .before(moveRight)
        firstCardAnimationSet.play(moveRight)
            .before(moveBack)
        firstCardAnimationSet.start()
        showButton(binButton,6000)
        showButton(shareButton,8000)

    }
    private fun reset(){
        resetButton.animate()
            .rotationBy(360F)
            .setDuration(600)
            .start()
        cardStack.clear()
        cards = mutableListOf(
            R.id.card_00,
            R.id.card_01,
            R.id.card_02,
            R.id.card_03,
            R.id.card_04,
            R.id.card_05,
            R.id.card_06,
            R.id.card_07,
            R.id.card_08,
            R.id.card_09,
            R.id.card_10,
            R.id.card_11,
            R.id.card_12,
            R.id.card_13,
            R.id.card_14,
            R.id.card_15,
            R.id.card_16,
            R.id.card_17,
            R.id.card_18,
            R.id.card_19,
            R.id.card_20,
            R.id.card_21,
            R.id.card_22,
            R.id.card_23,
            R.id.card_24,
            R.id.card_25,
            R.id.card_26,
            R.id.card_27,
            R.id.card_28,
            R.id.card_29,
        )
        changeBox(R.id.kalpi_full)
        resetCard(emptyCard)
        for(cardId in cards){
            resetCard(findViewById(cardId))
        }
        showButton(binButton, 2500)
        showButton(shareButton,2500)
    }
    private fun showButton(button: View, time: Long=1500){
        button.alpha = 1.0F
        button.animate()
            .alpha(0F)
            .setDuration(time)
            .start()
    }
    private fun resetCard(card: CardView){
        card.animate().withEndAction {
            card.visibility = View.INVISIBLE
            card.scaleX = 1.0F
            card.scaleY = 1.0F
            card.translationX = 0.0F
            card.translationY = 0.0F
        }
            .scaleY(0F)
            .scaleX(0F)
            .y(1200F)
            .x(200f)
            .setDuration(300)
            .start()


    }
    private fun throwCard(card: View) {
        if(!cards.contains(card.id)){
            return
        }
        throwTries++
        cards.remove(card.id)
        cardStack.remove(card)
        MediaPlayer.create(this, getRandomBinSoundId()).start()
        card.animate()
            .scaleY(0.3F)
            .scaleX(0.3F)
            .translationX((-card.width).toFloat())
            .setDuration(200)
            .withEndAction{card.visibility = View.GONE}
            .start()

        if(resetButton.alpha != 1.0F && cards.size < 15){
            resetButton.animate()
                .alpha(1.0F)
                .setDuration(350)
                .start()
        }
    }

}