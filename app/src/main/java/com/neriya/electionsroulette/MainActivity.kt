package com.neriya.electionsroulette

import android.animation.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.*
import android.widget.ImageView
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider.getUriForFile
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs
import kotlin.random.Random
val STATE = "state"
val ROTATION = "rotation"

val STATE_NEW = 0

val STATE_FRONT = 1
val STATE_BACK = 2
val BACK_SUFFIX = "_back"
class MainActivity : AppCompatActivity() {
    private var tries = 0
    private var throwTries = 0
    private lateinit var boxAnimation: Animation
    private lateinit var cardAnimation: Animation
    private lateinit var throwCardAnimation: Animation
    private lateinit var newCardSfx: MediaPlayer
    private lateinit var shareCardSfx: MediaPlayer
    private lateinit var pushBoxSfx: MediaPlayer
    private lateinit var binButton: ImageView
    private lateinit var shareButton: ImageView
    private lateinit var resetButton: ImageView

    private var firstMove = true
    private var welcome = true
    private val zDiff = 5
    private val z0 = 15.0F
    private val z1 = z0 - zDiff
    private val z2 = z1 - zDiff
    private lateinit var box: ViewFlipper
    private val cardStack =  ArrayDeque<CardView>()
    private var cards = mutableListOf(
        R.id.card_empty,
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
        R.id.card_30,
        R.id.card_31,
        R.id.card_32,
        R.id.card_33,
        R.id.card_34,
        R.id.card_35,
        R.id.card_36,
        R.id.card_37,
        R.id.card_38,
        R.id.card_39,
    )
    var cardStates = cards.associateWithTo(mutableMapOf()) {mutableMapOf(STATE to STATE_NEW, ROTATION to 1) }
    private var boxSounds = arrayOf(
        R.raw.box_00_ship,
        R.raw.box_00_ship,
        R.raw.box_00_ship,
        R.raw.box_01_sneeze,
        R.raw.box_02_high,
        R.raw.box_03_zipper,
        R.raw.box_04_car,
        R.raw.box_05_sonar,
        R.raw.box_06_duck,
        R.raw.box_07_down,
        R.raw.box_08_santa,
        R.raw.box_08_santa,
        R.raw.box_09_horn,
        R.raw.box_10_horse,
        R.raw.box_11_dog,
        R.raw.box_12_sword,
        R.raw.box_13_winter,
        R.raw.box_14_woosh,
        R.raw.box_15_oi,
        R.raw.box_16_quarter,


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




        binButton = findViewById<ImageView>(R.id.bin)
        binButton.z = 70F
        shareButton = findViewById<ImageView>(R.id.share)
        shareButton.z = 70F
        resetButton = findViewById(R.id.reset)

        resetButton.setOnClickListener { reset() }

        newCardSfx = MediaPlayer.create(this, R.raw.pop)
        shareCardSfx = MediaPlayer.create(this, R.raw.share_00)


        throwCardAnimation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.throw_card_animator)
        cardAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.card_animator)
        boxAnimation = AnimationUtils.loadAnimation(applicationContext, R.anim.box_animator)
        boxAnimation.setRepeatCount(5)
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

            if (welcome) {
                welcome = false
            }
            pushBoxSfx = MediaPlayer.create(this, getRandomBoxSoundId())

            pushBoxSfx.start()
            scaleUp(box, boxAnimation)

        }

    }

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

                addCard(getNextCard(applicationContext))
                if(cards.size > 25){
                    changeBox(R.id.kalpi_full)
                }
                else if(cards.size > 1){
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
    private fun getNextCard(context: Context) :CardView{
        var newCard = getRandomCard()
        var retries = 25
        while(badNewCard(newCard) && retries > 0) {
            newCard = getRandomCard()
            retries--
        }
        newCard.setBackgroundResource(
            resources.getIdentifier(resources.getResourceEntryName(newCard.id), "drawable", context.packageName)
        )
        newCard.scaleX = 1F
        cardStates[newCard.id]?.set(STATE, STATE_NEW)
        if(cardStates[newCard.id]?.get(ROTATION)!! == -1){
            newCard.rotation = -newCard.rotation
            newCard.rotationX = -newCard.rotationX
            newCard.rotationY = -newCard.rotationY
            cardStates[newCard.id]?.set(ROTATION, 1)
        }


        return newCard
    }
    private fun getRandomCard():CardView {
        return findViewById(cards[(cards.indices).random()])

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
                    private var click = false

                    private var originalX = newCard.x
                    private var originalY = newCard.y


                    @SuppressLint("ClickableViewAccessibility")

                    override fun onTouch(view: View, event: MotionEvent): Boolean {

                        val newX: Float
                        val newY: Float

                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                if(firstCardAnimationSet.isRunning)firstCardAnimationSet.cancel()
                                click = true
                                originalX = event.rawX
                                originalY = event.rawY
                                dX = view.x - event.rawX
                                dY = view.y - event.rawY
                            }
                            MotionEvent.ACTION_MOVE -> {

                                if(abs(originalY - event.rawY) < 5 && abs(originalX - event.rawX) < 5){
                                    click = true
                                    return true
                                }
                                click = false
                                newX = event.rawX + dX
                                newY = event.rawY + dY



                                view.animate()
                                    .x(newX)
                                    .y(newY)
                                    .setDuration(0)
                                    .start()

                            }
                            MotionEvent.ACTION_UP ->{
                                if(click){
                                    Log.d("click", "true")
                                    flipCard(applicationContext,view as CardView)


                                    return true
                                }
                                Log.d("click", "false")

                                newX = event.rawX + dX
                                if(newX < -10F){
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

        shareCardSfx.start()
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
            R.id.card_empty,
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
            R.id.card_30,
            R.id.card_31,
            R.id.card_32,
            R.id.card_33,
            R.id.card_34,
            R.id.card_35,
            R.id.card_36,
            R.id.card_37,
            R.id.card_38,
            R.id.card_39,
        )
        changeBox(R.id.kalpi_full)
        for(cardId in cards){
            resetCard(findViewById(cardId))
        }
        showButton(binButton, 2500)
        showButton(shareButton,2500)
        cardStates = cards.associateWithTo(mutableMapOf()) {mutableMapOf(STATE to STATE_NEW, ROTATION to 1) }

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
        if(resetButton.alpha != 1.0F && cards.size < 25){
            resetButton.animate()
                .alpha(1.0F)
                .setDuration(350)
                .start()
        }
        if(card.id == R.id.card_empty) {
            resetCard(card as CardView)
            return
        }
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



    }
    fun flipCard(context: Context, visibleView: CardView) {
        var resourceName = resources.getResourceEntryName(visibleView.id)
        val cardState = cardStates[visibleView.id]?.get(STATE)
        if(cardState == STATE_FRONT || cardState == STATE_NEW){
            resourceName += BACK_SUFFIX

            Log.d("resourceName", resourceName)
            cardStates[visibleView.id]?.set(STATE, STATE_BACK)
        }
        else{
            Log.d("resourceName", resourceName)

            cardStates[visibleView.id]?.set(STATE, STATE_FRONT)
        }

        val scale = applicationContext.resources.displayMetrics.density
        visibleView.scaleX = 1F
        cardStates[visibleView.id]?.set(ROTATION, cardStates[visibleView.id]?.get(ROTATION)!! * -1)

        visibleView.cameraDistance = 8000 * scale
        if(cardState != STATE_NEW) {
            visibleView.rotation = -visibleView.rotation
            visibleView.rotationY = -visibleView.rotationY
            visibleView.rotationX = -visibleView.rotationX
        }

        val flipOutAnimatorSet =
            AnimatorInflater.loadAnimator(
                context,
                R.animator.flip_out
            ) as AnimatorSet
        val flipInAnimationSet =
            AnimatorInflater.loadAnimator(
                context,
                R.animator.flip_in
            ) as AnimatorSet
        flipInAnimationSet.setTarget(visibleView)

        flipInAnimationSet.start()
        flipInAnimationSet.doOnEnd {
            visibleView.setBackgroundResource(
                resources.getIdentifier(resourceName, "drawable", context.packageName)
            )
            visibleView.scaleX = -1F
            visibleView.rotation = -visibleView.rotation
            visibleView.rotationX = -visibleView.rotationX
            visibleView.rotationY = -visibleView.rotationY
            flipOutAnimatorSet.setTarget(visibleView)
            flipOutAnimatorSet.start()
        }
    }
}